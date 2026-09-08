package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val id: String, // "${userId}_${productId}"
    val userId: String,
    val productId: String,
    val quantity: Int,
    val variant: String,
    val isGiftWrapped: Boolean,
    val giftMessage: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey val id: String, // "${userId}_${productId}"
    val userId: String,
    val productId: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey val alertId: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val targetPrice: Double,
    val currentPrice: Double,
    val stateAndDistrict: String,
    val isTriggered: Boolean = false,
    val createdAt: String
)

@Entity(tableName = "recent_views")
data class RecentViewEntity(
    @PrimaryKey val id: String, // "${userId}_${productId}"
    val userId: String,
    val productId: String,
    val viewedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val userId: String,
    val datePlaced: String,
    val itemsJson: String, // serialized item list
    val totalAmount: Double,
    val status: String,
    val trackingNumber: String,
    val estimatedDeliveryDate: String,
    val isGift: Boolean,
    val giftMessage: String?
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val ticketId: String,
    val userId: String,
    val subject: String,
    val orderId: String?,
    val category: String,
    val status: String,
    val aiSummary: String,
    val messagesJson: String,
    val humanEscalationRequested: Boolean,
    val createdAt: String
)

@Entity(tableName = "mitti_messages")
data class MittiMessageEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val sender: String,
    val text: String,
    val timestamp: String,
    val suggestedActionsJson: String = "",
    val confidenceNote: String? = null
)

@Entity(tableName = "user_sessions")
data class UserSessionEntity(
    @PrimaryKey val sessionId: String,
    val userId: String,
    val deviceId: String,
    val deviceName: String,
    val platform: String = "ANDROID",
    val ipAddress: String = "127.0.0.1",
    val lastActiveAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000L),
    val isCurrentDevice: Boolean = false
)

@Entity(tableName = "user_profiles")
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val memberSince: String,
    val location: String,
    val avatarInitials: String,
    val karmaCoins: Int,
    val walletBalance: Double,
    val addressJson: String, // serialized address list
    val preferredLanguage: String = "English (India)",
    val accountStatus: String = "ACTIVE",
    val isActiveSession: Boolean = false,
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)
