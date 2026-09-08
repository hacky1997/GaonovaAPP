package com.example.data.models

data class Product(
    val id: String,
    val name: String,
    val regionalName: String = "",
    val category: ProductCategory,
    val state: String,
    val district: String,
    val villageOrCluster: String,
    val price: Double,
    val originalPrice: Double,
    val rating: Double,
    val reviewCount: Int,
    val giCertified: Boolean = false,
    val giRegistrationNo: String? = null,
    val artisanId: String,
    val artisanName: String,
    val stockCount: Int,
    val shortDescription: String,
    // The 5 Core PRD Pillars (WHAT, WHERE, WHY, WHO, HOW)
    val whatDescription: String,
    val whereContext: String,
    val whySignificance: String,
    val whoArtisanStory: String,
    val howCareAndBuy: String,
    // Trust & Authenticity
    val authenticityScore: Int = 98, // out of 100
    val authenticityChecklist: List<String> = emptyList(),
    val materials: List<String> = emptyList(),
    val certifications: List<String> = emptyList(),
    // Citations & Sources
    val citations: List<Citation> = emptyList(),
    val sourceConflictNote: String? = null,
    // Price Intelligence
    val priceHistory: List<PricePoint> = emptyList(),
    // Visuals & Tags
    val primaryColorHex: Long = 0xFF9A3412,
    val tags: List<String> = emptyList(),
    val isSeasonal: Boolean = false,
    val festivalTag: String? = null,
    val isPriceDrop: Boolean = false,
    val isProductOfTheDay: Boolean = false,
    val lastVerifiedAt: String = "2026-08-15"
)

enum class ProductCategory(val displayName: String, val iconName: String) {
    TEXTILES_HANDLOOM("Textiles & Handloom", "textile"),
    POTTERY_CERAMICS("Pottery & Ceramics", "pottery"),
    PAINTINGS_FOLK_ART("Folk Art & Paintings", "painting"),
    WOOD_METAL_CRAFT("Wood & Metalcraft", "craft"),
    HERITAGE_SWEETS_FOOD("Heritage Foods & Sweets", "food"),
    JEWELRY_ORNAMENTS("Jewelry & Filigree", "jewelry"),
    NATURAL_FRAGRANCE("Attar & Fragrances", "fragrance")
}

data class Citation(
    val id: String,
    val title: String,
    val publisher: String,
    val sourceType: SourceType,
    val urlOrReference: String,
    val publicationYear: String,
    val lastVerifiedDate: String,
    val reliabilityScore: String = "High (Official/Government)",
    val excerpt: String
)

enum class SourceType {
    GOVERNMENT_GI_REGISTRY,
    CRAFTS_COUNCIL_INDIA,
    TEXTILE_MINISTRY_ARCHIVE,
    ACADEMIC_RESEARCH,
    ARTISAN_COLLECTIVE_RECORD,
    HISTORICAL_CHRONICLE
}

data class PricePoint(
    val monthYear: String,
    val price: Double,
    val note: String? = null
)

data class Artisan(
    val id: String,
    val name: String,
    val title: String,
    val state: String,
    val district: String,
    val village: String,
    val craftName: String,
    val experienceYears: Int,
    val lineageGenerations: Int,
    val biography: String,
    val awards: List<String> = emptyList(),
    val verifiedArtisan: Boolean = true,
    val communityImpact: String,
    val primaryColorHex: Long = 0xFF7C2D12
)

data class RegionInfo(
    val state: String,
    val district: String,
    val villageOrCluster: String,
    val stateCapital: String,
    val geographicOverview: String,
    val famousCrafts: List<String>,
    val culturalHeritageSummary: String,
    val artisanCommunitiesCount: Int,
    val climateAndMaterials: String,
    val coordinatesDisplay: String
)

data class GiftingGuide(
    val id: String,
    val occasionTitle: String,
    val recipientType: String,
    val recommendedRegion: String,
    val description: String,
    val budgetRange: String,
    val culturalEtiquette: String,
    val productIds: List<String>
)
