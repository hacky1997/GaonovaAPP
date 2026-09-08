package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.models.*
import com.example.data.models.production.*
import com.example.data.remote.api.ApiClient
import com.example.data.remote.api.GaonovaApiService
import com.example.data.remote.dto.*
import com.example.data.remote.mock.DevSeedData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * =========================================================================
 * GAONOVA PRODUCTION CATALOG REPOSITORY
 * Coordinates Remote API -> Room Offline Cache -> Domain State.
 * The backend is the single source of truth for all products, categories,
 * artisans, media, inventory, and pricing.
 * =========================================================================
 */
class ProductionCatalogRepository(
    private val database: AppDatabase,
    private val apiService: GaonovaApiService = ApiClient.create(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val categoryCacheDao = database.categoryCacheDao()
    private val locationCacheDao = database.locationCacheDao()
    private val artisanCacheDao = database.artisanCacheDao()
    private val productCacheDao = database.productCacheDao()
    private val collectionCacheDao = database.collectionCacheDao()

    private val _productionProducts = MutableStateFlow<List<ProductionProduct>>(emptyList())
    val productionProducts: StateFlow<List<ProductionProduct>> = _productionProducts.asStateFlow()

    private val _productionCategories = MutableStateFlow<List<CategoryDomain>>(emptyList())
    val productionCategories: StateFlow<List<CategoryDomain>> = _productionCategories.asStateFlow()

    private val _productionArtisans = MutableStateFlow<List<ArtisanDomain>>(emptyList())
    val productionArtisans: StateFlow<List<ArtisanDomain>> = _productionArtisans.asStateFlow()

    private val _homeFeed = MutableStateFlow<HomeFeedDomain?>(null)
    val homeFeed: StateFlow<HomeFeedDomain?> = _homeFeed.asStateFlow()

    init {
        // Hydrate initial state from Room cache or DevSeedData
        hydrateInitialState()
    }

    private fun hydrateInitialState() {
        val initialProducts = DevSeedData.products.map { it.toDomain() }
        val initialCategories = DevSeedData.categories.map { it.toDomain() }
        val initialArtisans = DevSeedData.artisans.map { it.toDomain() }

        _productionProducts.value = initialProducts
        _productionCategories.value = initialCategories
        _productionArtisans.value = initialArtisans
    }

    /**
     * Fetch Home Feed dynamically from /api/v1/home.
     * Updates remote data into Room cache and in-memory flows.
     */
    suspend fun refreshHomeFeed(state: String? = null, lat: Double? = null, lng: Double? = null): Result<HomeFeedDomain> = withContext(ioDispatcher) {
        try {
            val response = apiService.getHomeFeed(state = state, latitude = lat, longitude = lng)
            if (response.isSuccessful && response.body()?.data != null) {
                val homeDto = response.body()!!.data!!
                val domain = homeDto.toDomain()
                _homeFeed.value = domain
                _productionProducts.value = domain.regionalCraftsSpotlight
                _productionCategories.value = domain.categories

                // Cache to Room
                cacheCategories(homeDto.categories)
                cacheProducts(homeDto.regionalCraftsSpotlight)
                Result.success(domain)
            } else {
                val errorMsg = response.body()?.error?.message ?: "Failed to fetch home feed (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch Products dynamically from /api/v1/products with filtering & pagination.
     */
    suspend fun fetchProducts(
        categorySlug: String? = null,
        state: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        handmadeOnly: Boolean? = null,
        verifiedGiOnly: Boolean? = null,
        sort: String? = "featured",
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<ProductionProduct>> = withContext(ioDispatcher) {
        try {
            val response = apiService.getProducts(
                categorySlug = categorySlug,
                state = state,
                minPrice = minPrice,
                maxPrice = maxPrice,
                handmadeOnly = handmadeOnly,
                verifiedGiOnly = verifiedGiOnly,
                sort = sort,
                page = page,
                pageSize = pageSize
            )
            if (response.isSuccessful && response.body()?.data != null) {
                val itemsDto = response.body()!!.data!!.items
                val domainProducts = itemsDto.map { it.toDomain() }
                _productionProducts.value = domainProducts
                cacheProducts(itemsDto)
                Result.success(domainProducts)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Failed to fetch products"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch Product Detail with full Amazon-style knowledge, variants, and provenance.
     */
    suspend fun fetchProductDetail(slugOrId: String): Result<ProductDetailDto> = withContext(ioDispatcher) {
        try {
            val response = apiService.getProductDetails(slugOrId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Server-side inventory reservation before checkout lock.
     */
    suspend fun reserveInventory(productId: String, variantId: String? = null, quantity: Int = 1): Result<InventoryReservationResponseDto> = withContext(ioDispatcher) {
        try {
            val request = InventoryReservationRequestDto(productId, variantId, quantity)
            val response = apiService.reserveInventory(request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val error = response.body()?.error
                val msg = if (error?.code == ErrorCode.OUT_OF_STOCK) "Item is currently out of stock or reserved by another patron." else error?.message ?: "Unable to reserve inventory"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Server-side price validation before payment authorization.
     */
    suspend fun validatePricing(productId: String, variantId: String? = null, expectedPrice: Double): Result<PriceDomain> = withContext(ioDispatcher) {
        try {
            val response = apiService.validatePricing(productId, variantId, expectedPrice)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Price verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Submit a customer craft review.
     */
    suspend fun submitReview(review: ReviewSubmissionDto): Result<ReviewDto> = withContext(ioDispatcher) {
        try {
            val response = apiService.submitReview(review)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Failed to submit review"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // ROOM CACHING HELPERS
    // =========================================================================

    private suspend fun cacheCategories(categoriesDto: List<CategoryDto>) {
        val entities = categoriesDto.map {
            CachedCategoryEntity(
                id = it.id,
                slug = it.slug,
                name = it.name,
                description = it.description,
                parentId = it.parentId,
                iconSymbolName = it.iconSymbolName,
                iconUrl = it.iconUrl,
                thumbnailUrl = it.thumbnailUrl,
                heroImageUrl = it.heroImageUrl,
                displayOrder = it.displayOrder,
                isActive = it.isActive,
                isFeatured = it.isFeatured
            )
        }
        categoryCacheDao.insertCategories(entities)
    }

    private suspend fun cacheProducts(productsDto: List<ProductDto>) {
        val entities = productsDto.map {
            val rawJson = ApiClient.moshi.adapter(ProductDto::class.java).toJson(it)
            CachedProductEntity(
                id = it.id,
                slug = it.slug,
                name = it.name,
                regionalName = it.regionalName,
                shortDescription = it.shortDescription,
                longDescription = it.longDescription,
                categoryId = it.categoryId,
                categorySlug = it.categorySlug,
                artisanId = it.artisanId,
                artisanName = it.artisanName,
                sellerId = it.sellerId,
                sellerName = it.sellerName,
                state = it.location.state,
                district = it.location.district,
                village = it.location.villageOrCluster,
                mrp = it.pricing.mrp,
                sellingPrice = it.pricing.sellingPrice,
                discountPercentage = it.pricing.discountPercentage,
                availableStock = it.inventory.availableQuantity,
                isHandmade = it.isHandmade,
                isFeatured = it.isFeatured,
                isGiCertified = it.provenance.giRegistrationNo != null,
                giRegistrationNo = it.provenance.giRegistrationNo,
                primaryImageUrl = it.media.firstOrNull { m -> m.role == "PRIMARY" }?.urls?.thumbnail ?: it.media.firstOrNull()?.urls?.thumbnail,
                rawJsonData = rawJson
            )
        }
        productCacheDao.insertProducts(entities)
    }
}
