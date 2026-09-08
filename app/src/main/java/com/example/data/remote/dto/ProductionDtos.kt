package com.example.data.remote.dto

import com.example.data.models.production.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * =========================================================================
 * GAONOVA REST API v1 DATA TRANSFER OBJECTS (DTOs)
 * Strongly typed DTOs consumed from the backend /api/v1 endpoints.
 * Includes mapping functions to Domain Models for clean architecture.
 * =========================================================================
 */

// =========================================================================
// 1. STANDARD API RESPONSE & ERROR ENVELOPES
// =========================================================================

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data") val data: T? = null,
    @Json(name = "error") val error: ApiErrorDto? = null,
    @Json(name = "timestamp") val timestamp: String = System.currentTimeMillis().toString()
)

@JsonClass(generateAdapter = true)
data class ApiErrorDto(
    @Json(name = "code") val code: String,
    @Json(name = "message") val message: String,
    @Json(name = "details") val details: Map<String, String>? = null
)

object ErrorCode {
    const val VALIDATION_ERROR = "VALIDATION_ERROR"
    const val AUTH_REQUIRED = "AUTH_REQUIRED"
    const val NOT_FOUND = "NOT_FOUND"
    const val OUT_OF_STOCK = "OUT_OF_STOCK"
    const val PRICE_CHANGED = "PRICE_CHANGED"
    const val LOCATION_UNAVAILABLE = "LOCATION_UNAVAILABLE"
    const val SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"
    const val RATE_LIMITED = "RATE_LIMITED"
}

@JsonClass(generateAdapter = true)
data class PaginatedResponseDto<T>(
    @Json(name = "items") val items: List<T>,
    @Json(name = "page") val page: Int,
    @Json(name = "pageSize") val pageSize: Int,
    @Json(name = "totalItems") val totalItems: Int,
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "hasNext") val hasNext: Boolean
)

// =========================================================================
// 2. PRODUCT DTOs
// =========================================================================

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "id") val id: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "name") val name: String,
    @Json(name = "regionalName") val regionalName: String? = null,
    @Json(name = "shortDescription") val shortDescription: String,
    @Json(name = "longDescription") val longDescription: String,
    @Json(name = "categoryId") val categoryId: String,
    @Json(name = "categorySlug") val categorySlug: String,
    @Json(name = "categoryName") val categoryName: String,
    @Json(name = "subcategoryId") val subcategoryId: String? = null,
    @Json(name = "subcategoryName") val subcategoryName: String? = null,
    @Json(name = "artisanId") val artisanId: String,
    @Json(name = "artisanName") val artisanName: String,
    @Json(name = "sellerId") val sellerId: String,
    @Json(name = "sellerName") val sellerName: String,
    @Json(name = "brand") val brand: String = "Gaonova Heritage",
    @Json(name = "location") val location: LocationHierarchyDto,
    @Json(name = "productType") val productType: String = "PHYSICAL_CRAFT",
    @Json(name = "isHandmade") val isHandmade: Boolean = true,
    @Json(name = "isMadeToOrder") val isMadeToOrder: Boolean = false,
    @Json(name = "isGiftable") val isGiftable: Boolean = true,
    @Json(name = "isFeatured") val isFeatured: Boolean = false,
    @Json(name = "status") val status: String = "PUBLISHED",
    @Json(name = "pricing") val pricing: PriceDto,
    @Json(name = "inventory") val inventory: InventoryDto,
    @Json(name = "variants") val variants: List<ProductVariantDto> = emptyList(),
    @Json(name = "media") val media: List<ProductMediaDto> = emptyList(),
    @Json(name = "provenance") val provenance: ProductProvenanceDto,
    @Json(name = "knowledge") val knowledge: ProductKnowledgeDto,
    @Json(name = "attributes") val attributes: List<ProductAttributeDto> = emptyList(),
    @Json(name = "sources") val sources: List<ProductSourceDto> = emptyList(),
    @Json(name = "ratingsSummary") val ratingsSummary: RatingsSummaryDto = RatingsSummaryDto(),
    @Json(name = "tags") val tags: List<String> = emptyList(),
    @Json(name = "seoMetadata") val seoMetadata: SeoMetadataDto = SeoMetadataDto(),
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "updatedAt") val updatedAt: String,
    @Json(name = "publishedAt") val publishedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductVariantDto(
    @Json(name = "id") val id: String,
    @Json(name = "productId") val productId: String,
    @Json(name = "sku") val sku: String,
    @Json(name = "title") val title: String,
    @Json(name = "size") val size: String? = null,
    @Json(name = "colorName") val colorName: String? = null,
    @Json(name = "colorHex") val colorHex: Long? = null,
    @Json(name = "material") val material: String? = null,
    @Json(name = "dimensions") val dimensions: DimensionsDto? = null,
    @Json(name = "weightGrams") val weightGrams: Double? = null,
    @Json(name = "price") val price: PriceDto,
    @Json(name = "inventory") val inventory: InventoryDto,
    @Json(name = "media") val media: List<ProductMediaDto> = emptyList(),
    @Json(name = "status") val status: String = "ACTIVE"
)

