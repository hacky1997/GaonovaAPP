package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedTimestamp DESC")
    fun getCartItemsForUser(userId: String): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCartItem(item: CartEntity)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun removeCartItem(userId: String, productId: String)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCartForUser(userId: String)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun purgeUserData(userId: String)
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items WHERE userId = :userId ORDER BY addedTimestamp DESC")
    fun getWishlistForUser(userId: String): Flow<List<WishlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistEntity)

    @Query("DELETE FROM wishlist_items WHERE userId = :userId AND productId = :productId")
    suspend fun removeFromWishlist(userId: String, productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE userId = :userId AND productId = :productId)")
    fun isInWishlist(userId: String, productId: String): Flow<Boolean>

    @Query("DELETE FROM wishlist_items WHERE userId = :userId")
    suspend fun purgeUserData(userId: String)
}

@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alerts WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllPriceAlertsForUser(userId: String): Flow<List<PriceAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlertEntity)

    @Query("DELETE FROM price_alerts WHERE alertId = :alertId AND userId = :userId")
    suspend fun deleteAlert(alertId: String, userId: String)

    @Query("DELETE FROM price_alerts WHERE userId = :userId")
    suspend fun purgeUserData(userId: String)
}

@Dao
interface RecentViewDao {
    @Query("SELECT * FROM recent_views WHERE userId = :userId ORDER BY viewedTimestamp DESC LIMIT 15")
    fun getRecentViewsForUser(userId: String): Flow<List<RecentViewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordView(view: RecentViewEntity)

    @Query("DELETE FROM recent_views WHERE userId = :userId")
    suspend fun purgeUserData(userId: String)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM user_orders WHERE userId = :userId ORDER BY datePlaced DESC")
    fun getAllOrdersForUser(userId: String): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("SELECT * FROM user_orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("DELETE FROM user_orders WHERE userId = :userId")
    suspend fun purgeUserData(userId: String)
}

@Dao
interface SupportDao {
    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllTicketsForUser(userId: String): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity)

    @Update
    suspend fun updateTicket(ticket: SupportTicketEntity)

    @Query("DELETE FROM support_tickets WHERE userId = :userId")
    suspend fun purgeUserData(userId: String)
}

@Dao
interface MittiDao {
    @Query("SELECT * FROM mitti_messages WHERE userId = :userId ORDER BY timestamp ASC")
    fun getConversationForUser(userId: String): Flow<List<MittiMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: MittiMessageEntity)

    @Query("DELETE FROM mitti_messages WHERE userId = :userId")
    suspend fun purgeUserData(userId: String)
}

@Dao
interface UserSessionDao {
    @Query("SELECT * FROM user_sessions WHERE userId = :userId ORDER BY lastActiveAt DESC")
    fun getSessionsForUser(userId: String): Flow<List<UserSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<UserSessionEntity>)

    @Query("DELETE FROM user_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM user_sessions WHERE userId = :userId")
    suspend fun purgeUserData(userId: String)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles WHERE accountStatus != 'DELETED'")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user_profiles WHERE isActiveSession = 1 AND accountStatus != 'DELETED' LIMIT 1")
    fun getActiveUser(): Flow<UserEntity?>

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM user_profiles WHERE (email = :emailOrPhone OR phone = :emailOrPhone) AND accountStatus != 'DELETED' LIMIT 1")
    suspend fun getUserByEmailOrPhone(emailOrPhone: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE user_profiles SET isActiveSession = 0")
    suspend fun clearActiveSessions()

    @Query("UPDATE user_profiles SET isActiveSession = 1 WHERE userId = :userId")
    suspend fun setActiveSession(userId: String)

    @Query("UPDATE user_profiles SET accountStatus = 'DELETED', isActiveSession = 0 WHERE userId = :userId")
    suspend fun markUserDeleted(userId: String)

    @Query("DELETE FROM user_profiles WHERE userId = :userId")
    suspend fun deleteUser(userId: String)
}
