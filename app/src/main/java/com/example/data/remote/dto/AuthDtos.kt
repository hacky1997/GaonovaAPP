package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestOtpRequestDto(
    @Json(name = "phone") val phone: String,
    @Json(name = "channel") val channel: String = "SMS"
)

@JsonClass(generateAdapter = true)
data class RequestOtpResponseDto(
    @Json(name = "requestId") val requestId: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "expiresInSeconds") val expiresInSeconds: Int = 300,
    @Json(name = "resendAvailableInSeconds") val resendAvailableInSeconds: Int = 45,
    @Json(name = "message") val message: String = "OTP sent successfully to registered mobile number."
)

@JsonClass(generateAdapter = true)
data class VerifyOtpRequestDto(
    @Json(name = "requestId") val requestId: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "otpCode") val otpCode: String,
    @Json(name = "deviceId") val deviceId: String,
    @Json(name = "deviceName") val deviceName: String = "Android Device",
    @Json(name = "platform") val platform: String = "ANDROID"
)

@JsonClass(generateAdapter = true)
data class EmailLoginRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "deviceId") val deviceId: String,
    @Json(name = "deviceName") val deviceName: String = "Android Device",
    @Json(name = "platform") val platform: String = "ANDROID"
)

@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    @Json(name = "name") val name: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "location") val location: String? = null,
    @Json(name = "preferredLanguage") val preferredLanguage: String = "en",
    @Json(name = "deviceId") val deviceId: String,
    @Json(name = "deviceName") val deviceName: String = "Android Device",
    @Json(name = "platform") val platform: String = "ANDROID"
)

@JsonClass(generateAdapter = true)
data class RefreshTokenRequestDto(
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "deviceId") val deviceId: String
)

@JsonClass(generateAdapter = true)
data class AuthTokensDto(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "tokenType") val tokenType: String = "Bearer",
    @Json(name = "expiresInSeconds") val expiresInSeconds: Long = 3600L
)

@JsonClass(generateAdapter = true)
data class UserSessionDto(
    @Json(name = "sessionId") val sessionId: String,
    @Json(name = "userId") val userId: String,
    @Json(name = "deviceId") val deviceId: String,
    @Json(name = "deviceName") val deviceName: String,
    @Json(name = "platform") val platform: String = "ANDROID",
    @Json(name = "ipAddress") val ipAddress: String = "127.0.0.1",
    @Json(name = "lastActiveAt") val lastActiveAt: Long = System.currentTimeMillis(),
    @Json(name = "createdAt") val createdAt: Long = System.currentTimeMillis(),
    @Json(name = "expiresAt") val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000L),
    @Json(name = "isCurrentDevice") val isCurrentDevice: Boolean = true
)

@JsonClass(generateAdapter = true)
data class UserAddressDto(
    @Json(name = "id") val id: String,
    @Json(name = "tag") val tag: String = "Home",
    @Json(name = "fullName") val fullName: String,
    @Json(name = "street") val street: String,
    @Json(name = "city") val city: String,
    @Json(name = "state") val state: String,
    @Json(name = "postalCode") val postalCode: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "isDefault") val isDefault: Boolean = false
)

@JsonClass(generateAdapter = true)
data class UserProfileDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String?,
    @Json(name = "phone") val phone: String,
    @Json(name = "role") val role: String = "Patron Member",
    @Json(name = "memberSince") val memberSince: String = "2026",
    @Json(name = "avatarUrl") val avatarUrl: String? = null,
    @Json(name = "avatarInitials") val avatarInitials: String = "GN",
    @Json(name = "preferredLanguage") val preferredLanguage: String = "English (India)",
    @Json(name = "notificationPreferences") val notificationPreferences: Map<String, Boolean> = emptyMap(),
    @Json(name = "discoveryLocation") val discoveryLocation: String = "Kolkata, West Bengal",
    @Json(name = "accountStatus") val accountStatus: String = "ACTIVE",
    @Json(name = "karmaCoins") val karmaCoins: Int = 100,
    @Json(name = "walletBalance") val walletBalance: Double = 500.0,
    @Json(name = "addresses") val addresses: List<UserAddressDto> = emptyList(),
    @Json(name = "createdAt") val createdAt: Long = System.currentTimeMillis(),
    @Json(name = "lastLoginAt") val lastLoginAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "tokens") val tokens: AuthTokensDto,
    @Json(name = "session") val session: UserSessionDto,
    @Json(name = "user") val user: UserProfileDto
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequestDto(
    @Json(name = "name") val name: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "preferredLanguage") val preferredLanguage: String? = null,
    @Json(name = "discoveryLocation") val discoveryLocation: String? = null,
    @Json(name = "notificationPreferences") val notificationPreferences: Map<String, Boolean>? = null,
    @Json(name = "addresses") val addresses: List<UserAddressDto>? = null
)

@JsonClass(generateAdapter = true)
data class DeleteAccountRequestDto(
    @Json(name = "reason") val reason: String? = null,
    @Json(name = "confirmationPhrase") val confirmationPhrase: String = "DELETE"
)

object AuthErrorCodes {
    const val INVALID_PHONE = "INVALID_PHONE"
    const val OTP_EXPIRED = "OTP_EXPIRED"
    const val OTP_INVALID = "OTP_INVALID"
    const val OTP_RATE_LIMITED = "OTP_RATE_LIMITED"
    const val TOO_MANY_ATTEMPTS = "TOO_MANY_ATTEMPTS"
    const val SESSION_EXPIRED = "SESSION_EXPIRED"
    const val SESSION_REVOKED = "SESSION_REVOKED"
    const val AUTH_REQUIRED = "AUTH_REQUIRED"
    const val ACCOUNT_DISABLED = "ACCOUNT_DISABLED"
    const val ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND"
    const val USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS"
    const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    const val SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"
}