@JsonClass(generateAdapter = true)
data class DimensionsDto(
    @Json(name = "lengthCm") val lengthCm: Double,
    @Json(name = "widthCm") val widthCm: Double,
    @Json(name = "heightCm") val heightCm: Double
)

@JsonClass(generateAdapter = true)
data class ProductDetailDto(
    @Json(name = "product") val product: ProductDto,
    @Json(name = "artisan") val artisan: ArtisanDto,
    @Json(name = "seller") val seller: SellerDto,
    @Json(name = "relatedProducts") val relatedProducts: List<ProductDto> = emptyList(),
    @Json(name = "recentReviews") val recentReviews: List<ReviewDto> = emptyList(),
    @Json(name = "deliveryPolicy") val deliveryPolicy: String = "Free insured shipping on orders above ₹2000. Dispatched within 48 hours from village cluster.",
    @Json(name = "returnPolicy") val returnPolicy: String = "7-day direct master craft return or replacement guarantee."
)

// =========================================================================
// 3. MEDIA DTOs
// =========================================================================

@JsonClass(generateAdapter = true)
data class ProductMediaDto(
    @Json(name = "id") val id: String,
    @Json(name = "mediaType") val mediaType: String = "IMAGE",
    @Json(name = "role") val role: String = "PRIMARY",
    @Json(name = "displayOrder") val displayOrder: Int = 0,
    @Json(name = "altText") val altText: String,
    @Json(name = "caption") val caption: String? = null,
    @Json(name = "width") val width: Int = 1200,
    @Json(name = "height") val height: Int = 1200,
    @Json(name = "mimeType") val mimeType: String = "image/webp",
    @Json(name = "urls") val urls: MediaResolutionsDto,
    @Json(name = "isPublished") val isPublished: Boolean = true
)

@JsonClass(generateAdapter = true)
data class MediaResolutionsDto(
    @Json(name = "original") val original: String,
    @Json(name = "large") val large: String,
    @Json(name = "medium") val medium: String,
    @Json(name = "thumbnail") val thumbnail: String
)

// =========================================================================
// 4. CATEGORY & LOCATION DTOs
// =========================================================================

@JsonClass(generateAdapter = true)
data class CategoryDto(
    @Json(name = "id") val id: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "parentId") val parentId: String? = null,
    @Json(name = "iconUrl") val iconUrl: String? = null,
    @Json(name = "iconSymbolName") val iconSymbolName: String = "category",
    @Json(name = "thumbnailUrl") val thumbnailUrl: String? = null,
    @Json(name = "heroImageUrl") val heroImageUrl: String? = null,
    @Json(name = "displayOrder") val displayOrder: Int = 0,
    @Json(name = "isActive") val isActive: Boolean = true,
    @Json(name = "isFeatured") val isFeatured: Boolean = false,
    @Json(name = "subcategories") val subcategories: List<CategoryDto> = emptyList(),
    @Json(name = "seoMetadata") val seoMetadata: SeoMetadataDto = SeoMetadataDto()
)

