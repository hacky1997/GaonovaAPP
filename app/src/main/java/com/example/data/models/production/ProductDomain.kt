package com.example.data.models.production

import java.util.UUID

/**
 * =========================================================================
 * GAONOVA PRODUCTION DOMAIN ARCHITECTURE
 * Clean, decoupled domain models representing backend-driven entities.
 * Android renders these entities dynamically without hardcoded catalog assumptions.
 * =========================================================================
 */

// =========================================================================
// 1. PRODUCT & VARIANTS
// =========================================================================

data class ProductionProduct(
    val id: String,
    val slug: String,
    val name: String,
    val regionalName: String? = null,
    val shortDescription: String,
    val longDescription: String,
    val categoryId: String,
    val categorySlug: String,
    val categoryName: String,
    val subcategoryId: String? = null,
    val subcategoryName: String? = null,
    val artisanId: String,
    val artisanName: String,
    val sellerId: String,
    val sellerName: String,
    val brand: String = "Gaonova Heritage",
    val location: LocationHierarchy,
    val productType: ProductType = ProductType.PHYSICAL_CRAFT,
    val isHandmade: Boolean = true,
    val isMadeToOrder: Boolean = false,
    val isGiftable: Boolean = true,
    val isFeatured: Boolean = false,
    val status: ProductStatus = ProductStatus.PUBLISHED,
    val pricing: PriceDomain,
    val inventory: InventoryDomain,
    val variants: List<ProductVariant> = emptyList(),
    val media: List<ProductMedia> = emptyList(),
    val provenance: ProductProvenanceDomain,
    val knowledge: ProductKnowledgeDomain,
    val attributes: List<ProductAttributeDomain> = emptyList(),
    val sources: List<ProductSourceDomain> = emptyList(),
    val ratingsSummary: RatingsSummary = RatingsSummary(),
    val tags: List<String> = emptyList(),
    val seoMetadata: SeoMetadata = SeoMetadata(),
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String? = null
) {
    val primaryImage: ProductMedia?
        get() = media.firstOrNull { it.role == MediaRole.PRIMARY } ?: media.firstOrNull()
}

enum class ProductType {
    PHYSICAL_CRAFT,
    HERITAGE_EDIBLE,
    TEXTILE_LOOM,
    METALLURGY_ART,
    NATURAL_ESSENCE,
    TERRACOTTA_POTTERY
}

enum class ProductStatus {
    DRAFT,
    UNDER_AUTHENTICATION,
    PUBLISHED,
    UNAVAILABLE,
    ARCHIVED
}

data class ProductVariant(
    val id: String,
    val productId: String,
    val sku: String,
    val title: String,
    val size: String? = null,
    val colorName: String? = null,
    val colorHex: Long? = null,
    val material: String? = null,
    val dimensions: Dimensions? = null,
    val weightGrams: Double? = null,
    val price: PriceDomain,
    val inventory: InventoryDomain,
    val media: List<ProductMedia> = emptyList(),
    val status: VariantStatus = VariantStatus.ACTIVE
)

enum class VariantStatus {
    ACTIVE,
    OUT_OF_STOCK,
    DISCONTINUED
}

data class Dimensions(
    val lengthCm: Double,
    val widthCm: Double,
    val heightCm: Double
)

// =========================================================================
// 2. PRODUCT MEDIA & IMAGE PIPELINE
// =========================================================================

data class ProductMedia(
    val id: String,
    val mediaType: MediaType = MediaType.IMAGE,
    val role: MediaRole = MediaRole.PRIMARY,
    val displayOrder: Int = 0,
    val altText: String,
    val caption: String? = null,
    val width: Int = 1200,
    val height: Int = 1200,
    val mimeType: String = "image/webp",
    val urls: MediaResolutions,
    val isPublished: Boolean = true
)

enum class MediaType {
    IMAGE,
    VIDEO,
    DOCUMENT,
    PANORAMA_360
}

