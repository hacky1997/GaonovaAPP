package com.example.data.remote.api

import com.example.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * =============================================================================
 * CATEGORY API SERVICE (RETROFIT)
 * Dedicated contract for craft category taxonomies, subcategories,
 * category hierarchy, and category-filtered product listings.
 * =============================================================================
 */
interface CategoryApiService {

    /**
     * Retrieve all primary craft categories (e.g., Terracotta, Kantha, Dokra, Handloom).
     */
    @GET("api/v1/categories")
    suspend fun getCategories(
        @Query("includeSubcategories") includeSubcategories: Boolean = true
    ): Response<ApiResponse<List<CategoryDto>>>

    /**
     * Fetch a specific category by its unique URL slug.
     */
    @GET("api/v1/categories/{slug}")
    suspend fun getCategoryBySlug(
        @Path("slug") slug: String
    ): Response<ApiResponse<CategoryDto>>

    /**
     * Fetch a category by its internal ID.
     */
    @GET("api/v1/categories/by-id/{id}")
    suspend fun getCategoryById(
        @Path("id") id: String
    ): Response<ApiResponse<CategoryDto>>

    /**
     * Retrieve all subcategories for a given parent category slug.
     */
    @GET("api/v1/categories/{slug}/subcategories")
    suspend fun getSubcategories(
        @Path("slug") slug: String
    ): Response<ApiResponse<List<CategoryDto>>>

    /**
     * Fetch paginated products belonging to a specific category.
     */
    @GET("api/v1/categories/{slug}/products")
    suspend fun getCategoryProducts(
        @Path("slug") slug: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sort") sort: String? = null
    ): Response<ApiResponse<PaginatedResponseDto<ProductDto>>>

    /**
     * Fetch featured/highlighted categories for promotional banners & discovery shelves.
     */
    @GET("api/v1/categories/featured")
    suspend fun getFeaturedCategories(): Response<ApiResponse<List<CategoryDto>>>
}
