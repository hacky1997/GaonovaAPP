package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entities.*

@Database(
    entities = [
        CartEntity::class,
        WishlistEntity::class,
        PriceAlertEntity::class,
        RecentViewEntity::class,
        OrderEntity::class,
        SupportTicketEntity::class,
        MittiMessageEntity::class,
        UserSessionEntity::class,
        UserEntity::class,
        CachedCategoryEntity::class,
        CachedLocationEntity::class,
        CachedArtisanEntity::class,
        CachedProductEntity::class,
        CachedCollectionEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun priceAlertDao(): PriceAlertDao
    abstract fun recentViewDao(): RecentViewDao
    abstract fun orderDao(): OrderDao
    abstract fun supportDao(): SupportDao
    abstract fun mittiDao(): MittiDao
    abstract fun userSessionDao(): UserSessionDao
    abstract fun userDao(): UserDao
    abstract fun categoryCacheDao(): CategoryCacheDao
    abstract fun locationCacheDao(): LocationCacheDao
    abstract fun artisanCacheDao(): ArtisanCacheDao
    abstract fun productCacheDao(): ProductCacheDao
    abstract fun collectionCacheDao(): CollectionCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gaonova_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
