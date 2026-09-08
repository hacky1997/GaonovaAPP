package com.example.data.models

data class CartItem(
    val product: Product,
    val quantity: Int = 1,
    val selectedVariant: String = "Standard Craft Edition",
    val isGiftWrapped: Boolean = false,
    val giftMessage: String = ""
)

data class Order(
    val id: String,
    val datePlaced: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val deliveryFee: Double = 0.0,
    val status: OrderStatus,
    val shippingAddress: ShippingAddress,
    val paymentMethod: String = "UPI / Verified Gateway",
    val trackingNumber: String,
    val estimatedDeliveryDate: String,
    val timeline: List<OrderTimelineEvent>,
    val isGift: Boolean = false,
    val giftMessage: String? = null
)

enum class OrderStatus(val label: String, val colorHex: Long) {
    PLACED("Order Placed", 0xFF0284C7),
    ARTISAN_PREPARING("Artisan Handcrafting/Preparing", 0xFFD97706),
    AUTHENTICATED("Authenticity Inspected", 0xFF0D9488),
    SHIPPED("Dispatched from Village Cluster", 0xFF7C3AED),
    OUT_FOR_DELIVERY("Out for Delivery", 0xFF2563EB),
    DELIVERED("Delivered Safely", 0xFF16A34A),
    CANCELLED("Cancelled", 0xFFDC2626),
    REFUNDED("Refund Processed", 0xFF4B5563)
}

data class OrderTimelineEvent(
    val status: OrderStatus,
    val title: String,
    val description: String,
    val timestamp: String,
    val location: String,
    val isCompleted: Boolean
)

data class ShippingAddress(
    val id: String = java.util.UUID.randomUUID().toString(),
    val tag: String = "Home", // "Home", "Work", "Studio", "Other"
    val fullName: String = "Sayak Naskar",
    val street: String = "Flat 4B, Heritage Enclave, Salt Lake Sector V",
    val city: String = "Kolkata",
    val state: String = "West Bengal",
    val postalCode: String = "700091",
    val phone: String = "+91 98300 12345",
    val isDefault: Boolean = true
)

// =========================================================================
// USER & PATRON AUTHENTICATION & RBAC MODELS
// =========================================================================