@JsonClass(generateAdapter = true)
data class LocationHierarchyDto(
    @Json(name = "country") val country: String = "India",
    @Json(name = "stateId") val stateId: String,
    @Json(name = "state") val state: String,
    @Json(name = "districtId") val districtId: String,
    @Json(name = "district") val district: String,
    @Json(name = "cityOrTown") val cityOrTown: String,
    @Json(name = "villageOrClusterId") val villageOrClusterId: String,
    @Json(name = "villageOrCluster") val villageOrCluster: String,
    @Json(name = "postalCode") val postalCode: String,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "artisanHubName") val artisanHubName: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "coverImageUrl") val coverImageUrl: String? = null,
    @Json(name = "status") val status: String = "ACTIVE"
)

// =========================================================================
// 5. ARTISAN & SELLER DTOs
// =========================================================================

@JsonClass(generateAdapter = true)
data class ArtisanDto(
    @Json(name = "id") val id: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "name") val name: String,
    @Json(name = "displayName") val displayName: String,
    @Json(name = "title") val title: String,
    @Json(name = "profileImageUrl") val profileImageUrl: String? = null,
    @Json(name = "coverImageUrl") val coverImageUrl: String? = null,
    @Json(name = "biography") val biography: String,
    @Json(name = "craftTradition") val craftTradition: String,
    @Json(name = "experienceYears") val experienceYears: Int,
    @Json(name = "lineageGenerations") val lineageGenerations: Int = 1,
    @Json(name = "location") val location: LocationHierarchyDto,
    @Json(name = "cooperativeId") val cooperativeId: String? = null,
    @Json(name = "cooperativeName") val cooperativeName: String? = null,
    @Json(name = "verificationStatus") val verificationStatus: String = "VERIFIED_GI_MASTER",
    @Json(name = "masterStory") val masterStory: String,
    @Json(name = "awards") val awards: List<String> = emptyList(),
    @Json(name = "communityImpact") val communityImpact: String,
    @Json(name = "themeColorHex") val themeColorHex: Long = 0xFF94442A,
    @Json(name = "isActive") val isActive: Boolean = true
)

@JsonClass(generateAdapter = true)
data class SellerDto(
    @Json(name = "id") val id: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "businessName") val businessName: String,
    @Json(name = "sellerType") val sellerType: String = "ARTISAN_COOPERATIVE",
    @Json(name = "logoUrl") val logoUrl: String? = null,
    @Json(name = "coverUrl") val coverUrl: String? = null,
    @Json(name = "description") val description: String,
    @Json(name = "verificationState") val verificationState: String = "FIELD_INSPECTED",
    @Json(name = "address") val address: String,
    @Json(name = "supportEmail") val supportEmail: String,
    @Json(name = "supportPhone") val supportPhone: String,
    @Json(name = "rating") val rating: Double = 4.8,
    @Json(name = "totalArtisansRepresented") val totalArtisansRepresented: Int = 1,
    @Json(name = "isActive") val isActive: Boolean = true
)

// =========================================================================
// 6. PROVENANCE, KNOWLEDGE & SOURCES DTOs
// =========================================================================

@JsonClass(generateAdapter = true)
data class ProductProvenanceDto(
    @Json(name = "productId") val productId: String,
    @Json(name = "artisanId") val artisanId: String,
    @Json(name = "craftTradition") val craftTradition: String,
    @Json(name = "locationHierarchy") val locationHierarchy: LocationHierarchyDto,
    @Json(name = "giRegistrationNo") val giRegistrationNo: String? = null,
    @Json(name = "giCertificateUrl") val giCertificateUrl: String? = null,
    @Json(name = "gazetteNotificationDate") val gazetteNotificationDate: String? = null,
    @Json(name = "authenticityScore") val authenticityScore: Int = 98,
    @Json(name = "authenticityChecklist") val authenticityChecklist: List<String> = emptyList(),
    @Json(name = "rawMaterialOrigin") val rawMaterialOrigin: String,
    @Json(name = "verificationTimestamp") val verificationTimestamp: String
)

