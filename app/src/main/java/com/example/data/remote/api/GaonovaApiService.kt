package com.example.data.remote.api

import com.example.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * =========================================================================
 * GAONOVA REST API v1 CONTRACT
 * Versioned backend endpoints for products, categories, locations, artisans,
 * inventory reservations, pricing validation, collections, and stories.
 * =========================================================================
 */
interface GaonovaApiService {

    // =========================================================================
    // 1. HOME & DISCOVERY
    // =========================================================================

    @GET("api/v1/home")
    suspend fun getHomeFeed(
        @Query("state") state: String? = null,
        @Query("lat") latitude: Double? = null,
        @Query("lng") longitude: Double? = null,
        @Query("postalCode") postalCode: String? = null
    ): Response<ApiResponse<HomeFeedDto>>

    // =========================================================================
    // 2. PRODUCTS & SEARCH
    // =========================================================================

    @GET("api/v1/products")
    suspend fun getProducts(
        @Query("category") categorySlug: String? = null,
        @Query("subcategory") subcategorySlug: String? = null,
        @Query("state") state: String? = null,
        @Query("district") district: String? = null,
        @Query("village") village: String? = null,
        @Query("artisan") artisanSlugOrId: String? = null,
        @Query("seller") sellerId: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("handmade") handmadeOnly: Boolean? = null,
        @Query("verified") verifiedGiOnly: Boolean? = null,
        @Query("sort") sort: String? = "featured", // "price_asc", "price_desc", "rating", "newest"
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<PaginatedResponseDto<ProductDto>>>

    @GET("api/v1/products/{slug}/details")
    suspend fun getProductDetails(
        @Path("slug") slug: String
    ): Response<ApiResponse<ProductDetailDto>>

    @GET("api/v1/products/by-id/{id}")
    suspend fun getProductById(
        @Path("id") id: String
    ): Response<ApiResponse<ProductDto>>

    @GET("api/v1/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("naturalLanguage") naturalLanguage: Boolean = false,
        @Query("category") categorySlug: String? = null,
        @Query("state") state: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<PaginatedResponseDto<ProductDto>>>

    // =========================================================================
    // 3. CATEGORIES
    // =========================================================================

    @GET("api/v1/categories")
    suspend fun getCategories(
        @Query("includeSubcategories") includeSubcategories: Boolean = true
    ): Response<ApiResponse<List<CategoryDto>>>

    @GET("api/v1/categories/{slug}")
    suspend fun getCategoryBySlug(
        @Path("slug") slug: String
    ): Response<ApiResponse<CategoryDto>>

    @GET("api/v1/categories/{slug}/products")
    suspend fun getCategoryProducts(
        @Path("slug") slug: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sort") sort: String? = null
    ): Response<ApiResponse<PaginatedResponseDto<ProductDto>>>

    // =========================================================================
    // 4. LOCATIONS & GEOGRAPHY
    // =========================================================================

    @GET("api/v1/locations")
    suspend fun getLocations(
        @Query("level") level: String? = null, // "state", "district", "cluster"
        @Query("state") state: String? = null
    ): Response<ApiResponse<List<LocationHierarchyDto>>>

    @GET("api/v1/locations/{id}")
    suspend fun getLocationById(
        @Path("id") locationId: String
    ): Response<ApiResponse<LocationHierarchyDto>>

    @GET("api/v1/locations/{id}/products")
    suspend fun getLocationProducts(
        @Path("id") locationId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<PaginatedResponseDto<ProductDto>>>

    @GET("api/v1/locations/{id}/artisans")
    suspend fun getLocationArtisans(
        @Path("id") locationId: String
    ): Response<ApiResponse<List<ArtisanDto>>>

    @GET("api/v1/locations/{id}/stories")
    suspend fun getLocationStories(
        @Path("id") locationId: String
    ): Response<ApiResponse<List<VillageStoryDto>>>

    // =========================================================================
    // 5. ARTISANS & GUILDS
    // =========================================================================

    @GET("api/v1/artisans")
    suspend fun getArtisans(
        @Query("state") state: String? = null,
        @Query("craft") craft: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<PaginatedResponseDto<ArtisanDto>>>

    @GET("api/v1/artisans/{slug}")
    suspend fun getArtisanBySlug(
        @Path("slug") slug: String
    ): Response<ApiResponse<ArtisanDto>>

    @GET("api/v1/artisans/{slug}/products")
    suspend fun getArtisanProducts(
        @Path("slug") slug: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<PaginatedResponseDto<ProductDto>>>

    @GET("api/v1/artisans/{slug}/stories")
    suspend fun getArtisanStories(
        @Path("slug") slug: String
    ): Response<ApiResponse<List<VillageStoryDto>>>

    // =========================================================================
    // 6. SELLERS & COOPERATIVES
    // =========================================================================

    @GET("api/v1/sellers")
    suspend fun getSellers(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<PaginatedResponseDto<SellerDto>>>

    @GET("api/v1/sellers/{id}")
    suspend fun getSellerById(
        @Path("id") sellerId: String
    ): Response<ApiResponse<SellerDto>>

    // =========================================================================
    // 7. INVENTORY RESERVATION & LOCKS
    // =========================================================================

    @GET("api/v1/inventory/{productId}")
    suspend fun getProductInventory(
        @Path("productId") productId: String,
        @Query("variantId") variantId: String? = null
    ): Response<ApiResponse<InventoryDto>>

    @POST("api/v1/inventory/reserve")
    suspend fun reserveInventory(
        @Body request: InventoryReservationRequestDto
    ): Response<ApiResponse<InventoryReservationResponseDto>>

    @POST("api/v1/inventory/release/{reservationToken}")
    suspend fun releaseInventoryReservation(
        @Path("reservationToken") token: String
    ): Response<ApiResponse<Boolean>>

    // =========================================================================
    // 8. PRICING VALIDATION & REVIEWS
    // =========================================================================

    @GET("api/v1/pricing/check")
    suspend fun validatePricing(
        @Query("productId") productId: String,
        @Query("variantId") variantId: String? = null,
        @Query("expectedPrice") expectedPrice: Double
    ): Response<ApiResponse<PriceDto>>

    @GET("api/v1/reviews/{productId}")
    suspend fun getProductReviews(
        @Path("productId") productId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<PaginatedResponseDto<ReviewDto>>>

    @POST("api/v1/reviews")
    suspend fun submitReview(
        @Body review: ReviewSubmissionDto
    ): Response<ApiResponse<ReviewDto>>

    // =========================================================================
    // 9. COLLECTIONS & VILLAGE STORIES
    // =========================================================================

    @GET("api/v1/collections")
    suspend fun getCollections(): Response<ApiResponse<List<CollectionDto>>>

    @GET("api/v1/collections/{slug}")
    suspend fun getCollectionBySlug(
        @Path("slug") slug: String
    ): Response<ApiResponse<CollectionDto>>

    @GET("api/v1/stories")
    suspend fun getVillageStories(
        @Query("state") state: String? = null
    ): Response<ApiResponse<List<VillageStoryDto>>>

    @GET("api/v1/stories/{slug}")
    suspend fun getVillageStoryBySlug(
        @Path("slug") slug: String
    ): Response<ApiResponse<VillageStoryDto>>

    // =========================================================================
    // 10. AUTHENTICATION, IDENTITY, SESSIONS & USER PROFILE
    // =========================================================================

    @POST("api/v1/auth/request-otp")
    suspend fun requestOtp(
        @Body request: RequestOtpRequestDto
    ): Response<ApiResponse<RequestOtpResponseDto>>

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequestDto
    ): Response<ApiResponse<AuthResponseDto>>

    @POST("api/v1/auth/email-login")
    suspend fun loginWithEmail(
        @Body request: EmailLoginRequestDto
    ): Response<ApiResponse<AuthResponseDto>>

    @POST("api/v1/auth/register")
    suspend fun registerPatron(
        @Body request: RegisterRequestDto
    ): Response<ApiResponse<AuthResponseDto>>

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequestDto
    ): Response<ApiResponse<AuthTokensDto>>

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<Boolean>>

    @GET("api/v1/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<UserProfileDto>>

    @PATCH("api/v1/me")
    suspend fun updateCurrentUserProfile(
        @Body request: UpdateProfileRequestDto,
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<UserProfileDto>>

    @DELETE("api/v1/me")
    suspend fun deleteUserAccount(
        @Body request: DeleteAccountRequestDto,
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<Boolean>>

    @GET("api/v1/me/sessions")
    suspend fun getUserSessions(
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<List<UserSessionDto>>>

    @DELETE("api/v1/me/sessions/{sessionId}")
    suspend fun revokeUserSession(
        @Path("sessionId") sessionId: String,
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<Boolean>>
}
