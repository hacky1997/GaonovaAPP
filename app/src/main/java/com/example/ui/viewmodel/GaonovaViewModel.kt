package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiResponseResult
import com.example.data.ai.GaonovaAiService
import com.example.data.local.AppDatabase
import com.example.data.location.*
import com.example.data.models.*
import com.example.data.models.production.*
import com.example.data.repository.CommerceRepository
import com.example.data.repository.ProductRepository
import com.example.data.repository.ProductionCatalogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class ScreenDestination {
    object Home : ScreenDestination()
    object Explore : ScreenDestination()
    object Mitti : ScreenDestination()
    object Account : ScreenDestination()
    object Cart : ScreenDestination()
    object Gifting : ScreenDestination()
    object OrdersAlerts : ScreenDestination()
    object Wallet : ScreenDestination()
    data class ProductDetail(val productId: String) : ScreenDestination()
    data class ArtisanDetail(val artisanId: String) : ScreenDestination()

    // Backward compatibility alias
    companion object {
        val AiSage: ScreenDestination = Mitti
    }
}

data class UiNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val message: String,
    val isSuccess: Boolean = true
)

class GaonovaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    val secureStorage = com.example.data.security.SecureSessionStorage(application)
    val authRepository = com.example.data.repository.AuthRepository(database, secureStorage)
    private val commerceRepo = CommerceRepository(database, authRepository)
    val productionCatalogRepo = ProductionCatalogRepository(database)

    // Backend-driven production state flows
    val productionProducts: StateFlow<List<ProductionProduct>> = productionCatalogRepo.productionProducts
    val productionCategories: StateFlow<List<CategoryDomain>> = productionCatalogRepo.productionCategories
    val productionArtisans: StateFlow<List<ArtisanDomain>> = productionCatalogRepo.productionArtisans
    val homeFeed: StateFlow<HomeFeedDomain?> = productionCatalogRepo.homeFeed

    // Location & First Launch Infrastructure
    val locationPrefs = LocationPreferences(application)
    val locationService = LocationService(application)

    private val _locationContext = MutableStateFlow(locationPrefs.loadCachedLocation())
    val locationContext = _locationContext.asStateFlow()

    private val _locationStatus = MutableStateFlow<LocationStatus>(
        if (locationPrefs.isFirstLaunchCompleted) LocationStatus.Idle else LocationStatus.PermissionRationaleRequired
    )
    val locationStatus = _locationStatus.asStateFlow()

    val isFirstLaunchWelcomeVisible = MutableStateFlow(!locationPrefs.isFirstLaunchCompleted)

    // Daily Welcome Moment
    val isDailyWelcomeGreetingActive = MutableStateFlow(false)
    val dailyWelcomeGreetingText = MutableStateFlow("")

    // Navigation Backstack State
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Home)
    val currentScreen = _currentScreen.asStateFlow()

    private val screenBackstack = mutableListOf<ScreenDestination>()

    // Global Search & Filters
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedStateFilter = MutableStateFlow("All India")
    val isGiOnlyFilter = MutableStateFlow(false)
    val isPriceDropOnly = MutableStateFlow(false)

    // Search Modals (Speech / Voice & Google Lens / Image Scan)
    val isVoiceSearchActive = MutableStateFlow(false)
    val isLensSearchActive = MutableStateFlow(false)

    // Product of the day & Daily Welcome
    val isDailyWelcomeDismissed = MutableStateFlow(false)

    // Filtered Products
    val filteredProducts: StateFlow<List<Product>> = combine(
        searchQuery,
        selectedCategory,
        selectedStateFilter,
        isGiOnlyFilter,
        isPriceDropOnly
    ) { query, category, state, giOnly, priceDropOnly ->
        ProductRepository.searchProducts(
            query = query,
            category = category,
            state = if (state == "All India") null else state,
            onlyGi = giOnly,
            onlyPriceDrops = priceDropOnly
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductRepository.sampleProducts)

    // Commerce States from Room
    val cartItems: StateFlow<List<CartItem>> = commerceRepo.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistIds: StateFlow<Set<String>> = commerceRepo.wishlistIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val priceAlerts: StateFlow<List<PriceAlert>> = commerceRepo.priceAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = commerceRepo.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportTickets: StateFlow<List<SupportTicket>> = commerceRepo.supportTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Swadeshi Artisan Patron Wallet State
    val walletState = MutableStateFlow(WalletState())

    // User Authentication & Session State from AuthRepository
    val authState: StateFlow<AuthState> = authRepository.authState
    val activeUser: StateFlow<UserProfile?> = authRepository.activeUser
    val userSessions: StateFlow<List<com.example.data.remote.dto.UserSessionDto>> = authRepository.userSessions
    val allRegisteredUsers: StateFlow<List<UserProfile>> = authRepository.allRegisteredUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DemoUsers.allDemoUsers)

    // OTP & Auth Interactive State
    val otpRequestId = MutableStateFlow<String?>(null)
    val otpResendCountdown = MutableStateFlow(0)
    val isOtpLoading = MutableStateFlow(false)
    val authError = MutableStateFlow<String?>(null)

    // Auth UI Dialogs / Sheets
    val isLoginModalVisible = MutableStateFlow(false)
    val isRegisterModalVisible = MutableStateFlow(false)
    val isEditProfileModalVisible = MutableStateFlow(false)
    val isAddAddressModalVisible = MutableStateFlow(false)
    val isLogoutConfirmDialogVisible = MutableStateFlow(false)
    val isDeleteAccountDialogVisible = MutableStateFlow(false)
    val isSessionsModalVisible = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            try {
                productionCatalogRepo.refreshHomeFeed()
            } catch (e: Exception) {
                // Ignore gracefully
            }
            try {
                commerceRepo.initializeDefaultUsersIfNeeded()
            } catch (e: Exception) {
                // Ignore gracefully
            }
            try {
                // Seamless Session Restoration on App Launch
                val restored = authRepository.restoreSessionOnAppLaunch()
                if (!restored) {
                    val defaultUser = DemoUsers.sayakNaskar
                    authRepository.switchAccount(defaultUser)
                }
            } catch (e: Exception) {
                // Fallback gracefully
                authRepository.switchAccount(DemoUsers.sayakNaskar)
            }
        }

        viewModelScope.launch {
            authRepository.activeUser.collect { user ->
                try {
                    if (user != null) {
                        walletState.value = WalletState(
                            balance = user.walletBalance,
                            karmaCoins = user.karmaCoins,
                            transactions = getTransactionsForUser(user.id)
                        )
                    } else {
                        walletState.value = WalletState(
                            balance = 0.0,
                            karmaCoins = 0,
                            transactions = emptyList()
                        )
                    }
                } catch (e: Exception) {
                    // Ignore gracefully
                }
            }
        }
    }

    // Mitti Assistant State
    val aiQueryText = MutableStateFlow("")
    val isAiThinking = MutableStateFlow(false)
    val aiChatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome_mitti",
                sender = MessageSender.MITTI,
                text = "Hi, I'm Mitti. What would you like to discover?\n\nI can help you explore India's village crafts, trace regional geographical origins, verify materials, or find thoughtful gifts.",
                timestamp = "Just now",
                suggestedActions = listOf(
                    "What makes Nakshi Kantha unique?",
                    "Where is Jaipur Blue Pottery crafted?",
                    "Find gifts under ₹2,000",
                    "How to verify pure Pashmina?"
                ),
                isConfidenceDisclosed = true,
                confidenceNote = "Verified with Official GI Registry and National Handicrafts Archives."
            )
        )
    )

    val activeScannedImage = MutableStateFlow<Bitmap?>(null)

    // Notifications / Snackbars
    val currentNotification = MutableStateFlow<UiNotification?>(null)

    // Navigation Helpers
    fun navigateTo(destination: ScreenDestination) {
        if (_currentScreen.value != destination) {
            screenBackstack.add(_currentScreen.value)
            _currentScreen.value = destination
        }
    }

    fun navigateBack(): Boolean {
        return if (screenBackstack.isNotEmpty()) {
            _currentScreen.value = screenBackstack.removeAt(screenBackstack.size - 1)
            true
        } else {
            if (_currentScreen.value != ScreenDestination.Home) {
                _currentScreen.value = ScreenDestination.Home
                true
            } else {
                false
            }
        }
    }

    fun dismissDailyWelcome() {
        isDailyWelcomeDismissed.value = true
    }

    // Wallet Actions
    fun topUpWallet(amount: Double, paymentMethod: String = "Instant UPI") {
        if (!authState.value.effectiveRole().canPerformTopUp) {
            showNotification("Please sign in as a Patron to load money into your wallet.")
            isLoginModalVisible.value = true
            return
        }
        val current = walletState.value
        val newTxn = WalletTransaction(
            id = "TXN_${System.currentTimeMillis().toString().takeLast(6)}",
            title = "Wallet Top Up via $paymentMethod",
            subtitle = "Credited to Gaonova Artisan Patron Wallet",
            amount = amount,
            isCredit = true,
            date = "Just now",
            type = WalletTransactionType.TOP_UP
        )
        walletState.value = current.copy(
            balance = current.balance + amount,
            karmaCoins = current.karmaCoins + (amount * 0.05).toInt(),
            transactions = listOf(newTxn) + current.transactions
        )
        showNotification("₹${amount.toInt()} added to your Wallet successfully!")
    }

    fun addKarmaCoins(coins: Int, bonusCash: Double = 0.0) {
        if (!authState.value.effectiveRole().canAccessWallet) {
            showNotification("Please sign in to earn and collect Swadeshi Karma Coins.")
            isLoginModalVisible.value = true
            return
        }
        val current = walletState.value
        val newTxn = if (bonusCash > 0) {
            listOf(
                WalletTransaction(
                    id = "TXN_${System.currentTimeMillis().toString().takeLast(6)}",
                    title = "Daily Shubh-Labh Blessing",
                    subtitle = "Unlocked $coins Karma Coins & ₹${bonusCash.toInt()} Gift Voucher",
                    amount = bonusCash,
                    isCredit = true,
                    date = "Just now",
                    type = WalletTransactionType.PATRON_CASHBACK
                )
            )
        } else emptyList()

        walletState.value = current.copy(
            balance = current.balance + bonusCash,
            karmaCoins = current.karmaCoins + coins,
            transactions = newTxn + current.transactions
        )
    }

    fun redeemKarmaCoins(coinCount: Int) {
        if (!authState.value.effectiveRole().canRedeemKarmaCoins) {
            showNotification("Please sign in to redeem Karma Coins.")
            isLoginModalVisible.value = true
            return
        }
        val current = walletState.value
        if (current.karmaCoins < coinCount) {
            showNotification("Insufficient Karma Coins")
            return
        }
        val discountAmount = coinCount.toDouble()
        val newTxn = WalletTransaction(
            id = "TXN_${System.currentTimeMillis().toString().takeLast(6)}",
            title = "Karma Coins Converted to Craft Credit",
            subtitle = "Redeemed $coinCount Swadeshi Patron Coins",
            amount = discountAmount,
            isCredit = true,
            date = "Just now",
            type = WalletTransactionType.PATRON_CASHBACK
        )
        walletState.value = current.copy(
            balance = current.balance + discountAmount,
            karmaCoins = current.karmaCoins - coinCount,
            transactions = listOf(newTxn) + current.transactions
        )
        showNotification("Redeemed $coinCount Karma Coins (₹$coinCount credited)")
    }

    // Commerce Actions
    fun addToCart(product: Product, quantity: Int = 1, isGiftWrapped: Boolean = false, message: String = "") {
        viewModelScope.launch {
            commerceRepo.addToCart(product, quantity, isGiftWrapped = isGiftWrapped, giftMessage = message)
            showNotification("Added ${product.name} to Cart")
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            commerceRepo.updateCartQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            commerceRepo.removeFromCart(productId)
            showNotification("Removed item from cart")
        }
    }

    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            val isWishlisted = wishlistIds.value.contains(productId)
            if (isWishlisted) {
                commerceRepo.removeFromWishlist(productId)
                showNotification("Removed from Saved Crafts")
            } else {
                commerceRepo.toggleWishlist(productId)
                showNotification("Saved to Wishlist")
            }
        }
    }

    fun createPriceAlert(product: Product, targetPrice: Double) {
        viewModelScope.launch {
            commerceRepo.createPriceAlert(product, targetPrice)
            showNotification("Price alert created for ₹${targetPrice.toInt()}!")
        }
    }

    fun deletePriceAlert(alertId: String) {
        viewModelScope.launch {
            commerceRepo.deletePriceAlert(alertId)
            showNotification("Price alert removed")
        }
    }

    fun placeOrder(isGift: Boolean = false, giftMessage: String? = null, useWallet: Boolean = false) {
        viewModelScope.launch {
            val currentItems = cartItems.value
            if (currentItems.isEmpty()) return@launch

            val subtotal = currentItems.sumOf { it.product.price * it.quantity }
            val walletBal = walletState.value.balance

            var finalPaidFromWallet = 0.0
            var finalTotal = subtotal

            if (useWallet) {
                if (walletBal >= subtotal) {
                    finalPaidFromWallet = subtotal
                    finalTotal = 0.0
                    val currentWallet = walletState.value
                    val deductTxn = WalletTransaction(
                        id = "TXN_${System.currentTimeMillis().toString().takeLast(6)}",
                        title = "Craft Purchase - Order Placed",
                        subtitle = "${currentItems.size} items from artisan clusters",
                        amount = subtotal,
                        isCredit = false,
                        date = "Just now",
                        type = WalletTransactionType.ORDER_PAYMENT
                    )
                    walletState.value = currentWallet.copy(
                        balance = currentWallet.balance - subtotal,
                        karmaCoins = currentWallet.karmaCoins + (subtotal * 0.05).toInt(),
                        transactions = listOf(deductTxn) + currentWallet.transactions
                    )
                } else {
                    finalPaidFromWallet = walletBal
                    finalTotal = subtotal - walletBal
                    val currentWallet = walletState.value
                    val deductTxn = WalletTransaction(
                        id = "TXN_${System.currentTimeMillis().toString().takeLast(6)}",
                        title = "Craft Purchase (Partial Wallet)",
                        subtitle = "Remaining ₹${finalTotal.toInt()} via UPI",
                        amount = walletBal,
                        isCredit = false,
                        date = "Just now",
                        type = WalletTransactionType.ORDER_PAYMENT
                    )
                    walletState.value = currentWallet.copy(
                        balance = 0.0,
                        karmaCoins = currentWallet.karmaCoins + (subtotal * 0.05).toInt(),
                        transactions = listOf(deductTxn) + currentWallet.transactions
                    )
                }
            }

            val newOrder = commerceRepo.placeOrder(
                items = currentItems,
                totalAmount = subtotal,
                isGift = isGift,
                giftMessage = giftMessage
            )
            showNotification("Order #${newOrder.id} Confirmed! Artisan guild notified.")
            navigateTo(ScreenDestination.OrdersAlerts)
        }
    }

    fun createSupportTicket(subject: String, orderId: String?, category: SupportCategory, humanEscalate: Boolean) {
        viewModelScope.launch {
            val ticketId = commerceRepo.createSupportTicket(subject, orderId, category, humanEscalate)
            showNotification(if (humanEscalate) "Escalated to Craft Desk Officer ($ticketId)" else "Support Ticket $ticketId Created")
        }
    }

    // Mitti Assistant Query Actions
    private var mittiQueryJob: kotlinx.coroutines.Job? = null

    fun stopMittiQuery() {
        mittiQueryJob?.cancel()
        mittiQueryJob = null
        isAiThinking.value = false
    }

    fun askMitti(query: String, image: Bitmap? = null) {
        if (query.isBlank() && image == null) return

        val userMessage = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            sender = MessageSender.USER,
            text = query.ifBlank { "Scan & Identify this craft item" },
            timestamp = "Just now"
        )

        val currentHistory = aiChatHistory.value + userMessage
        aiChatHistory.value = currentHistory
        aiQueryText.value = ""
        isAiThinking.value = true

        mittiQueryJob?.cancel()
        mittiQueryJob = viewModelScope.launch {
            try {
                val result: AiResponseResult = GaonovaAiService.queryMitti(
                    userQuery = query,
                    bitmapImage = image,
                    chatHistory = currentHistory
                )

                val mittiMessage = ChatMessage(
                    id = "mitti_${System.currentTimeMillis()}",
                    sender = if (result.isEscalationRecommended) MessageSender.HUMAN_OFFICER else MessageSender.MITTI,
                    text = result.answerText,
                    timestamp = "Just now",
                    citations = result.citations,
                    relatedProductIds = result.matchedProducts.map { it.id },
                    suggestedActions = result.suggestedActions,
                    isConfidenceDisclosed = true,
                    confidenceNote = result.confidenceLabel
                )

                aiChatHistory.value = aiChatHistory.value + mittiMessage
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    // Query was stopped by user
                    return@launch
                }
                val errorMessage = ChatMessage(
                    id = "err_${System.currentTimeMillis()}",
                    sender = MessageSender.MITTI,
                    text = "I couldn't retrieve verified documentation for that question. Let me connect you with our regional craft knowledge desk.",
                    timestamp = "Just now",
                    suggestedActions = listOf("Browse State Catalog", "Speak to Craft Desk"),
                    isConfidenceDisclosed = true,
                    confidenceNote = "Uncertainty Disclosed: Insufficient verified documentation."
                )
                aiChatHistory.value = aiChatHistory.value + errorMessage
            } finally {
                isAiThinking.value = false
            }
        }
    }

    // Backward compatibility alias
    fun askAiSage(query: String, image: Bitmap? = null) = askMitti(query, image)

    // =========================================================================
    // AUTHENTICATION & USER MANAGEMENT ACTIONS
    // =========================================================================

    fun loginWithDemoUser(user: UserProfile) {
        viewModelScope.launch {
            authRepository.switchAccount(user)
            isLoginModalVisible.value = false
            showNotification("Welcome back, ${user.name}! Session restored as ${user.role}.")
        }
    }

    fun loginWithCredentials(identifier: String, passOrOtp: String) {
        viewModelScope.launch {
            authError.value = null
            isOtpLoading.value = true
            val result = authRepository.loginWithEmail(identifier.trim(), passOrOtp.ifBlank { "patron123" })
            isOtpLoading.value = false
            when (result) {
                is com.example.data.repository.AuthResult.Success -> {
                    isLoginModalVisible.value = false
                    showNotification("Welcome back, ${result.data.name}!")
                }
                is com.example.data.repository.AuthResult.Error -> {
                    authError.value = result.message
                    showNotification(result.message, isSuccess = false)
                }
            }
        }
    }

    fun requestOtp(phone: String) {
        val cleanPhone = if (phone.startsWith("+91")) phone else "+91 ${phone.removePrefix("+91").trim()}"
        viewModelScope.launch {
            authError.value = null
            isOtpLoading.value = true
            val result = authRepository.requestOtp(cleanPhone)
            isOtpLoading.value = false
            when (result) {
                is com.example.data.repository.AuthResult.Success -> {
                    otpRequestId.value = result.data.requestId
                    showNotification("OTP sent via SMS gateway to $cleanPhone")
                    startOtpResendTimer(result.data.resendAvailableInSeconds)
                }
                is com.example.data.repository.AuthResult.Error -> {
                    authError.value = result.message
                    showNotification(result.message, isSuccess = false)
                }
            }
        }
    }

    private fun startOtpResendTimer(seconds: Int) {
        viewModelScope.launch {
            otpResendCountdown.value = seconds
            while (otpResendCountdown.value > 0) {
                kotlinx.coroutines.delay(1000L)
                otpResendCountdown.value -= 1
            }
        }
    }

    fun verifyOtpAndLogin(otp: String) {
        val phone = authState.value.pendingOtpPhone ?: "+91 98300 12345"
        val reqId = otpRequestId.value ?: "otp_req_active"
        viewModelScope.launch {
            authError.value = null
            isOtpLoading.value = true
            val result = authRepository.verifyOtp(requestId = reqId, phone = phone, otpCode = otp)
            isOtpLoading.value = false
            when (result) {
                is com.example.data.repository.AuthResult.Success -> {
                    isLoginModalVisible.value = false
                    showNotification("Namaste ${result.data.name}! Verified & signed in successfully.")
                }
                is com.example.data.repository.AuthResult.Error -> {
                    authError.value = result.message
                    showNotification(result.message, isSuccess = false)
                }
            }
        }
    }

    fun registerNewPatron(name: String, email: String, phone: String, city: String) {
        viewModelScope.launch {
            authError.value = null
            isOtpLoading.value = true
            val result = authRepository.register(
                name = name.ifBlank { "Patron of Bengal" },
                email = email.ifBlank { null },
                phone = phone.ifBlank { "+91 98300 12345" },
                location = "$city, India"
            )
            isOtpLoading.value = false
            when (result) {
                is com.example.data.repository.AuthResult.Success -> {
                    isRegisterModalVisible.value = false
                    isLoginModalVisible.value = false
                    showNotification("Namaste ${result.data.name}! ₹500 welcome patron credit & 100 Karma Coins added.")
                }
                is com.example.data.repository.AuthResult.Error -> {
                    authError.value = result.message
                    showNotification(result.message, isSuccess = false)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            isLogoutConfirmDialogVisible.value = false
            showNotification("Signed out safely. Switched to Guest Patron mode.")
        }
    }

    fun deleteAccount(confirmationPhrase: String = "DELETE") {
        viewModelScope.launch {
            val result = authRepository.deleteAccount(confirmationPhrase)
            isDeleteAccountDialogVisible.value = false
            when (result) {
                is com.example.data.repository.AuthResult.Success -> {
                    showNotification("Your Gaonova patron account & data have been permanently deleted.")
                }
                is com.example.data.repository.AuthResult.Error -> {
                    showNotification(result.message, isSuccess = false)
                }
            }
        }
    }

    fun deleteAccountPermanently() = deleteAccount("DELETE")

    fun clearAuthError() {
        authError.value = null
    }

    fun revokeSession(sessionId: String) {
        viewModelScope.launch {
            val success = authRepository.revokeSession(sessionId)
            if (success) {
                showNotification("Session revoked successfully.")
            } else {
                showNotification("Unable to revoke session.", isSuccess = false)
            }
        }
    }

    fun refreshSessions() {
        viewModelScope.launch {
            authRepository.refreshUserSessions()
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            authRepository.logout()
            isLoginModalVisible.value = false
            isRegisterModalVisible.value = false
            showNotification("Browsing in Guest Mode. Discover freely and sign in anytime.")
        }
    }

    // Founder & Co-Founder Governance Actions (RBAC Protected)
    fun approveGiCertificateSeal(batchId: String, clusterName: String) {
        if (!authState.value.effectiveRole().canApproveGiProvenance) {
            showNotification("Access Denied: Only Patron Founders can approve GI Provenance Seals.")
            return
        }
        showNotification("Digitally signed & approved GI Provenance Seal for $clusterName ($batchId).")
    }

    fun releaseArtisanClusterEscrow(clusterName: String, amount: Double) {
        if (!authState.value.effectiveRole().canAuditArtisanEscrow) {
            showNotification("Access Denied: Only Patron Founders can release cooperative escrow payouts.")
            return
        }
        showNotification("Released ₹${amount.toInt()} escrow payout directly to $clusterName cooperative account.")
    }

    fun updateProfile(name: String, email: String, phone: String, location: String) {
        val current = authState.value.currentUser ?: return
        val initials = name.trim().split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .ifBlank { current.avatarInitials }

        val updated = current.copy(
            name = name.ifBlank { current.name },
            email = email.ifBlank { current.email },
            phone = phone.ifBlank { current.phone },
            location = location.ifBlank { current.location },
            avatarInitials = initials
        )
        viewModelScope.launch {
            commerceRepo.updateUserProfile(updated)
            isEditProfileModalVisible.value = false
            showNotification("Profile details updated successfully!")
        }
    }

    fun addShippingAddress(tag: String, fullName: String, street: String, city: String, state: String, postalCode: String, phone: String) {
        val current = authState.value.currentUser ?: return
        val newAddr = ShippingAddress(
            id = "addr_${java.util.UUID.randomUUID().toString().take(6)}",
            tag = tag.ifBlank { "Home" },
            fullName = fullName.ifBlank { current.name },
            street = street,
            city = city,
            state = state,
            postalCode = postalCode,
            phone = phone.ifBlank { current.phone },
            isDefault = current.addresses.isEmpty()
        )
        val updatedAddresses = current.addresses + newAddr
        val updated = current.copy(addresses = updatedAddresses)
        viewModelScope.launch {
            commerceRepo.updateUserProfile(updated)
            isAddAddressModalVisible.value = false
            showNotification("Address added to your Address Book")
        }
    }

    fun setDefaultShippingAddress(addressId: String) {
        val current = authState.value.currentUser ?: return
        val updatedAddresses = current.addresses.map { 
            it.copy(isDefault = it.id == addressId)
        }
        val updated = current.copy(addresses = updatedAddresses)
        viewModelScope.launch {
            commerceRepo.updateUserProfile(updated)
            showNotification("Default delivery address updated")
        }
    }

    fun deleteShippingAddress(addressId: String) {
        val current = authState.value.currentUser ?: return
        val updatedAddresses = current.addresses.filter { it.id != addressId }
        val updated = current.copy(addresses = updatedAddresses)
        viewModelScope.launch {
            commerceRepo.updateUserProfile(updated)
            showNotification("Address removed")
        }
    }

    fun showNotification(msg: String, isSuccess: Boolean = true) {
        currentNotification.value = UiNotification(message = msg, isSuccess = isSuccess)
    }

    fun clearNotification() {
        currentNotification.value = null
    }

    // =========================================================================
    // LOCATION RESOLUTION & FIRST-LAUNCH WELCOME ORCHESTRATION
    // =========================================================================

    /**
     * Initializes location detection on startup or when requested.
     */
    fun startLocationResolution(isUserTriggered: Boolean = false) {
        if (!locationService.hasLocationPermission()) {
            _locationStatus.value = LocationStatus.PermissionRationaleRequired
            return
        }

        if (!locationService.isLocationProviderEnabled()) {
            _locationStatus.value = LocationStatus.LocationServicesDisabled
            return
        }

        _locationStatus.value = LocationStatus.Resolving

        viewModelScope.launch {
            when (val result = locationService.resolveCurrentLocation()) {
                is LocationResult.Success -> {
                    _locationContext.value = result.location
                    _locationStatus.value = LocationStatus.Success(result.location)
                    locationPrefs.saveLocation(result.location)

                    // Subtly adapt home regional state filter if set to default
                    if (selectedStateFilter.value == "All India" && result.location.state.isNotBlank()) {
                        selectedStateFilter.value = result.location.state
                    }
                }
                is LocationResult.Failure -> {
                    _locationStatus.value = result.status
                    // Keep cached location active so UI remains functional
                }
            }
        }
    }

    fun handlePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            startLocationResolution(isUserTriggered = true)
        } else {
            // User declined/denied location: default to Google's nearest location track & Indian regional cluster
            resolveNearestFallbackLocation()
        }
    }

    /**
     * Resolves the nearest regional cluster / Google location cache when manual permission is not shared.
     */
    fun resolveNearestFallbackLocation() {
        viewModelScope.launch {
            when (val result = locationService.resolveDefaultGoogleOrNearestCluster()) {
                is LocationResult.Success -> {
                    _locationContext.value = result.location
                    _locationStatus.value = LocationStatus.Success(result.location)
                    locationPrefs.saveLocation(result.location)

                    if (selectedStateFilter.value == "All India" && result.location.state.isNotBlank()) {
                        selectedStateFilter.value = result.location.state
                    }
                    showNotification("Using nearest regional hub: ${result.location.city}, ${result.location.state}")
                }
                is LocationResult.Failure -> {
                    _locationStatus.value = LocationStatus.PermissionDenied(isPermanent = false)
                }
            }
        }
    }

    fun selectManualPinCode(pin: String) {
        val resolved = IndianPostalDirectory.resolvePinToLocation(pin)
        if (resolved != null) {
            _locationContext.value = resolved
            _locationStatus.value = LocationStatus.Success(resolved)
            locationPrefs.saveLocation(resolved)

            if (selectedStateFilter.value == "All India" && resolved.state.isNotBlank()) {
                selectedStateFilter.value = resolved.state
            }
            showNotification("Discovery area set to ${resolved.formattedDisplay()}")
        } else {
            showNotification("Please enter a valid 6-digit Indian PIN code", isSuccess = false)
        }
    }

    fun selectManualCluster(cluster: PostalClusterInfo) {
        val newContext = LocationContext(
            city = cluster.city,
            district = cluster.district,
            state = cluster.state,
            postalCode = cluster.pinCode,
            country = "India",
            latitude = cluster.latitude,
            longitude = cluster.longitude,
            hubCode = cluster.hubCode,
            confidence = LocationConfidence.MANUAL,
            source = LocationSource.MANUAL_SELECTION,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        _locationContext.value = newContext
        _locationStatus.value = LocationStatus.Success(newContext)
        locationPrefs.saveLocation(newContext)

        selectedStateFilter.value = cluster.state
        showNotification("Discovery area set to ${cluster.city}, ${cluster.state}")
    }

    fun dismissFirstLaunchWelcome() {
        locationPrefs.isFirstLaunchCompleted = true
        isFirstLaunchWelcomeVisible.value = false
        if (_locationStatus.value !is LocationStatus.Success) {
            resolveNearestFallbackLocation()
        }
    }

    fun checkAndTriggerDailyWelcome(userName: String = "Sayak") {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = locationPrefs.lastDailyWelcomeDate
        if (lastDate != todayStr && locationPrefs.isFirstLaunchCompleted) {
            locationPrefs.lastDailyWelcomeDate = todayStr
            val location = _locationContext.value
            val greeting = when (val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
                in 4..11 -> "Good morning"
                in 12..16 -> "Good afternoon"
                else -> "Good evening"
            }
            dailyWelcomeGreetingText.value = "$greeting, $userName. We're grateful to welcome you today from ${location.city}."
            isDailyWelcomeGreetingActive.value = true
        }
    }

    fun dismissDailyWelcomeGreeting() {
        isDailyWelcomeGreetingActive.value = false
    }
}