@JsonClass(generateAdapter = true)
data class ProductKnowledgeDto(
    @Json(name = "historyAndOrigins") val historyAndOrigins: String,
    @Json(name = "culturalSignificance") val culturalSignificance: String,
    @Json(name = "manufacturingProcess") val manufacturingProcess: String,
    @Json(name = "rawMaterials") val rawMaterials: List<String> = emptyList(),
    @Json(name = "careInstructions") val careInstructions: String,
    @Json(name = "storageGuidelines") val storageGuidelines: String,
    @Json(name = "packagingDetails") val packagingDetails: String,
    @Json(name = "giftingEtiquette") val giftingEtiquette: String,
    @Json(name = "authenticityGuidelines") val authenticityGuidelines: String,
    @Json(name = "faqs") val faqs: List<KnowledgeFaqDto> = emptyList(),
    @Json(name = "lastVerifiedTimestamp") val lastVerifiedTimestamp: String
)

@JsonClass(generateAdapter = true)
data class KnowledgeFaqDto(
    @Json(name = "question") val question: String,
    @Json(name = "answer") val answer: String
)

@JsonClass(generateAdapter = true)
data class SourceDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "publisher") val publisher: String,
    @Json(name = "url") val url: String? = null,
    @Json(name = "sourceType") val sourceType: String,
    @Json(name = "publicationDate") val publicationDate: String,
    @Json(name = "retrievedDate") val retrievedDate: String,
    @Json(name = "lastVerifiedDate") val lastVerifiedDate: String,
    @Json(name = "reliabilityRating") val reliabilityRating: String = "High (Official/Government Archive)",
    @Json(name = "excerpt") val excerpt: String
)

@JsonClass(generateAdapter = true)
data class ProductSourceDto(
    @Json(name = "source") val source: SourceDto,
    @Json(name = "claimType") val claimType: String,
    @Json(name = "verificationStatus") val verificationStatus: String = "VERIFIED"
)

// =========================================================================
// 7. ATTRIBUTES & INVENTORY DTOs
// =========================================================================

@JsonClass(generateAdapter = true)
data class ProductAttributeDto(
    @Json(name = "key") val key: String,
    @Json(name = "displayName") val displayName: String,
    @Json(name = "value") val value: String,
    @Json(name = "attributeType") val attributeType: String
)

@JsonClass(generateAdapter = true)
data class InventoryDto(
    @Json(name = "availableQuantity") val availableQuantity: Int,
    @Json(name = "reservedQuantity") val reservedQuantity: Int = 0,
    @Json(name = "soldQuantity") val soldQuantity: Int = 0,
    @Json(name = "lowStockThreshold") val lowStockThreshold: Int = 3,
    @Json(name = "stockStatus") val stockStatus: String = "IN_STOCK",
    @Json(name = "allowBackorders") val allowBackorders: Boolean = false,
    @Json(name = "restockEstimatedDays") val restockEstimatedDays: Int? = null
)

@JsonClass(generateAdapter = true)
data class InventoryReservationRequestDto(
    @Json(name = "productId") val productId: String,
    @Json(name = "variantId") val variantId: String? = null,
    @Json(name = "quantity") val quantity: Int
)

@JsonClass(generateAdapter = true)
data class InventoryReservationResponseDto(
    @Json(name = "reservationToken") val reservationToken: String,
    @Json(name = "productId") val productId: String,
    @Json(name = "variantId") val variantId: String? = null,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "expiresAtTimestamp") val expiresAtTimestamp: Long,
    @Json(name = "isConfirmed") val isConfirmed: Boolean
)

// =========================================================================
// 8. PRICING & REVIEWS DTOs
// =========================================================================