enum class UserRole(
    val title: String,
    val badgeLabel: String,
    val canAccessWallet: Boolean,
    val canPerformTopUp: Boolean,
    val canRedeemKarmaCoins: Boolean,
    val canViewPersonalOrders: Boolean,
    val canViewEscrowTransactions: Boolean,
    val canAccessFounderGovernance: Boolean,
    val canApproveGiProvenance: Boolean,
    val canAuditArtisanEscrow: Boolean,
    val canEscalateSupportDirectly: Boolean
) {
    GUEST(
        title = "Guest Patron (Atithi)",
        badgeLabel = "Atithi / Guest",
        canAccessWallet = false,
        canPerformTopUp = false,
        canRedeemKarmaCoins = false,
        canViewPersonalOrders = false,
        canViewEscrowTransactions = false,
        canAccessFounderGovernance = false,
        canApproveGiProvenance = false,
        canAuditArtisanEscrow = false,
        canEscalateSupportDirectly = false
    ),
    PATRON_MEMBER(
        title = "Patron Member",
        badgeLabel = "Verified Patron",
        canAccessWallet = true,
        canPerformTopUp = true,
        canRedeemKarmaCoins = true,
        canViewPersonalOrders = true,
        canViewEscrowTransactions = true,
        canAccessFounderGovernance = false,
        canApproveGiProvenance = false,
        canAuditArtisanEscrow = false,
        canEscalateSupportDirectly = true
    ),
    MASTER_CRAFT_COLLECTOR(
        title = "Master Craft Collector",
        badgeLabel = "Master Collector",
        canAccessWallet = true,
        canPerformTopUp = true,
        canRedeemKarmaCoins = true,
        canViewPersonalOrders = true,
        canViewEscrowTransactions = true,
        canAccessFounderGovernance = false,
        canApproveGiProvenance = false,
        canAuditArtisanEscrow = false,
        canEscalateSupportDirectly = true
    ),
    PATRON_FOUNDER(
        title = "Patron Founder & Governance Officer",
        badgeLabel = "Co-Founder & Governance",
        canAccessWallet = true,
        canPerformTopUp = true,
        canRedeemKarmaCoins = true,
        canViewPersonalOrders = true,
        canViewEscrowTransactions = true,
        canAccessFounderGovernance = true,
        canApproveGiProvenance = true,
        canAuditArtisanEscrow = true,
        canEscalateSupportDirectly = true
    )
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String = "Patron Member",
    val memberSince: String = "2024",
    val location: String = "Salt Lake, Kolkata",
    val avatarInitials: String = "SN",
    val karmaCoins: Int = 420,
    val walletBalance: Double = 2850.0,
    val addresses: List<ShippingAddress> = listOf(
        ShippingAddress(
            id = "addr_default_1",
            tag = "Home",
            fullName = "Sayak Naskar",
            street = "Flat 4B, Heritage Enclave, Salt Lake Sector V",
            city = "Kolkata",
            state = "West Bengal",
            postalCode = "700091",
            phone = "+91 98300 12345",
            isDefault = true
        ),
        ShippingAddress(
            id = "addr_work_1",
            tag = "Work",
            fullName = "Sayak Naskar",
            street = "Godrej Waterside, Tower 2, DP Block, Sector V",
            city = "Kolkata",
            state = "West Bengal",
            postalCode = "700091",
            phone = "+91 98300 12345",
            isDefault = false
        )
    ),
    val preferredLanguage: String = "English (India)"
) {
    fun resolveRole(): UserRole {
        return when {
            role.contains("Founder", ignoreCase = true) || role.contains("Co-Founder", ignoreCase = true) -> UserRole.PATRON_FOUNDER
            role.contains("Collector", ignoreCase = true) -> UserRole.MASTER_CRAFT_COLLECTOR
            else -> UserRole.PATRON_MEMBER
        }
    }
}

data class AuthState(
    val isLoggedIn: Boolean = false,
    val currentUser: UserProfile? = null,
    val isGuest: Boolean = true,
    val pendingOtpPhone: String? = null,
    val generatedOtpCode: String? = null
) {
    fun effectiveRole(): UserRole {
        if (!isLoggedIn || isGuest || currentUser == null) return UserRole.GUEST
        return currentUser.resolveRole()
    }
}

object DemoUsers {
    val sayakNaskar = UserProfile(
        id = "usr_sayak",
        name = "Sayak Naskar",
        email = "sayaknaskar@gmail.com",
        phone = "+91 98300 12345",
        role = "Patron Founder",
        memberSince = "2024",
        location = "Salt Lake, Kolkata",
        avatarInitials = "SN",
        karmaCoins = 420,
        walletBalance = 2850.0,
        addresses = listOf(
            ShippingAddress(
                id = "addr_sayak_1",
                tag = "Home",
                fullName = "Sayak Naskar",
                street = "Flat 4B, Heritage Enclave, Salt Lake Sector V",
                city = "Kolkata",
                state = "West Bengal",
                postalCode = "700091",
                phone = "+91 98300 12345",
                isDefault = true
            ),
            ShippingAddress(
                id = "addr_sayak_2",
                tag = "Office",
                fullName = "Sayak Naskar",
                street = "Tech Hub, Salt Lake Sector V",
                city = "Kolkata",
                state = "West Bengal",
                postalCode = "700091",
                phone = "+91 98300 12345",
                isDefault = false
            )
        )
    )

