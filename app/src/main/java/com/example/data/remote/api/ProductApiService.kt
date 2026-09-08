package com.example.data.remote.api

import com.example.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * =============================================================================
 * PRODUCT API SERVICE (RETROFIT)
 * Dedicated contract for product discovery, catalog queries, search,
 * regional GI craft filtering, artisan catalog listings, and product details.
 * =============================================================================
 */
interface ProductApiService {

    /**
     * Retrieve paginated catalog products with comprehensive filtering and sorting.
     */
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
        @Query("cluster") craftCluster: String? = null,
        @Query("sort") sort: String? = "featured", // "featured", "price_asc", "price_desc", "rating", "newest"
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<PaginatedResponseDto<ProductDto>>>

    /**
     * Fetch full product details including provenance, artisan bio, seller credentials,
     * recent reviews, and related craft recommendations.
     */
    @GET("api/v1/products/{slug}/details")
    suspend fun getProductDetails(
        @Path("slug") slug: String
    ): Response<ApiResponse<ProductDetailDto>>

    /**
     * Fetch a single product by unique identifier.
     */
    @GET("api/v1/products/by-id/{id}")
    suspend fun getProductById(
        @Path("id") id: String
    ): Response<ApiResponse<ProductDto>>

    /**
     * Semantic and keyword search across craft titles, artisan names, materials, and tags.
     */
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

    /**
     * Fetch curated featured heritage products for the home screen and highlights.
     */
    @GET("api/v1/products/featured")
    suspend fun getFeaturedProducts(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<ProductDto>>>

    /**
     * Fetch trending and top-rated artisan products.
     */
    @GET("api/v1/products/trending")
    suspend fun getTrendingProducts(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<ProductDto>>>

    /**
     * Fetch products crafted by a specific artisan or guild.
     */
    @GET("api/v1/artisans/{artisanId}/products")
    suspend fun getProductsByArtisan(
        @Path("artisanId") artisanId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<PaginatedResponseDto<ProductDto>>>

    /**
     * Fetch authentic products belonging to a specific GI craft cluster / location.
     */
    @GET("api/v1/locations/{locationId}/products")
    suspend fun getProductsByLocation(
        @Path("locationId") locationId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<PaginatedResponseDto<ProductDto>>>

    /**
     * Fetch reviews and community feedback for a product.
     */
    @GET("api/v1/products/{productId}/reviews")
    suspend fun getProductReviews(
        @Path("productId") productId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<ApiResponse<PaginatedResponseDto<ReviewDto>>>
}