enum class MediaRole {
    PRIMARY,
    SECONDARY,
    DETAIL,
    LIFESTYLE,
    ARTISAN,
    MAKING_PROCESS,
    PACKAGING,
    CERTIFICATION,
    VIDEO_THUMBNAIL
}

data class MediaResolutions(
    val original: String,
    val large: String,
    val medium: String,
    val thumbnail: String
) {
    fun urlForContext(context: MediaRenderContext): String {
        return when (context) {
            MediaRenderContext.HOME_GRID, MediaRenderContext.MINI_CART -> thumbnail
            MediaRenderContext.SEARCH_LIST, MediaRenderContext.CATEGORY_BROWSE -> medium
            MediaRenderContext.DETAIL_HERO, MediaRenderContext.FULLSCREEN_GALLERY -> large
            MediaRenderContext.ORIGINAL_DOWNLOAD -> original
        }
    }
}

enum class MediaRenderContext {
    HOME_GRID,
    SEARCH_LIST,
    CATEGORY_BROWSE,
    DETAIL_HERO,
    FULLSCREEN_GALLERY,
    MINI_CART,
    ORIGINAL_DOWNLOAD
}

// =========================================================================
// 3. CATEGORY HIERARCHY
// =========================================================================

data class CategoryDomain(
    val id: String,
    val slug: String,
    val name: String,
    val description: String,
    val parentId: String? = null,
    val iconUrl: String? = null,
    val iconSymbolName: String = "category",
    val thumbnailUrl: String? = null,
    val heroImageUrl: String? = null,
    val displayOrder: Int = 0,
    val isActive: Boolean = true,
    val isFeatured: Boolean = false,
    val subcategories: List<CategoryDomain> = emptyList(),
    val seoMetadata: SeoMetadata = SeoMetadata()
)

// =========================================================================
// 4. HIERARCHICAL GEOGRAPHIC LOCATION
// =========================================================================

data class LocationHierarchy(
    val country: String = "India",
    val stateId: String,
    val state: String,
    val districtId: String,
    val district: String,
    val cityOrTown: String,
    val villageOrClusterId: String,
    val villageOrCluster: String,
    val postalCode: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val artisanHubName: String? = null,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val status: LocationStatusType = LocationStatusType.ACTIVE
) {
    fun formattedHierarchy(): String = "$villageOrCluster, $district, $state"
}

enum class LocationStatusType {
    ACTIVE,
    PENDING_VERIFICATION,
    DELIVERY_RESTRICTED
}

// =========================================================================
// 5. ARTISAN & SELLER
// =========================================================================

data class ArtisanDomain(
    val id: String,
    val slug: String,
    val name: String,
    val displayName: String,
    val title: String,
    val profileImageUrl: String? = null,
    val coverImageUrl: String? = null,
    val biography: String,
    val craftTradition: String,
    val experienceYears: Int,
    val lineageGenerations: Int = 1,
    val location: LocationHierarchy,
    val cooperativeId: String? = null,
    val cooperativeName: String? = null,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED_GI_MASTER,
    val masterStory: String,
    val awards: List<String> = emptyList(),
    val communityImpact: String,
    val themeColorHex: Long = 0xFF94442A,
    val isActive: Boolean = true
)

enum class VerificationStatus {
    UNVERIFIED,
    DOCUMENTS_SUBMITTED,
    FIELD_INSPECTED,
    VERIFIED_GI_MASTER,
    NATIONAL_SHILP_GURU
}

data class SellerDomain(
    val id: String,
    val slug: String,
    val businessName: String,
    val sellerType: SellerType = SellerType.ARTISAN_COOPERATIVE,
    val logoUrl: String? = null,
    val coverUrl: String? = null,
    val description: String,
    val verificationState: VerificationStatus = VerificationStatus.FIELD_INSPECTED,
    val address: String,
    val supportEmail: String,
    val supportPhone: String,
    val rating: Double = 4.8,
    val totalArtisansRepresented: Int = 1,
    val isActive: Boolean = true
)