    val arnabRoy = UserProfile(
        id = "usr_arnab",
        name = "Arnab Roy",
        email = "arnab.roy@gaonova.com",
        phone = "+91 98311 54321",
        role = "Guild Patron & Co-Founder",
        memberSince = "2024",
        location = "Ballygunge, Kolkata",
        avatarInitials = "AR",
        karmaCoins = 850,
        walletBalance = 5400.0,
        addresses = listOf(
            ShippingAddress(
                id = "addr_arnab_1",
                tag = "Studio",
                fullName = "Arnab Roy",
                street = "12/1 Ballygunge Circular Road",
                city = "Kolkata",
                state = "West Bengal",
                postalCode = "700019",
                phone = "+91 98311 54321",
                isDefault = true
            )
        )
    )

    val priyaSharma = UserProfile(
        id = "usr_priya",
        name = "Priya Sharma",
        email = "priya.sharma@craftloom.in",
        phone = "+91 91234 56789",
        role = "Master Craft Collector",
        memberSince = "2025",
        location = "C-Scheme, Jaipur",
        avatarInitials = "PS",
        karmaCoins = 230,
        walletBalance = 1500.0,
        addresses = listOf(
            ShippingAddress(
                id = "addr_priya_1",
                tag = "Home",
                fullName = "Priya Sharma",
                street = "7, Sardar Patel Marg, C-Scheme",
                city = "Jaipur",
                state = "Rajasthan",
                postalCode = "302001",
                phone = "+91 91234 56789",
                isDefault = true
            )
        )
    )

    val allDemoUsers = listOf(sayakNaskar, arnabRoy, priyaSharma)
}

data class PriceAlert(
    val id: String,
    val productId: String,
    val productName: String,
    val targetPrice: Double,
    val currentPrice: Double,
    val stateAndDistrict: String,
    val isTriggered: Boolean = false,
    val createdAt: String
)

data class SupportTicket(
    val id: String,
    val subject: String,
    val orderId: String? = null,
    val category: SupportCategory,
    val status: TicketStatus,
    val priority: String = "Standard",
    val aiSummary: String,
    val conversation: List<ChatMessage>,
    val humanEscalationRequested: Boolean = false,
    val assignedTeam: String = "Gaonova Craft Resolution Desk",
    val slaTime: String = "Within 4 hours",
    val createdAt: String
)

enum class SupportCategory(val title: String) {
    AUTHENTICITY_INQUIRY("Product Authenticity & GI Check"),
    ORDER_TRACKING("Order Status & Delayed Delivery"),
    DAMAGED_OR_MISSING("Artisanal Craft Condition / Return"),
    SELLER_COMMUNICATION("Artisan / Producer Inquiry"),
    REFUND_DISPUTE("Payment & Refund Resolution"),
    GENERAL_KNOWLEDGE("Cultural & Regional Knowledge Inquiry")
}

enum class TicketStatus(val title: String, val colorHex: Long) {
    AI_ASSISTING("AI Assisted", 0xFF0284C7),
    ESCALATED_TO_HUMAN("Escalated to Craft Desk Officer", 0xFFD97706),
    IN_INVESTIGATION("Under Verification", 0xFF7C3AED),
    RESOLVED("Resolved & Closed", 0xFF16A34A)
}

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: String,
    val citations: List<Citation> = emptyList(),
    val relatedProductIds: List<String> = emptyList(),
    val suggestedActions: List<String> = emptyList(),
    val isConfidenceDisclosed: Boolean = false,
    val confidenceNote: String? = null
)

enum class MessageSender {
    USER,
    MITTI,
    HUMAN_OFFICER,
    SYSTEM
}

// =========================================================================
// SWADESHI ARTISAN PATRON WALLET MODELS
// =========================================================================

data class WalletState(
    val balance: Double = 0.0,
    val karmaCoins: Int = 0, // 1 Karma Coin = ₹1 for handloom & craft purchase
    val isAutoPayEnabled: Boolean = false,
    val transactions: List<WalletTransaction> = emptyList()
)

data class WalletTransaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val isCredit: Boolean,
    val date: String,
    val type: WalletTransactionType,
    val status: String = "Successful"
)