@JsonClass(generateAdapter = true)
data class PriceDto(
    @Json(name = "mrp") val mrp: Double,
    @Json(name = "sellingPrice") val sellingPrice: Double,
    @Json(name = "currency") val currency: String = "INR",
    @Json(name = "discountPercentage") val discountPercentage: Int = 0,
    @Json(name = "validFrom") val validFrom: String? = null,
    @Json(name = "validTo") val validTo: String? = null,
    @Json(name = "priceSource") val priceSource: String = "Direct Artisan Fair Trade Pricing",
    @Json(name = "priceHistory") val priceHistory: List<PriceHistoryPointDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PriceHistoryPointDto(
    @Json(name = "recordedDate") val recordedDate: String,
    @Json(name = "price") val price: Double,
    @Json(name = "eventNote") val eventNote: String? = null
)

@JsonClass(generateAdapter = true)
data class RatingsSummaryDto(
    @Json(name = "averageRating") val averageRating: Double = 4.8,
    @Json(name = "totalReviews") val totalReviews: Int = 0,
    @Json(name = "ratingBreakdown") val ratingBreakdown: Map<Int, Int> = emptyMap(),
    @Json(name = "aiCraftSummary") val aiCraftSummary: String? = null
)

@JsonClass(generateAdapter = true)
data class ReviewDto(
    @Json(name = "id") val id: String,
    @Json(name = "productId") val productId: String,
    @Json(name = "userId") val userId: String,
    @Json(name = "userName") val userName: String,
    @Json(name = "userAvatarUrl") val userAvatarUrl: String? = null,
    @Json(name = "rating") val rating: Int,
    @Json(name = "headline") val headline: String,
    @Json(name = "reviewText") val reviewText: String,
    @Json(name = "isVerifiedPurchase") val isVerifiedPurchase: Boolean = true,
    @Json(name = "mediaUrls") val mediaUrls: List<String> = emptyList(),
    @Json(name = "helpfulVotesCount") val helpfulVotesCount: Int = 0,
    @Json(name = "moderationStatus") val moderationStatus: String = "APPROVED",
    @Json(name = "createdAt") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class ReviewSubmissionDto(
    @Json(name = "productId") val productId: String,
    @Json(name = "orderItemId") val orderItemId: String? = null,
    @Json(name = "rating") val rating: Int,
    @Json(name = "headline") val headline: String,
    @Json(name = "reviewText") val reviewText: String,
    @Json(name = "mediaUrls") val mediaUrls: List<String> = emptyList()
)

// =========================================================================
// 9. COLLECTIONS, STORIES & HOME FEED DTOs
// =========================================================================

@JsonClass(generateAdapter = true)
data class CollectionDto(
    @Json(name = "id") val id: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "title") val title: String,
    @Json(name = "subtitle") val subtitle: String,
    @Json(name = "description") val description: String,
    @Json(name = "coverImageUrl") val coverImageUrl: String,
    @Json(name = "themeColorHex") val themeColorHex: Long = 0xFF7C2D12,
    @Json(name = "productIds") val productIds: List<String> = emptyList(),
    @Json(name = "isFeatured") val isFeatured: Boolean = false,
    @Json(name = "publishedAt") val publishedAt: String
)

@JsonClass(generateAdapter = true)
data class VillageStoryDto(
    @Json(name = "id") val id: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "title") val title: String,
    @Json(name = "craftTradition") val craftTradition: String,
    @Json(name = "location") val location: LocationHierarchyDto,
    @Json(name = "artisanId") val artisanId: String? = null,
    @Json(name = "summary") val summary: String,
    @Json(name = "fullStory") val fullStory: String,
    @Json(name = "heroImageUrl") val heroImageUrl: String,
    @Json(name = "galleryImages") val galleryImages: List<String> = emptyList(),
    @Json(name = "readTimeMinutes") val readTimeMinutes: Int = 3,
    @Json(name = "publishedAt") val publishedAt: String
)