enum class SellerType {
    DIRECT_ARTISAN_FAMILY,
    ARTISAN_COOPERATIVE,
    SELF_HELP_GROUP_FEDERATION,
    HERITAGE_FOUNDATION,
    STATE_HANDLOOM_CORPORATION
}

// =========================================================================
// 6. PROVENANCE, KNOWLEDGE & SOURCES
// =========================================================================

data class ProductProvenanceDomain(
    val productId: String,
    val artisanId: String,
    val craftTradition: String,
    val locationHierarchy: LocationHierarchy,
    val giRegistrationNo: String? = null,
    val giCertificateUrl: String? = null,
    val gazetteNotificationDate: String? = null,
    val authenticityScore: Int = 98,
    val authenticityChecklist: List<String> = emptyList(),
    val rawMaterialOrigin: String,
    val verificationTimestamp: String
)

data class ProductKnowledgeDomain(
    val historyAndOrigins: String,
    val culturalSignificance: String,
    val manufacturingProcess: String,
    val rawMaterials: List<String> = emptyList(),
    val careInstructions: String,
    val storageGuidelines: String,
    val packagingDetails: String,
    val giftingEtiquette: String,
    val authenticityGuidelines: String,
    val faqs: List<KnowledgeFaq> = emptyList(),
    val lastVerifiedTimestamp: String
)

data class KnowledgeFaq(
    val question: String,
    val answer: String
)

data class SourceDomain(
    val id: String,
    val title: String,
    val publisher: String,
    val url: String? = null,
    val sourceType: SourceDomainType,
    val publicationDate: String,
    val retrievedDate: String,
    val lastVerifiedDate: String,
    val reliabilityRating: String = "High (Official/Government Archive)",
    val excerpt: String
)

enum class SourceDomainType {
    GOVERNMENT_GI_REGISTRY,
    CRAFTS_COUNCIL_INDIA,
    TEXTILE_MINISTRY_ARCHIVE,
    ACADEMIC_RESEARCH_PEER_REVIEWED,
    ARTISAN_COOPERATIVE_PRIMARY_RECORD,
    HISTORICAL_CHRONICLE
}

data class ProductSourceDomain(
    val source: SourceDomain,
    val claimType: String,
    val verificationStatus: String = "VERIFIED"
)

// =========================================================================
// 7. ATTRIBUTES
// =========================================================================

data class ProductAttributeDomain(
    val key: String,
    val displayName: String,
    val value: String,
    val attributeType: AttributeType
)

enum class AttributeType {
    MATERIAL,
    TECHNIQUE,
    CRAFT_GENRE,
    ORIGIN_GEO,
    PRODUCTION_TYPE,
    GENDER_SUITABILITY,
    GIFT_SUITABILITY,
    SHELF_LIFE,
    CARE_REQUIREMENT
}

// =========================================================================
// 8. INVENTORY & RESERVATIONS
// =========================================================================

data class InventoryDomain(
    val availableQuantity: Int,
    val reservedQuantity: Int = 0,
    val soldQuantity: Int = 0,
    val lowStockThreshold: Int = 3,
    val stockStatus: StockStatus = StockStatus.IN_STOCK,
    val allowBackorders: Boolean = false,
    val restockEstimatedDays: Int? = null
) {
    val isPurchasable: Boolean
        get() = stockStatus == StockStatus.IN_STOCK || stockStatus == StockStatus.LOW_STOCK || stockStatus == StockStatus.MADE_TO_ORDER
}

enum class StockStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK,
    MADE_TO_ORDER,
    SEASONAL_REST
}

data class InventoryReservationDomain(
    val reservationToken: String = UUID.randomUUID().toString(),
    val productId: String,
    val variantId: String? = null,
    val quantity: Int,
    val expiresAtTimestamp: Long,
    val isConfirmed: Boolean = false
)

