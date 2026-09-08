package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserSessionEntity
import com.example.data.models.AuthState
import com.example.data.models.DemoUsers
import com.example.data.models.ShippingAddress
import com.example.data.models.UserProfile
import com.example.data.remote.api.ApiClient
import com.example.data.remote.api.GaonovaApiService
import com.example.data.remote.dto.*
import com.example.data.security.SecureSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val code: String, val message: String) : AuthResult<Nothing>()
}

class AuthRepository(
    private val database: AppDatabase,
    private val secureStorage: SecureSessionStorage,
    private val apiService: GaonovaApiService = ApiClient.apiService
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _activeUser = MutableStateFlow<UserProfile?>(null)
    val activeUser: StateFlow<UserProfile?> = _activeUser.asStateFlow()

    private val _authState = MutableStateFlow(
        AuthState(
            isLoggedIn = false,
            currentUser = null,
            isGuest = true,
            pendingOtpPhone = null,
            generatedOtpCode = null
        )
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _userSessions = MutableStateFlow<List<UserSessionDto>>(emptyList())
    val userSessions: StateFlow<List<UserSessionDto>> = _userSessions.asStateFlow()

    val allRegisteredUsers: Flow<List<UserProfile>> = database.userDao().getAllUsers().map { list ->
        list.map { mapEntityToUserProfile(it) }
    }

    init {
        // Observe Room DB active user changes
        repositoryScope.launch {
            database.userDao().getActiveUser().collect { entity ->
                if (entity != null) {
                    val profile = mapEntityToUserProfile(entity)
                    _activeUser.value = profile
                    _authState.value = _authState.value.copy(
                        isLoggedIn = true,
                        currentUser = profile,
                        isGuest = false
                    )
                } else {
                    _activeUser.value = null
                    _authState.value = _authState.value.copy(
                        isLoggedIn = false,
                        currentUser = null,
                        isGuest = true
                    )
                }
            }
        }
    }

    /**
     * Attempts to seamlessly restore active session from hardware-encrypted storage.
     */
    suspend fun restoreSessionOnAppLaunch(): Boolean {
        val storedSession = secureStorage.loadSession() ?: return false
        val authHeader = "Bearer ${storedSession.accessToken}"

        try {
            // Check if token is expired and refresh if necessary
            if (secureStorage.isAccessTokenExpired()) {
                val refreshReq = RefreshTokenRequestDto(
                    refreshToken = storedSession.refreshToken,
                    deviceId = storedSession.deviceId
                )
                val refreshResp = apiService.refreshToken(refreshReq)
                if (refreshResp.isSuccessful && refreshResp.body()?.data != null) {
                    val tokens = refreshResp.body()!!.data!!
                    secureStorage.saveSession(
                        accessToken = tokens.accessToken,
                        refreshToken = tokens.refreshToken,
                        sessionId = storedSession.sessionId,
                        userId = storedSession.userId,
                        expiresInSeconds = tokens.expiresInSeconds
                    )
                } else {
                    // Token expired permanently
                    secureStorage.clearSession()
                    database.userDao().clearActiveSessions()
                    return false
                }
            }

            // Fetch latest user profile from server
            val meResp = apiService.getCurrentUser("Bearer ${secureStorage.getAccessToken() ?: storedSession.accessToken}")
            if (meResp.isSuccessful && meResp.body()?.data != null) {
                val userDto = meResp.body()!!.data!!
                val profile = mapDtoToUserProfile(userDto)
                persistUserLocally(profile, makeActive = true)
                refreshUserSessions()
                return true
            } else {
                // Fallback to locally cached user
                val local = database.userDao().getUserById(storedSession.userId)
                if (local != null) {
                    database.userDao().setActiveSession(local.userId)
                    return true
                }
            }
        } catch (e: Exception) {
            // Offline fallback
            val local = database.userDao().getUserById(storedSession.userId)
            if (local != null) {
                database.userDao().setActiveSession(local.userId)
                return true
            }
        }
        return false
    }

    suspend fun requestOtp(phone: String): AuthResult<RequestOtpResponseDto> {
        return try {
            val cleanPhone = if (phone.startsWith("+91")) phone else "+91 $phone"
            val resp = apiService.requestOtp(RequestOtpRequestDto(phone = cleanPhone))
            if (resp.isSuccessful && resp.body()?.data != null) {
                val data = resp.body()!!.data!!
                _authState.value = _authState.value.copy(
                    pendingOtpPhone = cleanPhone,
                    generatedOtpCode = null // Strictly server-managed; no fake OTP exposure
                )
                AuthResult.Success(data)
            } else {
                val err = resp.body()?.error
                AuthResult.Error(
                    code = err?.code ?: AuthErrorCodes.SERVICE_UNAVAILABLE,
                    message = err?.message ?: "Unable to send verification OTP. Please try again."
                )
            }
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorCodes.SERVICE_UNAVAILABLE, e.localizedMessage ?: "Network error.")
        }
    }

    suspend fun verifyOtp(
        requestId: String,
        phone: String,
        otpCode: String
    ): AuthResult<UserProfile> {
        return try {
            val deviceId = secureStorage.getOrCreateDeviceId()
            val req = VerifyOtpRequestDto(
                requestId = requestId,
                phone = phone,
                otpCode = otpCode,
                deviceId = deviceId,
                deviceName = "Android Device"
            )
            val resp = apiService.verifyOtp(req)
            if (resp.isSuccessful && resp.body()?.data != null) {
                val authData = resp.body()!!.data!!
                handleSuccessfulAuth(authData)
                val profile = mapDtoToUserProfile(authData.user)
                AuthResult.Success(profile)
            } else {
                val err = resp.body()?.error
                AuthResult.Error(
                    code = err?.code ?: AuthErrorCodes.OTP_INVALID,
                    message = err?.message ?: "Invalid or expired OTP. Please verify."
                )
            }
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorCodes.SERVICE_UNAVAILABLE, e.localizedMessage ?: "Verification failed.")
        }
    }

    suspend fun loginWithEmail(email: String, pass: String): AuthResult<UserProfile> {
        return try {
            val deviceId = secureStorage.getOrCreateDeviceId()
            val req = EmailLoginRequestDto(
                email = email,
                password = pass,
                deviceId = deviceId
            )
            val resp = apiService.loginWithEmail(req)
            if (resp.isSuccessful && resp.body()?.data != null) {
                val authData = resp.body()!!.data!!
                handleSuccessfulAuth(authData)
                val profile = mapDtoToUserProfile(authData.user)
                AuthResult.Success(profile)
            } else {
                val err = resp.body()?.error
                AuthResult.Error(
                    code = err?.code ?: AuthErrorCodes.INVALID_CREDENTIALS,
                    message = err?.message ?: "Invalid email or password."
                )
            }
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorCodes.SERVICE_UNAVAILABLE, e.localizedMessage ?: "Sign in failed.")
        }
    }

    suspend fun register(
        name: String,
        email: String?,
        phone: String,
        location: String?
    ): AuthResult<UserProfile> {
        return try {
            val deviceId = secureStorage.getOrCreateDeviceId()
            val req = RegisterRequestDto(
                name = name,
                email = email,
                phone = phone,
                location = location,
                deviceId = deviceId
            )
            val resp = apiService.registerPatron(req)
            if (resp.isSuccessful && resp.body()?.data != null) {
                val authData = resp.body()!!.data!!
                handleSuccessfulAuth(authData)
                val profile = mapDtoToUserProfile(authData.user)
                AuthResult.Success(profile)
            } else {
                val err = resp.body()?.error
                AuthResult.Error(
                    code = err?.code ?: AuthErrorCodes.USER_ALREADY_EXISTS,
                    message = err?.message ?: "Account registration failed."
                )
            }
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorCodes.SERVICE_UNAVAILABLE, e.localizedMessage ?: "Registration failed.")
        }
    }

    suspend fun switchAccount(targetUser: UserProfile) {
        database.userDao().clearActiveSessions()
        database.userDao().setActiveSession(targetUser.id)
        secureStorage.saveSession(
            accessToken = "gnv_jwt_${targetUser.id}_${UUID.randomUUID().toString().take(8)}",
            refreshToken = "gnv_rfr_${targetUser.id}_${UUID.randomUUID().toString().take(8)}",
            sessionId = "ses_${UUID.randomUUID().toString().take(8)}",
            userId = targetUser.id,
            expiresInSeconds = 3600L
        )
        refreshUserSessions()
    }

    suspend fun logout(): Boolean {
        try {
            val token = secureStorage.getAccessToken()
            if (token != null) {
                apiService.logout("Bearer $token")
            }
        } catch (e: Exception) {
            // Ignore network errors on logout
        } finally {
            secureStorage.clearSession()
            database.userDao().clearActiveSessions()
            _activeUser.value = null
            _authState.value = AuthState(
                isLoggedIn = false,
                currentUser = null,
                isGuest = true,
                pendingOtpPhone = null,
                generatedOtpCode = null
            )
            _userSessions.value = emptyList()
        }
        return true
    }

    suspend fun deleteAccount(confirmationPhrase: String = "DELETE"): AuthResult<Boolean> {
        val currentUser = _activeUser.value
        val userId = currentUser?.id ?: secureStorage.getActiveUserId()

        return try {
            val token = secureStorage.getAccessToken()
            val req = DeleteAccountRequestDto(
                reason = "User requested account deletion",
                confirmationPhrase = confirmationPhrase
            )
            apiService.deleteUserAccount(req, "Bearer $token")

            // Purge all isolated user data locally
            if (userId != null) {
                database.cartDao().purgeUserData(userId)
                database.wishlistDao().purgeUserData(userId)
                database.priceAlertDao().purgeUserData(userId)
                database.recentViewDao().purgeUserData(userId)
                database.orderDao().purgeUserData(userId)
                database.supportDao().purgeUserData(userId)
                database.mittiDao().purgeUserData(userId)
                database.userSessionDao().purgeUserData(userId)
                database.userDao().markUserDeleted(userId)
            }

            secureStorage.clearSession()
            database.userDao().clearActiveSessions()
            _activeUser.value = null
            _authState.value = AuthState(
                isLoggedIn = false,
                currentUser = null,
                isGuest = true
            )
            _userSessions.value = emptyList()
            AuthResult.Success(true)
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorCodes.SERVICE_UNAVAILABLE, e.localizedMessage ?: "Failed to delete account.")
        }
    }

    suspend fun updateProfile(
        name: String? = null,
        email: String? = null,
        location: String? = null,
        language: String? = null
    ): AuthResult<UserProfile> {
        val token = secureStorage.getAccessToken() ?: return AuthResult.Error(AuthErrorCodes.AUTH_REQUIRED, "Sign in required.")
        return try {
            val req = UpdateProfileRequestDto(
                name = name,
                email = email,
                discoveryLocation = location,
                preferredLanguage = language
            )
            val resp = apiService.updateCurrentUserProfile(req, "Bearer $token")
            if (resp.isSuccessful && resp.body()?.data != null) {
                val updated = mapDtoToUserProfile(resp.body()!!.data!!)
                persistUserLocally(updated, makeActive = true)
                AuthResult.Success(updated)
            } else {
                AuthResult.Error(AuthErrorCodes.SERVICE_UNAVAILABLE, "Unable to update profile.")
            }
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorCodes.SERVICE_UNAVAILABLE, e.localizedMessage ?: "Update failed.")
        }
    }

    suspend fun refreshUserSessions(): List<UserSessionDto> {
        val token = secureStorage.getAccessToken() ?: return emptyList()
        return try {
            val resp = apiService.getUserSessions("Bearer $token")
            if (resp.isSuccessful && resp.body()?.data != null) {
                val list = resp.body()!!.data!!
                _userSessions.value = list
                list
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun revokeSession(sessionId: String): Boolean {
        val token = secureStorage.getAccessToken() ?: return false
        return try {
            val resp = apiService.revokeUserSession(sessionId, "Bearer $token")
            if (resp.isSuccessful) {
                _userSessions.value = _userSessions.value.filter { it.sessionId != sessionId }
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun handleSuccessfulAuth(authData: AuthResponseDto) {
        secureStorage.saveSession(
            accessToken = authData.tokens.accessToken,
            refreshToken = authData.tokens.refreshToken,
            sessionId = authData.session.sessionId,
            userId = authData.user.id,
            expiresInSeconds = authData.tokens.expiresInSeconds
        )

        val profile = mapDtoToUserProfile(authData.user)
        persistUserLocally(profile, makeActive = true)
        refreshUserSessions()
    }

    private suspend fun persistUserLocally(user: UserProfile, makeActive: Boolean) {
        if (makeActive) {
            database.userDao().clearActiveSessions()
        }
        database.userDao().insertUser(
            UserEntity(
                userId = user.id,
                name = user.name,
                email = user.email,
                phone = user.phone,
                role = user.role,
                memberSince = user.memberSince,
                location = user.location,
                avatarInitials = user.avatarInitials,
                karmaCoins = user.karmaCoins,
                walletBalance = user.walletBalance,
                addressJson = serializeAddresses(user.addresses),
                preferredLanguage = "English (India)",
                accountStatus = "ACTIVE",
                isActiveSession = makeActive,
                lastLoginTimestamp = System.currentTimeMillis()
            )
        )
    }

    private fun mapDtoToUserProfile(dto: UserProfileDto): UserProfile {
        return UserProfile(
            id = dto.id,
            name = dto.name,
            email = dto.email ?: "${dto.id}@gaonova.com",
            phone = dto.phone,
            role = dto.role,
            memberSince = dto.memberSince,
            location = dto.discoveryLocation,
            avatarInitials = dto.avatarInitials,
            karmaCoins = dto.karmaCoins,
            walletBalance = dto.walletBalance,
            addresses = dto.addresses.map {
                ShippingAddress(
                    id = it.id,
                    tag = it.tag,
                    fullName = it.fullName,
                    street = it.street,
                    city = it.city,
                    state = it.state,
                    postalCode = it.postalCode,
                    phone = it.phone,
                    isDefault = it.isDefault
                )
            }.ifEmpty { listOf(ShippingAddress(fullName = dto.name, phone = dto.phone)) }
        )
    }

    private fun mapEntityToUserProfile(entity: UserEntity): UserProfile {
        val parsedAddresses = deserializeAddresses(entity.addressJson)
        return UserProfile(
            id = entity.userId,
            name = entity.name,
            email = entity.email,
            phone = entity.phone,
            role = entity.role,
            memberSince = entity.memberSince,
            location = entity.location,
            avatarInitials = entity.avatarInitials,
            karmaCoins = entity.karmaCoins,
            walletBalance = entity.walletBalance,
            addresses = if (parsedAddresses.isEmpty()) listOf(ShippingAddress(fullName = entity.name, phone = entity.phone)) else parsedAddresses
        )
    }

    private fun serializeAddresses(addresses: List<ShippingAddress>): String {
        return addresses.joinToString(";;;") {
            "${it.id}::${it.tag}::${it.fullName}::${it.street}::${it.city}::${it.state}::${it.postalCode}::${it.phone}::${it.isDefault}"
        }
    }

    private fun deserializeAddresses(raw: String): List<ShippingAddress> {
        if (raw.isBlank()) return emptyList()
        return raw.split(";;;").mapNotNull { part ->
            val p = part.split("::")
            if (p.size >= 8) {
                ShippingAddress(
                    id = p[0],
                    tag = p[1],
                    fullName = p[2],
                    street = p[3],
                    city = p[4],
                    state = p[5],
                    postalCode = p[6],
                    phone = p[7],
                    isDefault = p.getOrNull(8)?.toBooleanStrictOrNull() ?: false
                )
            } else null
        }
    }
}
