package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * =========================================================================
 * GAONOVA PRODUCTION ROOM CACHE ENTITIES
 * Local persistence layer for categories, locations, artisans, and products.
 * Used as an offline/caching buffer while remote backend remains authoritative.
 * =========================================================================
 */

@Entity(tableName = "cached_categories")
data class CachedCategoryEntity(
    @PrimaryKey val id: String,
    val slug: String,
    val name: String,
    val description: String,
    val parentId: String?,
    val iconSymbolName: String,
    val iconUrl: String?,
    val thumbnailUrl: String?,
    val heroImageUrl: String?,
    val displayOrder: Int,
    val isActive: Boolean,
    val isFeatured: Boolean,
    val cachedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_locations")
data class CachedLocationEntity(
    @PrimaryKey val locationId: String,
    val state: String,
    val district: String,
    val villageOrCluster: String,
    val postalCode: String,
    val latitude: Double?,
    val longitude: Double?,
    val artisanHubName: String?,
    val description: String?,
    val cachedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_artisans")
data class CachedArtisanEntity(
    @PrimaryKey val id: String,
    val slug: String,
    val name: String,
    val displayName: String,
    val title: String,
    val biography: String,
    val craftTradition: String,
    val experienceYears: Int,
    val state: String,
    val district: String,
    val village: String,
    val verificationStatus: String,
    val awardsJson: String,
    val communityImpact: String,
    val cachedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_products")
data class CachedProductEntity(
    @PrimaryKey val id: String,
    val slug: String,
    val name: String,
    val regionalName: String?,
    val shortDescription: String,
    val longDescription: String,
    val categoryId: String,
    val categorySlug: String,
    val artisanId: String,
    val artisanName: String,
    val sellerId: String,
    val sellerName: String,
    val state: String,
    val district: String,
    val village: String,
    val mrp: Double,
    val sellingPrice: Double,
    val discountPercentage: Int,
    val availableStock: Int,
    val isHandmade: Boolean,
    val isFeatured: Boolean,
    val isGiCertified: Boolean,
    val giRegistrationNo: String?,
    val primaryImageUrl: String?,
    val rawJsonData: String, // Full JSON for complete reconstruction
    val cachedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_collections")
data class CachedCollectionEntity(
    @PrimaryKey val id: String,
    val slug: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val coverImageUrl: String,
    val themeColorHex: Long,
    val productIdsJson: String,
    val isFeatured: Boolean,
    val cachedAtTimestamp: Long = System.currentTimeMillis()
)