// =========================================================================
// 9. PRICING & HISTORICAL INTELLIGENCE
// =========================================================================

data class PriceDomain(
    val mrp: Double,
    val sellingPrice: Double,
    val currency: String = "INR",
    val discountPercentage: Int = 0,
    val validFrom: String? = null,
    val validTo: String? = null,
    val priceSource: String = "Direct Artisan Fair Trade Pricing",
    val priceHistory: List<PriceHistoryPoint> = emptyList()
) {
    val isDiscounted: Boolean
        get() = mrp > sellingPrice
}

data class PriceHistoryPoint(
    val recordedDate: String,
    val price: Double,
    val eventNote: String? = null
)

// =========================================================================
// 10. REVIEWS & RATINGS
// =========================================================================

data class RatingsSummary(
    val averageRating: Double = 4.8,
    val totalReviews: Int = 0,
    val ratingBreakdown: Map<Int, Int> = emptyMap(),
    val aiCraftSummary: String? = null
)

data class ReviewDomain(
    val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String? = null,
    val rating: Int,
    val headline: String,
    val reviewText: String,
    val isVerifiedPurchase: Boolean = true,
    val mediaUrls: List<String> = emptyList(),
    val helpfulVotesCount: Int = 0,
    val moderationStatus: ReviewModerationStatus = ReviewModerationStatus.APPROVED,
    val createdAt: String
)

enum class ReviewModerationStatus {
    PENDING,
    APPROVED,
    FLAGGED,
    REJECTED
}

// =========================================================================
// 11. PRODUCT RELATIONS, COLLECTIONS & STORIES
// =========================================================================

data class ProductRelationDomain(
    val relationType: RelationType,
    val relatedProductId: String,
    val matchScore: Double = 1.0,
    val description: String? = null
)

enum class RelationType {
    SIMILAR_CRAFT,
    ALTERNATIVE_BUDGET,
    PREMIUM_MASTERPIECE,
    REGIONAL_COMPANION,
    FREQUENTLY_BOUGHT_TOGETHER,
    GIFT_PAIRING
}

data class CollectionDomain(
    val id: String,
    val slug: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val coverImageUrl: String,
    val themeColorHex: Long = 0xFF7C2D12,
    val productIds: List<String> = emptyList(),
    val isFeatured: Boolean = false,
    val publishedAt: String
)

data class VillageStoryDomain(
    val id: String,
    val slug: String,
    val title: String,
    val craftTradition: String,
    val location: LocationHierarchy,
    val artisanId: String? = null,
    val summary: String,
    val fullStory: String,
    val heroImageUrl: String,
    val galleryImages: List<String> = emptyList(),
    val readTimeMinutes: Int = 3,
    val publishedAt: String
)

// =========================================================================
// 12. HOME FEED ENVELOPE
// =========================================================================

data class HomeFeedDomain(
    val greeting: String,
    val currentLocation: LocationHierarchy,
    val activeBanners: List<CollectionDomain> = emptyList(),
    val categories: List<CategoryDomain> = emptyList(),
    val regionalCraftsSpotlight: List<ProductionProduct> = emptyList(),
    val recommendedForUser: List<ProductionProduct> = emptyList(),
    val artisanSpotlight: ArtisanDomain? = null,
    val rotatingVillageStories: List<VillageStoryDomain> = emptyList(),
    val verifiedPriceDrops: List<ProductionProduct> = emptyList(),
    val curatedCollections: List<CollectionDomain> = emptyList()
)

// =========================================================================
// 13. SEO & METADATA
// =========================================================================

data class SeoMetadata(
    val title: String = "",
    val description: String = "",
    val keywords: List<String> = emptyList(),
    val ogImageUrl: String? = null,
    val canonicalUrl: String? = null
)
