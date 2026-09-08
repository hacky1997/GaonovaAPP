package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

/**
 * =========================================================================
 * GAONOVA PRODUCTION CACHE DAOs
 * Fast, queryable local SQLite storage for categories, locations, artisans,
 * collections, and products fetched dynamically from the /api/v1 backend.
 * =========================================================================
 */

@Dao
interface CategoryCacheDao {
    @Query("SELECT * FROM cached_categories WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<CachedCategoryEntity>>

    @Query("SELECT * FROM cached_categories WHERE slug = :slug LIMIT 1")
    suspend fun getCategoryBySlug(slug: String): CachedCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CachedCategoryEntity>)

    @Query("DELETE FROM cached_categories")
    suspend fun clearCategories()
}

@Dao
interface LocationCacheDao {
    @Query("SELECT * FROM cached_locations ORDER BY state ASC, district ASC")
    fun getAllLocations(): Flow<List<CachedLocationEntity>>

    @Query("SELECT * FROM cached_locations WHERE locationId = :id LIMIT 1")
    suspend fun getLocationById(id: String): CachedLocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<CachedLocationEntity>)

    @Query("DELETE FROM cached_locations")
    suspend fun clearLocations()
}

@Dao
interface ArtisanCacheDao {
    @Query("SELECT * FROM cached_artisans ORDER BY name ASC")
    fun getAllArtisans(): Flow<List<CachedArtisanEntity>>

    @Query("SELECT * FROM cached_artisans WHERE id = :id LIMIT 1")
    suspend fun getArtisanById(id: String): CachedArtisanEntity?

    @Query("SELECT * FROM cached_artisans WHERE slug = :slug LIMIT 1")
    suspend fun getArtisanBySlug(slug: String): CachedArtisanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtisans(artisans: List<CachedArtisanEntity>)

    @Query("DELETE FROM cached_artisans")
    suspend fun clearArtisans()
}

@Dao
interface ProductCacheDao {
    @Query("SELECT * FROM cached_products ORDER BY cachedAtTimestamp DESC")
    fun getAllCachedProducts(): Flow<List<CachedProductEntity>>

    @Query("SELECT * FROM cached_products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): CachedProductEntity?

    @Query("SELECT * FROM cached_products WHERE slug = :slug LIMIT 1")
    suspend fun getProductBySlug(slug: String): CachedProductEntity?

    @Query("SELECT * FROM cached_products WHERE categorySlug = :categorySlug")
    fun getProductsByCategory(categorySlug: String): Flow<List<CachedProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<CachedProductEntity>)

    @Query("DELETE FROM cached_products WHERE id = :id")
    suspend fun deleteProduct(id: String)

    @Query("DELETE FROM cached_products")
    suspend fun clearProductCache()
}

@Dao
interface CollectionCacheDao {
    @Query("SELECT * FROM cached_collections ORDER BY isFeatured DESC, cachedAtTimestamp DESC")
    fun getAllCollections(): Flow<List<CachedCollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: List<CachedCollectionEntity>)

    @Query("DELETE FROM cached_collections")
    suspend fun clearCollections()
}