@JsonClass(generateAdapter = true)
data class HomeFeedDto(
    @Json(name = "greeting") val greeting: String,
    @Json(name = "currentLocation") val currentLocation: LocationHierarchyDto,
    @Json(name = "activeBanners") val activeBanners: List<CollectionDto> = emptyList(),
    @Json(name = "categories") val categories: List<CategoryDto> = emptyList(),
    @Json(name = "regionalCraftsSpotlight") val regionalCraftsSpotlight: List<ProductDto> = emptyList(),
    @Json(name = "recommendedForUser") val recommendedForUser: List<ProductDto> = emptyList(),
    @Json(name = "artisanSpotlight") val artisanSpotlight: ArtisanDto? = null,
    @Json(name = "rotatingVillageStories") val rotatingVillageStories: List<VillageStoryDto> = emptyList(),
    @Json(name = "verifiedPriceDrops") val verifiedPriceDrops: List<ProductDto> = emptyList(),
    @Json(name = "curatedCollections") val curatedCollections: List<CollectionDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SeoMetadataDto(
    @Json(name = "title") val title: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "keywords") val keywords: List<String> = emptyList(),
    @Json(name = "ogImageUrl") val ogImageUrl: String? = null,
    @Json(name = "canonicalUrl") val canonicalUrl: String? = null
)

// =========================================================================
// 10. MAPPER EXTENSIONS (DTO -> DOMAIN)
// =========================================================================

fun LocationHierarchyDto.toDomain() = LocationHierarchy(
    country = country,
    stateId = stateId,
    state = state,
    districtId = districtId,
    district = district,
    cityOrTown = cityOrTown,
    villageOrClusterId = villageOrClusterId,
    villageOrCluster = villageOrCluster,
    postalCode = postalCode,
    latitude = latitude,
    longitude = longitude,
    artisanHubName = artisanHubName,
    description = description,
    coverImageUrl = coverImageUrl,
    status = try { LocationStatusType.valueOf(status) } catch (_: Exception) { LocationStatusType.ACTIVE }
)

fun PriceDto.toDomain() = PriceDomain(
    mrp = mrp,
    sellingPrice = sellingPrice,
    currency = currency,
    discountPercentage = discountPercentage,
    validFrom = validFrom,
    validTo = validTo,
    priceSource = priceSource,
    priceHistory = priceHistory.map { PriceHistoryPoint(it.recordedDate, it.price, it.eventNote) }
)

fun InventoryDto.toDomain() = InventoryDomain(
    availableQuantity = availableQuantity,
    reservedQuantity = reservedQuantity,
    soldQuantity = soldQuantity,
    lowStockThreshold = lowStockThreshold,
    stockStatus = try { StockStatus.valueOf(stockStatus) } catch (_: Exception) { StockStatus.IN_STOCK },
    allowBackorders = allowBackorders,
    restockEstimatedDays = restockEstimatedDays
)

fun ProductMediaDto.toDomain() = ProductMedia(
    id = id,
    mediaType = try { MediaType.valueOf(mediaType) } catch (_: Exception) { MediaType.IMAGE },
    role = try { MediaRole.valueOf(role) } catch (_: Exception) { MediaRole.PRIMARY },
    displayOrder = displayOrder,
    altText = altText,
    caption = caption,
    width = width,
    height = height,
    mimeType = mimeType,
    urls = MediaResolutions(urls.original, urls.large, urls.medium, urls.thumbnail),
    isPublished = isPublished
)

fun CategoryDto.toDomain(): CategoryDomain = CategoryDomain(
    id = id,
    slug = slug,
    name = name,
    description = description,
    parentId = parentId,
    iconUrl = iconUrl,
    iconSymbolName = iconSymbolName,
    thumbnailUrl = thumbnailUrl,
    heroImageUrl = heroImageUrl,
    displayOrder = displayOrder,
    isActive = isActive,
    isFeatured = isFeatured,
    subcategories = subcategories.map { it.toDomain() },
    seoMetadata = SeoMetadata(seoMetadata.title, seoMetadata.description, seoMetadata.keywords, seoMetadata.ogImageUrl, seoMetadata.canonicalUrl)
)

