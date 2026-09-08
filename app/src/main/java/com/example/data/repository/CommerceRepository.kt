package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class CommerceRepository(
    private val database: AppDatabase,
    private val authRepository: AuthRepository
) {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Dynamically scoped to active user ID; switches reactively on login/switch/logout
    val currentUserId: Flow<String> = authRepository.activeUser.map { it?.id ?: "guest_user" }

    val cartItems: Flow<List<CartItem>> = currentUserId.flatMapLatest { userId ->
        database.cartDao().getCartItemsForUser(userId).map { entities ->
            entities.mapNotNull { entity ->
                val product = ProductRepository.getProductById(entity.productId) ?: return@mapNotNull null
                CartItem(
                    product = product,
                    quantity = entity.quantity,
                    selectedVariant = entity.variant,
                    isGiftWrapped = entity.isGiftWrapped,
                    giftMessage = entity.giftMessage
                )
            }
        }
    }

    val wishlistIds: Flow<Set<String>> = currentUserId.flatMapLatest { userId ->
        database.wishlistDao().getWishlistForUser(userId).map { entities ->
            entities.map { it.productId }.toSet()
        }
    }

    val priceAlerts: Flow<List<PriceAlert>> = currentUserId.flatMapLatest { userId ->
        database.priceAlertDao().getAllPriceAlertsForUser(userId).map { list ->
            list.map { entity ->
                PriceAlert(
                    id = entity.alertId,
                    productId = entity.productId,
                    productName = entity.productName,
                    targetPrice = entity.targetPrice,
                    currentPrice = entity.currentPrice,
                    stateAndDistrict = entity.stateAndDistrict,
                    isTriggered = entity.isTriggered || entity.currentPrice <= entity.targetPrice,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    val orders: Flow<List<Order>> = currentUserId.flatMapLatest { userId ->
        database.orderDao().getAllOrdersForUser(userId).map { entities ->
            entities.map { entity ->
                val statusEnum = try {
                    OrderStatus.valueOf(entity.status)
                } catch (e: Exception) {
                    OrderStatus.PLACED
                }

                val timeline = listOf(
                    OrderTimelineEvent(
                        status = OrderStatus.PLACED,
                        title = "Order Confirmed & Idempotently Recorded",
                        description = "Payment verified. Artisan collective notified in real-time.",
                        timestamp = entity.datePlaced,
                        location = "Gaonova Digital Gateway",
                        isCompleted = true
                    ),
                    OrderTimelineEvent(
                        status = OrderStatus.ARTISAN_PREPARING,
                        title = "Artisan Verification & Packaging",
                        description = "Master artisan inspecting handcraft seals and GI provenance tags.",
                        timestamp = "Estimated: Next Day",
                        location = "Village Artisan Studio",
                        isCompleted = statusEnum.ordinal >= OrderStatus.ARTISAN_PREPARING.ordinal
                    ),
                    OrderTimelineEvent(
                        status = OrderStatus.SHIPPED,
                        title = "Dispatched from Regional Cluster Hub",
                        description = "Assigned tracked express logistics with climate-safe craft packaging.",
                        timestamp = "Estimated: 2 Days",
                        location = "District Craft Logistic Center",
                        isCompleted = statusEnum.ordinal >= OrderStatus.SHIPPED.ordinal
                    ),
                    OrderTimelineEvent(
                        status = OrderStatus.DELIVERED,
                        title = "Delivery to Doorstep",
                        description = "Expected delivery with authenticity certificate enclosed.",
                        timestamp = entity.estimatedDeliveryDate,
                        location = "Destination City",
                        isCompleted = statusEnum.ordinal >= OrderStatus.DELIVERED.ordinal
                    )
                )

                val sampleProduct = ProductRepository.getProductById(entity.itemsJson)
                    ?: ProductRepository.sampleProducts.first()

                Order(
                    id = entity.orderId,
                    datePlaced = entity.datePlaced,
                    items = listOf(CartItem(product = sampleProduct, quantity = 1)),
                    totalAmount = entity.totalAmount,
                    status = statusEnum,
                    shippingAddress = ShippingAddress(),
                    trackingNumber = entity.trackingNumber,
                    estimatedDeliveryDate = entity.estimatedDeliveryDate,
                    timeline = timeline,
                    isGift = entity.isGift,
                    giftMessage = entity.giftMessage
                )
            }
        }
    }

    val supportTickets: Flow<List<SupportTicket>> = currentUserId.flatMapLatest { userId ->
        database.supportDao().getAllTicketsForUser(userId).map { entities ->
            entities.map { entity ->
                val statusEnum = try {
                    TicketStatus.valueOf(entity.status)
                } catch (e: Exception) {
                    TicketStatus.AI_ASSISTING
                }

                val categoryEnum = try {
                    SupportCategory.valueOf(entity.category)
                } catch (e: Exception) {
                    SupportCategory.GENERAL_KNOWLEDGE
                }

                SupportTicket(
                    id = entity.ticketId,
                    subject = entity.subject,
                    orderId = entity.orderId,
                    category = categoryEnum,
                    status = statusEnum,
                    aiSummary = entity.aiSummary,
                    conversation = listOf(
                        ChatMessage(
                            id = "msg_init",
                            sender = MessageSender.USER,
                            text = entity.subject,
                            timestamp = entity.createdAt
                        ),
                        ChatMessage(
                            id = "msg_resp",
                            sender = if (entity.humanEscalationRequested) MessageSender.HUMAN_OFFICER else MessageSender.MITTI,
                            text = if (entity.humanEscalationRequested)
                                "Hello, your inquiry has been escalated directly to our senior Craft Desk Officer. We are reviewing the provenance records with the district collective. Ticket SLA is within 4 hours."
                            else
                                "Mitti is actively investigating your request against our official regional provenance archives.",
                            timestamp = entity.createdAt
                        )
                    ),
                    humanEscalationRequested = entity.humanEscalationRequested,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    val activeUser: Flow<UserProfile?> = authRepository.activeUser
    val allRegisteredUsers: Flow<List<UserProfile>> = authRepository.allRegisteredUsers

    private fun getEffectiveUserId(): String {
        return authRepository.activeUser.value?.id ?: "guest_user"
    }

    suspend fun addToCart(
        product: Product,
        quantity: Int = 1,
        variant: String = "Standard Craft Edition",
        isGiftWrapped: Boolean = false,
        giftMessage: String = ""
    ) {
        val userId = getEffectiveUserId()
        database.cartDao().insertOrUpdateCartItem(
            CartEntity(
                id = "${userId}_${product.id}",
                userId = userId,
                productId = product.id,
                quantity = quantity,
                variant = variant,
                isGiftWrapped = isGiftWrapped,
                giftMessage = giftMessage
            )
        )
    }

    suspend fun updateCartQuantity(productId: String, quantity: Int) {
        val userId = getEffectiveUserId()
        if (quantity <= 0) {
            database.cartDao().removeCartItem(userId, productId)
        } else {
            val product = ProductRepository.getProductById(productId) ?: return
            database.cartDao().insertOrUpdateCartItem(
                CartEntity(
                    id = "${userId}_$productId",
                    userId = userId,
                    productId = productId,
                    quantity = quantity,
                    variant = "Standard Craft Edition",
                    isGiftWrapped = false,
                    giftMessage = ""
                )
            )
        }
    }

    suspend fun removeFromCart(productId: String) {
        val userId = getEffectiveUserId()
        database.cartDao().removeCartItem(userId, productId)
    }

    suspend fun clearCart() {
        val userId = getEffectiveUserId()
        database.cartDao().clearCartForUser(userId)
    }

    suspend fun toggleWishlist(productId: String) {
        val userId = getEffectiveUserId()
        database.wishlistDao().addToWishlist(
            WishlistEntity(
                id = "${userId}_$productId",
                userId = userId,
                productId = productId
            )
        )
    }

    suspend fun removeFromWishlist(productId: String) {
        val userId = getEffectiveUserId()
        database.wishlistDao().removeFromWishlist(userId, productId)
    }

    suspend fun createPriceAlert(product: Product, targetPrice: Double) {
        val userId = getEffectiveUserId()
        val alert = PriceAlertEntity(
            alertId = "ALT-${UUID.randomUUID().toString().take(8).uppercase()}",
            userId = userId,
            productId = product.id,
            productName = product.name,
            targetPrice = targetPrice,
            currentPrice = product.price,
            stateAndDistrict = "${product.district}, ${product.state}",
            isTriggered = product.price <= targetPrice,
            createdAt = shortDateFormat.format(Date())
        )
        database.priceAlertDao().insertAlert(alert)
    }

    suspend fun deletePriceAlert(alertId: String) {
        val userId = getEffectiveUserId()
        database.priceAlertDao().deleteAlert(alertId, userId)
    }

    suspend fun recordRecentView(productId: String) {
        val userId = getEffectiveUserId()
        database.recentViewDao().recordView(
            RecentViewEntity(
                id = "${userId}_$productId",
                userId = userId,
                productId = productId
            )
        )
    }

    suspend fun placeOrder(
        items: List<CartItem>,
        totalAmount: Double,
        isGift: Boolean = false,
        giftMessage: String? = null
    ): Order {
        val userId = getEffectiveUserId()
        val orderId = "GNV-${(100000..999999).random()}"
        val trackingNo = "GNV-TRK-${UUID.randomUUID().toString().take(8).uppercase()}"
        val placedDate = dateFormat.format(Date())
        val estimatedDelivery = shortDateFormat.format(Date(System.currentTimeMillis() + 4 * 24 * 60 * 60 * 1000L))

        val orderEntity = OrderEntity(
            orderId = orderId,
            userId = userId,
            datePlaced = placedDate,
            itemsJson = items.firstOrNull()?.product?.id ?: "prod_kantha_bolpur",
            totalAmount = totalAmount,
            status = OrderStatus.PLACED.name,
            trackingNumber = trackingNo,
            estimatedDeliveryDate = estimatedDelivery,
            isGift = isGift,
            giftMessage = giftMessage
        )

        database.orderDao().insertOrder(orderEntity)
        database.cartDao().clearCartForUser(userId)

        return Order(
            id = orderId,
            datePlaced = placedDate,
            items = items,
            totalAmount = totalAmount,
            status = OrderStatus.PLACED,
            shippingAddress = ShippingAddress(),
            trackingNumber = trackingNo,
            estimatedDeliveryDate = estimatedDelivery,
            timeline = emptyList(),
            isGift = isGift,
            giftMessage = giftMessage
        )
    }

    suspend fun createSupportTicket(
        subject: String,
        orderId: String?,
        category: SupportCategory,
        humanEscalation: Boolean
    ): String {
        val userId = getEffectiveUserId()
        val ticketId = "TCK-${UUID.randomUUID().toString().take(6).uppercase()}"
        val now = dateFormat.format(Date())
        val entity = SupportTicketEntity(
            ticketId = ticketId,
            userId = userId,
            subject = subject,
            orderId = orderId,
            category = category.name,
            status = if (humanEscalation) TicketStatus.ESCALATED_TO_HUMAN.name else TicketStatus.AI_ASSISTING.name,
            aiSummary = "Inquiry regarding $subject for order: ${orderId ?: "General Inquiry"}",
            messagesJson = "",
            humanEscalationRequested = humanEscalation,
            createdAt = now
        )
        database.supportDao().insertTicket(entity)
        return ticketId
    }

    suspend fun initializeDefaultUsersIfNeeded() {
        for (user in DemoUsers.allDemoUsers) {
            val existing = database.userDao().getUserById(user.id)
            if (existing == null) {
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
                        isActiveSession = false
                    )
                )
            }
        }
    }

    suspend fun loginWithUser(user: UserProfile) {
        authRepository.switchAccount(user)
    }

    suspend fun registerNewUser(name: String, email: String, phone: String, location: String): UserProfile {
        val res = authRepository.register(name, email, phone, location)
        return if (res is AuthResult.Success) {
            res.data
        } else {
            DemoUsers.sayakNaskar
        }
    }

    suspend fun logout() {
        authRepository.logout()
    }

    suspend fun updateUserProfile(user: UserProfile) {
        authRepository.updateProfile(user.name, user.email, user.location, "English (India)")
    }

    private fun serializeAddresses(addresses: List<ShippingAddress>): String {
        return addresses.joinToString(";;;") {
            "${it.id}::${it.tag}::${it.fullName}::${it.street}::${it.city}::${it.state}::${it.postalCode}::${it.phone}::${it.isDefault}"
        }
    }
}