enum class WalletTransactionType(val label: String) {
    TOP_UP("Added to Wallet"),
    ORDER_PAYMENT("Craft Purchase"),
    PATRON_CASHBACK("GI Patron Cashback"),
    ARTISAN_DIVIDEND("Cooperative Dividend"),
    GIFT_VOUCHER("Gift Card Added")
}

fun getTransactionsForUser(userId: String?): List<WalletTransaction> {
    return when (userId) {
        "usr_sayak" -> listOf(
            WalletTransaction(
                id = "TXN_98412",
                title = "GI Patron Cashback Reward",
                subtitle = "Earned 5% on Nakshi Kantha Silk Dupatta",
                amount = 172.5,
                isCredit = true,
                date = "Today, 10:15 AM",
                type = WalletTransactionType.PATRON_CASHBACK
            ),
            WalletTransaction(
                id = "TXN_98399",
                title = "Wallet Top Up via UPI",
                subtitle = "Google Pay • Axis Bank xx4921",
                amount = 2000.0,
                isCredit = true,
                date = "Yesterday, 04:30 PM",
                type = WalletTransactionType.TOP_UP
            ),
            WalletTransaction(
                id = "TXN_98210",
                title = "Artisan Direct Payment",
                subtitle = "Order #GNV-847291 • Raghurajpur Guild",
                amount = 1890.0,
                isCredit = false,
                date = "18 Aug 2026",
                type = WalletTransactionType.ORDER_PAYMENT
            ),
            WalletTransaction(
                id = "TXN_98004",
                title = "Weaver Cluster Patron Dividend",
                subtitle = "Quarterly reward for supporting certified GI clusters",
                amount = 350.0,
                isCredit = true,
                date = "15 Aug 2026",
                type = WalletTransactionType.ARTISAN_DIVIDEND
            )
        )
        "usr_arnab" -> listOf(
            WalletTransaction(
                id = "TXN_98550",
                title = "Founder Cluster Escrow Release",
                subtitle = "Nadia Weaver Direct Settlement #ESC-901",
                amount = 2500.0,
                isCredit = true,
                date = "Today, 09:00 AM",
                type = WalletTransactionType.ARTISAN_DIVIDEND
            ),
            WalletTransaction(
                id = "TXN_98312",
                title = "Bank IMPS Top Up",
                subtitle = "HDFC Bank • Studio Acc xx1910",
                amount = 5000.0,
                isCredit = true,
                date = "20 Aug 2026",
                type = WalletTransactionType.TOP_UP
            ),
            WalletTransaction(
                id = "TXN_98120",
                title = "Bastar Bell Metal Purchase",
                subtitle = "Order #GNV-99381 • Kondagaon Cluster",
                amount = 2100.0,
                isCredit = false,
                date = "17 Aug 2026",
                type = WalletTransactionType.ORDER_PAYMENT
            )
        )
        "usr_priya" -> listOf(
            WalletTransaction(
                id = "TXN_98610",
                title = "Jaipur Blue Pottery Cashback",
                subtitle = "GI Certified Artisan direct benefit",
                amount = 150.0,
                isCredit = true,
                date = "Yesterday, 02:20 PM",
                type = WalletTransactionType.PATRON_CASHBACK
            ),
            WalletTransaction(
                id = "TXN_98440",
                title = "UPI Top Up",
                subtitle = "PhonePe • SBI xx8821",
                amount = 1500.0,
                isCredit = true,
                date = "19 Aug 2026",
                type = WalletTransactionType.TOP_UP
            )
        )
        null -> emptyList()
        else -> listOf(
            WalletTransaction(
                id = "TXN_${System.currentTimeMillis().toString().takeLast(5)}",
                title = "Welcome Patron Credit",
                subtitle = "Gaonova Swadeshi onboarding credit",
                amount = 500.0,
                isCredit = true,
                date = "Just now",
                type = WalletTransactionType.PATRON_CASHBACK
            )
        )
    }
}