fun ArtisanDto.toDomain() = ArtisanDomain(
    id = id,
    slug = slug,
    name = name,
    displayName = displayName,
    title = title,
    profileImageUrl = profileImageUrl,
    coverImageUrl = coverImageUrl,
    biography = biography,
    craftTradition = craftTradition,
    experienceYears = experienceYears,
    lineageGenerations = lineageGenerations,
    location = location.toDomain(),
    cooperativeId = cooperativeId,
    cooperativeName = cooperativeName,
    verificationStatus = try { VerificationStatus.valueOf(verificationStatus) } catch (_: Exception) { VerificationStatus.VERIFIED_GI_MASTER },
    masterStory = masterStory,
    awards = awards,
    communityImpact = communityImpact,
    themeColorHex = themeColorHex,
    isActive = isActive
)

fun SellerDto.toDomain() = SellerDomain(
    id = id,
    slug = slug,
    businessName = businessName,
    sellerType = try { SellerType.valueOf(sellerType) } catch (_: Exception) { SellerType.ARTISAN_COOPERATIVE },
    logoUrl = logoUrl,
    coverUrl = coverUrl,
    description = description,
    verificationState = try { VerificationStatus.valueOf(verificationState) } catch (_: Exception) { VerificationStatus.FIELD_INSPECTED },
    address = address,
    supportEmail = supportEmail,
    supportPhone = supportPhone,
    rating = rating,
    totalArtisansRepresented = totalArtisansRepresented,
    isActive = isActive
)

fun ProductProvenanceDto.toDomain() = ProductProvenanceDomain(
    productId = productId,
    artisanId = artisanId,
    craftTradition = craftTradition,
    locationHierarchy = locationHierarchy.toDomain(),
    giRegistrationNo = giRegistrationNo,
    giCertificateUrl = giCertificateUrl,
    gazetteNotificationDate = gazetteNotificationDate,
    authenticityScore = authenticityScore,
    authenticityChecklist = authenticityChecklist,
    rawMaterialOrigin = rawMaterialOrigin,
    verificationTimestamp = verificationTimestamp
)

fun ProductKnowledgeDto.toDomain() = ProductKnowledgeDomain(
    historyAndOrigins = historyAndOrigins,
    culturalSignificance = culturalSignificance,
    manufacturingProcess = manufacturingProcess,
    rawMaterials = rawMaterials,
    careInstructions = careInstructions,
    storageGuidelines = storageGuidelines,
    packagingDetails = packagingDetails,
    giftingEtiquette = giftingEtiquette,
    authenticityGuidelines = authenticityGuidelines,
    faqs = faqs.map { KnowledgeFaq(it.question, it.answer) },
    lastVerifiedTimestamp = lastVerifiedTimestamp
)

fun SourceDto.toDomain() = SourceDomain(
    id = id,
    title = title,
    publisher = publisher,
    url = url,
    sourceType = try { SourceDomainType.valueOf(sourceType) } catch (_: Exception) { SourceDomainType.GOVERNMENT_GI_REGISTRY },
    publicationDate = publicationDate,
    retrievedDate = retrievedDate,
    lastVerifiedDate = lastVerifiedDate,
    reliabilityRating = reliabilityRating,
    excerpt = excerpt
)

fun ProductDto.toDomain() = ProductionProduct(
    id = id,
    slug = slug,
    name = name,
    regionalName = regionalName,
    shortDescription = shortDescription,
    longDescription = longDescription,
    categoryId = categoryId,
    categorySlug = categorySlug,
    categoryName = categoryName,
    subcategoryId = subcategoryId,
    subcategoryName = subcategoryName,
    artisanId = artisanId,
    artisanName = artisanName,
    sellerId = sellerId,
    sellerName = sellerName,
    brand = brand,
    location = location.toDomain(),
    productType = try { ProductType.valueOf(productType) } catch (_: Exception) { ProductType.PHYSICAL_CRAFT },
    isHandmade = isHandmade,
    isMadeToOrder = isMadeToOrder,
    isGiftable = isGiftable,
    isFeatured = isFeatured,
    status = try { ProductStatus.valueOf(status) } catch (_: Exception) { ProductStatus.PUBLISHED },
    pricing = pricing.toDomain(),
    inventory = inventory.toDomain(),
    variants = variants.map { variant ->
        ProductVariant(
            id = variant.id,
            productId = variant.productId,
            sku = variant.sku,
            title = variant.title,
            size = variant.size,
            colorName = variant.colorName,
            colorHex = variant.colorHex,
            material = variant.material,
            dimensions = variant.dimensions?.let { Dimensions(it.lengthCm, it.widthCm, it.heightCm) },
            weightGrams = variant.weightGrams,
            price = variant.price.toDomain(),
            inventory = variant.inventory.toDomain(),
            media = variant.media.map { it.toDomain() },
            status = try { VariantStatus.valueOf(variant.status) } catch (_: Exception) { VariantStatus.ACTIVE }
        )
    },
    media = media.map { it.toDomain() },
    provenance = provenance.toDomain(),
    knowledge = knowledge.toDomain(),
    attributes = attributes.map { attr ->
        ProductAttributeDomain(
            key = attr.key,
            displayName = attr.displayName,
            value = attr.value,
            attributeType = try { AttributeType.valueOf(attr.attributeType) } catch (_: Exception) { AttributeType.CRAFT_GENRE }
        )
    },
    sources = sources.map { s ->
        ProductSourceDomain(
            source = s.source.toDomain(),
            claimType = s.claimType,
            verificationStatus = s.verificationStatus
        )
    },
    ratingsSummary = RatingsSummary(
        averageRating = ratingsSummary.averageRating,
        totalReviews = ratingsSummary.totalReviews,
        ratingBreakdown = ratingsSummary.ratingBreakdown,
        aiCraftSummary = ratingsSummary.aiCraftSummary
    ),
    tags = tags,
    seoMetadata = SeoMetadata(seoMetadata.title, seoMetadata.description, seoMetadata.keywords, seoMetadata.ogImageUrl, seoMetadata.canonicalUrl),
    createdAt = createdAt,
    updatedAt = updatedAt,
    publishedAt = publishedAt
)

fun CollectionDto.toDomain() = CollectionDomain(
    id = id,
    slug = slug,
    title = title,
    subtitle = subtitle,
    description = description,
    coverImageUrl = coverImageUrl,
    themeColorHex = themeColorHex,
    productIds = productIds,
    isFeatured = isFeatured,
    publishedAt = publishedAt
)

fun VillageStoryDto.toDomain() = VillageStoryDomain(
    id = id,
    slug = slug,
    title = title,
    craftTradition = craftTradition,
    location = location.toDomain(),
    artisanId = artisanId,
    summary = summary,
    fullStory = fullStory,
    heroImageUrl = heroImageUrl,
    galleryImages = galleryImages,
    readTimeMinutes = readTimeMinutes,
    publishedAt = publishedAt
)

fun HomeFeedDto.toDomain() = HomeFeedDomain(
    greeting = greeting,
    currentLocation = currentLocation.toDomain(),
    activeBanners = activeBanners.map { it.toDomain() },
    categories = categories.map { it.toDomain() },
    regionalCraftsSpotlight = regionalCraftsSpotlight.map { it.toDomain() },
    recommendedForUser = recommendedForUser.map { it.toDomain() },
    artisanSpotlight = artisanSpotlight?.toDomain(),
    rotatingVillageStories = rotatingVillageStories.map { it.toDomain() },
    verifiedPriceDrops = verifiedPriceDrops.map { it.toDomain() },
    curatedCollections = curatedCollections.map { it.toDomain() }
)
