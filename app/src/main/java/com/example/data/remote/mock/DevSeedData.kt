package com.example.data.remote.mock

import com.example.data.remote.dto.*

/**
 * =========================================================================
 * GAONOVA DEVELOPMENT SEED DATA (MIGRATION & OFFLINE TEST SIMULATOR)
 * Separated cleanly from production application logic and Android UI.
 * This represents what the backend CMS will seed in production databases.
 * =========================================================================
 */
object DevSeedData {

    val categories: List<CategoryDto> = listOf(
        CategoryDto(
            id = "cat_textiles",
            slug = "textiles-handloom",
            name = "Textiles & Handloom",
            description = "GI-certified handspun silks, pashminas, and artisanal weaves from historic loom clusters.",
            iconSymbolName = "textile",
            displayOrder = 1,
            isActive = true,
            isFeatured = true,
            subcategories = listOf(
                CategoryDto(id = "sub_kantha", slug = "kantha-embroidery", name = "Nakshi Kantha", description = "Bengal run-stitch folk embroidery", parentId = "cat_textiles"),
                CategoryDto(id = "sub_pashmina", slug = "pashmina-shawls", name = "Changthangi Pashmina", description = "High-altitude hand-spun cashmere", parentId = "cat_textiles"),
                CategoryDto(id = "sub_blockprint", slug = "bagru-blockprint", name = "Dabu & Bagru Block Print", description = "Chippa mud-resist indigo prints", parentId = "cat_textiles")
            )
        ),
        CategoryDto(
            id = "cat_pottery",
            slug = "pottery-ceramics",
            name = "Pottery & Ceramics",
            description = "Natural clay terracotta, non-clay blue pottery, and wood-fired smoke black earthenware.",
            iconSymbolName = "pottery",
            displayOrder = 2,
            isActive = true,
            isFeatured = true,
            subcategories = listOf(
                CategoryDto(id = "sub_terracotta", slug = "bankura-terracotta", name = "Bishnupur Terracotta", description = "Red alluvial burnt clay crafts", parentId = "cat_pottery"),
                CategoryDto(id = "sub_bluepottery", slug = "jaipur-blue-pottery", name = "Jaipur Quartz Blue Pottery", description = "Turquoise glazed Egyptian paste", parentId = "cat_pottery")
            )
        ),
        CategoryDto(
            id = "cat_paintings",
            slug = "folk-art-paintings",
            name = "Folk Art & Paintings",
            description = "Mineral pigment scrolls, palm leaf etchings, and tribal folklore canvases.",
            iconSymbolName = "painting",
            displayOrder = 3,
            isActive = true,
            isFeatured = true,
            subcategories = listOf(
                CategoryDto(id = "sub_pattachitra", slug = "raghurajpur-pattachitra", name = "Pattachitra Scroll Art", description = "Tussar & palm leaf master paintings", parentId = "cat_paintings")
            )
        ),
        CategoryDto(
            id = "cat_wood_metal",
            slug = "wood-metalcraft",
            name = "Wood & Metalcraft",
            description = "Lost-wax bell metal castings, walnut root carvings, and natural lacquer woodcraft.",
            iconSymbolName = "craft",
            displayOrder = 4,
            isActive = true,
            isFeatured = true,
            subcategories = listOf(
                CategoryDto(id = "sub_dokra", slug = "bastar-dokra", name = "Bastar Bell Metal Dokra", description = "4000-year lost wax casting", parentId = "cat_wood_metal"),
                CategoryDto(id = "sub_channapatna", slug = "channapatna-toys", name = "Channapatna Lacquer Wood", description = "Child-safe vegetable dyed wood", parentId = "cat_wood_metal")
            )
        ),
        CategoryDto(
            id = "cat_fragrances",
            slug = "natural-fragrance",
            name = "Attar & Fragrances",
            description = "Hydro-distilled pure floral and baked earth monsoon perfumes in copper Deg-Bhapkas.",
            iconSymbolName = "fragrance",
            displayOrder = 5,
            isActive = true,
            isFeatured = true,
            subcategories = listOf(
                CategoryDto(id = "sub_mitti_attar", slug = "kannauj-mitti-attar", name = "Baked Earth Mitti Attar", description = "Petrichor captured in aged sandalwood", parentId = "cat_fragrances")
            )
        )
    )

    val locations: List<LocationHierarchyDto> = listOf(
        LocationHierarchyDto(
            stateId = "loc_wb",
            state = "West Bengal",
            districtId = "dist_birbhum",
            district = "Birbhum",
            cityOrTown = "Bolpur",
            villageOrClusterId = "vil_santiniketan",
            villageOrCluster = "Santiniketan & Suri Clusters",
            postalCode = "731235",
            latitude = 23.6800,
            longitude = 87.6800,
            artisanHubName = "Santiniketan Nakshi Guild",
            description = "Red soil country famous for Rabindranath Tagore's ashram and heritage needlecraft."
        ),
        LocationHierarchyDto(
            stateId = "loc_raj",
            state = "Rajasthan",
            districtId = "dist_jaipur",
            district = "Jaipur",
            cityOrTown = "Jaipur",
            villageOrClusterId = "vil_kotjewar",
            villageOrCluster = "Kot Jewar & Sanganer",
            postalCode = "302001",
            latitude = 26.9124,
            longitude = 75.7873,
            artisanHubName = "Jaipur Craft Guild",
            description = "Historic royal capital with centuries of Jaipur quartz blue pottery and Bagru blockprint."
        ),
        LocationHierarchyDto(
            stateId = "loc_jk",
            state = "Jammu & Kashmir",
            districtId = "dist_srinagar",
            district = "Srinagar",
            cityOrTown = "Srinagar",
            villageOrClusterId = "vil_downtown_srinagar",
            villageOrCluster = "Downtown Shahr-e-Khaas Cluster",
            postalCode = "190001",
            latitude = 34.0837,
            longitude = 74.7973,
            artisanHubName = "Kashmir Pashmina Guild",
            description = "High-altitude hand-spun Changthangi cashmere and master needlepoint Sozni."
        ),
        LocationHierarchyDto(
            stateId = "loc_cg",
            state = "Chhattisgarh",
            districtId = "dist_bastar",
            district = "Bastar & Kondagaon",
            cityOrTown = "Kondagaon",
            villageOrClusterId = "vil_bhelwapadar",
            villageOrCluster = "Bhelwapadar Artisan Gram",
            postalCode = "494226",
            latitude = 19.5985,
            longitude = 81.6667,
            artisanHubName = "Bastar Dokra Collective",
            description = "Ancient lost-wax bell metal art practiced by tribal Ghadwa metalsmiths."
        ),
        LocationHierarchyDto(
            stateId = "loc_od",
            state = "Odisha",
            districtId = "dist_puri",
            district = "Puri",
            cityOrTown = "Chandanpur",
            villageOrClusterId = "vil_raghurajpur",
            villageOrCluster = "Raghurajpur Heritage Craft Village",
            postalCode = "752012",
            latitude = 19.8800,
            longitude = 85.8300,
            artisanHubName = "Raghurajpur Chitrakar Guild",
            description = "Every house is an open-air art studio producing sacred Jagannath Pattachitra scrolls."
        ),
        LocationHierarchyDto(
            stateId = "loc_up",
            state = "Uttar Pradesh",
            districtId = "dist_kannauj",
            district = "Kannauj",
            cityOrTown = "Kannauj",
            villageOrClusterId = "vil_badabazar",
            villageOrCluster = "Bada Bazar Heritage Distilleries",
            postalCode = "209725",
            latitude = 27.0500,
            longitude = 79.9200,
            artisanHubName = "Kannauj Deg-Bhapka Guild",
            description = "The Perfume Capital of India hydro-distilling baked monsoon earth and fresh flowers."
        )
    )

    val artisans: List<ArtisanDto> = listOf(
        ArtisanDto(
            id = "art_kantha_1",
            slug = "sujata-mondal-kantha",
            name = "Sujata Mondal",
            displayName = "Master Sujata Mondal",
            title = "Master Kantha Needlecraft Artisan",
            biography = "Sujata leads a collective of 120 rural women artisans in the red soil villages surrounding Santiniketan. Preserving centuries-old run-stitch folk patterns inspired by rural Bengali flora, fauna, and village folklore.",
            craftTradition = "Nakshi Kantha Folk Embroidery",
            experienceYears = 28,
            lineageGenerations = 4,
            location = locations[0],
            cooperativeName = "Amar Kutir Rural Crafts Cooperative",
            verificationStatus = "VERIFIED_GI_MASTER",
            masterStory = "Starting at age 12 learning running stitches from her grandmother, Sujata revived forgotten geometric kantha borders.",
            awards = listOf("National Handicrafts Award 2018", "Shilp Guru Nominee", "Bengal State Craft Merit"),
            communityImpact = "Empowers 120+ rural women with sustainable dignified household income and fair trade craft dividends."
        ),
        ArtisanDto(
            id = "art_blue_pottery_1",
            slug = "mahesh-sharma-blue-pottery",
            name = "Mahesh Sharma",
            displayName = "Mahesh Sharma (Kripawat Guild)",
            title = "Master Ceramicist & Glaze Alchemist",
            biography = "Mahesh carries on the legendary revival of non-clay Egyptian paste blue pottery, using natural quartz, fuller's earth, gum, and copper oxide turquoise pigments fired in traditional wood kilns.",
            craftTradition = "Jaipur Traditional Blue Pottery",
            experienceYears = 34,
            lineageGenerations = 3,
            location = locations[1],
            cooperativeName = "Kot Jewar Potters Collective",
            verificationStatus = "VERIFIED_GI_MASTER",
            masterStory = "Mahesh perfected lead-free natural glazes that withstand high-heat without crazing.",
            awards = listOf("UNESCO Seal of Excellence", "Rajasthan State Craft Award 2015"),
            communityImpact = "Sustains 45 artisan households in Kot Jewar village cluster with clean artisan solar kilns."
        ),
        ArtisanDto(
            id = "art_pashmina_1",
            slug = "ghulam-mohammad-mir",
            name = "Ghulam Mohammad Mir",
            displayName = "Ustad Ghulam Mohammad Mir",
            title = "Master Pashmina Sozni Weaver",
            biography = "Ghulam Mohammad spins and weaves ultra-fine 12-14 micron wool harvested from Changthangi goats in Ladakh. Each heirloom shawl takes over 180 hours of meticulous handloom weaving and needlepoint Sozni embroidery.",
            craftTradition = "Handspun Changthangi Pashmina",
            experienceYears = 42,
            lineageGenerations = 5,
            location = locations[2],
            cooperativeName = "Kashmir Artisans Apex Federation",
            verificationStatus = "VERIFIED_GI_MASTER",
            masterStory = "Mastering the double-interlock kani weave and floral Sozni needlepoint on sheer 14-micron cashmere.",
            awards = listOf("President of India Craft Medal 2012", "J&K Master Craftsman Honour"),
            communityImpact = "Directly pays premium wages to Changpa nomadic herders and local Kashmiri women hand-spinners."
        )
    )

    val sellers: List<SellerDto> = listOf(
        SellerDto(
            id = "sel_amar_kutir",
            slug = "amar-kutir-society",
            businessName = "Amar Kutir Society for Rural Development",
            sellerType = "ARTISAN_COOPERATIVE",
            description = "Non-profit rural cooperative established in 1927 in Santiniketan to sustain traditional cottage crafts.",
            verificationState = "VERIFIED_GI_MASTER",
            address = "P.O. Sriniketan, Birbhum, West Bengal 731236",
            supportEmail = "cooperative@amarkutir.org",
            supportPhone = "+91 3463 252 355",
            rating = 4.9,
            totalArtisansRepresented = 450
        ),
        SellerDto(
            id = "sel_kot_jewar",
            slug = "kot-jewar-collective",
            businessName = "Kot Jewar Village Craft Producer Co.",
            sellerType = "DIRECT_ARTISAN_FAMILY",
            description = "Direct artisan-led cluster producer company registered with Geographical Indications Registry.",
            verificationState = "VERIFIED_GI_MASTER",
            address = "Gram Kot Jewar, Jaipur, Rajasthan 303007",
            supportEmail = "support@kotjewar.org",
            supportPhone = "+91 141 230 9182",
            rating = 4.8,
            totalArtisansRepresented = 85
        )
    )

    val products: List<ProductDto> = listOf(
        ProductDto(
            id = "prod_kantha_bolpur",
            slug = "santiniketan-nakshi-kantha-silk-dupatta",
            name = "Santiniketan Nakshi Kantha Silk Dupatta",
            regionalName = "নকশী কাঁথা রেশম ওড়না (Nakshi Kantha Reshom)",
            shortDescription = "Pure Mulberry Bishnupuri silk drape with 140+ hours of hand-embroidered running stitch motifs.",
            longDescription = "An authentic Geographical Indication certified heirloom textile representing the pastoral folklore of rural Bengal. Hand-stitched across six weeks on shimmering tussar silk using natural dye threads.",
            categoryId = "cat_textiles",
            categorySlug = "textiles-handloom",
            categoryName = "Textiles & Handloom",
            subcategoryId = "sub_kantha",
            subcategoryName = "Nakshi Kantha",
            artisanId = "art_kantha_1",
            artisanName = "Sujata Mondal",
            sellerId = "sel_amar_kutir",
            sellerName = "Amar Kutir Society for Rural Development",
            location = locations[0],
            isHandmade = true,
            isFeatured = true,
            pricing = PriceDto(
                mrp = 4200.0,
                sellingPrice = 3450.0,
                discountPercentage = 18,
                priceSource = "Direct Cooperative Fair Price",
                priceHistory = listOf(
                    PriceHistoryPointDto("2026-03-01", 3800.0, "Spring Weave Batch"),
                    PriceHistoryPointDto("2026-06-01", 3600.0, "Monsoon Season Adjustment"),
                    PriceHistoryPointDto("2026-08-01", 3450.0, "Festive Artisan Direct Price")
                )
            ),
            inventory = InventoryDto(availableQuantity = 14, lowStockThreshold = 3, stockStatus = "IN_STOCK"),
            variants = listOf(
                ProductVariantDto(
                    id = "var_kantha_madder",
                    productId = "prod_kantha_bolpur",
                    sku = "GNV-KAN-001-MDR",
                    title = "Madder Red & Rust Gold",
                    colorName = "Madder Red",
                    colorHex = 0xFF94442A,
                    material = "100% Pure Mulberry Silk",
                    price = PriceDto(mrp = 4200.0, sellingPrice = 3450.0),
                    inventory = InventoryDto(availableQuantity = 8, lowStockThreshold = 2)
                ),
                ProductVariantDto(
                    id = "var_kantha_indigo",
                    productId = "prod_kantha_bolpur",
                    sku = "GNV-KAN-001-IND",
                    title = "Natural Indigo & Cream",
                    colorName = "Indigo Navy",
                    colorHex = 0xFF1E3A8A,
                    material = "100% Pure Mulberry Silk",
                    price = PriceDto(mrp = 4200.0, sellingPrice = 3450.0),
                    inventory = InventoryDto(availableQuantity = 6, lowStockThreshold = 2)
                )
            ),
            media = listOf(
                ProductMediaDto(
                    id = "med_kantha_1",
                    role = "PRIMARY",
                    altText = "Santiniketan Nakshi Kantha Silk Dupatta draped over handcrafted loom frame",
                    urls = MediaResolutionsDto(
                        original = "https://cdn.gaonova.com/products/kantha/hero_orig.webp",
                        large = "https://cdn.gaonova.com/products/kantha/hero_lg.webp",
                        medium = "https://cdn.gaonova.com/products/kantha/hero_md.webp",
                        thumbnail = "https://cdn.gaonova.com/products/kantha/hero_thumb.webp"
                    )
                ),
                ProductMediaDto(
                    id = "med_kantha_2",
                    role = "DETAIL",
                    altText = "Close-up macro of traditional run stitch needlework",
                    urls = MediaResolutionsDto(
                        original = "https://cdn.gaonova.com/products/kantha/detail_orig.webp",
                        large = "https://cdn.gaonova.com/products/kantha/detail_lg.webp",
                        medium = "https://cdn.gaonova.com/products/kantha/detail_md.webp",
                        thumbnail = "https://cdn.gaonova.com/products/kantha/detail_thumb.webp"
                    )
                )
            ),
            provenance = ProductProvenanceDto(
                productId = "prod_kantha_bolpur",
                artisanId = "art_kantha_1",
                craftTradition = "Nakshi Kantha Folk Embroidery",
                locationHierarchy = locations[0],
                giRegistrationNo = "GI-REG-IN-0078",
                giCertificateUrl = "https://cdn.gaonova.com/certificates/GI-0078.pdf",
                gazetteNotificationDate = "2008-03-28",
                authenticityScore = 99,
                authenticityChecklist = listOf(
                    "GI Registered Origin Tag #0078",
                    "Silk Mark Certified 100% Mulberry Cocoon",
                    "Hand-embroidered running stitch inspection",
                    "Direct Artisan Signature On Border"
                ),
                rawMaterialOrigin = "Bishnupur Silk & Nadia Natural Thread",
                verificationTimestamp = "2026-08-15T10:00:00Z"
            ),
            knowledge = ProductKnowledgeDto(
                historyAndOrigins = "Kantha embroidery traces back to the Vedic era and Buddhist stupas, where discarded sarees were layered and quilted together with running stitches to tell village tales.",
                culturalSignificance = "Symbol of rebirth, maternal protection, and artistic recycling in rural Bengal families.",
                manufacturingProcess = "1. Tracing motifs on handwoven silk\n2. Selecting vegetable-dyed anchor threads\n3. Hand-stitching 140+ hours per piece\n4. Natural herbal wash and finishing.",
                rawMaterials = listOf("Murshidabad Mulberry Silk", "Natural Indigo Dye", "Madder Root Red Thread"),
                careInstructions = "Dry clean recommended for first 2 washes. Iron on reverse low heat.",
                storageGuidelines = "Store folded in breathable cotton fabric with dried neem leaves. Avoid direct damp sunlight.",
                packagingDetails = "Packed in recycled handmade jute envelope with GI stamp.",
                giftingEtiquette = "Exquisite heirloom gift for milestone wedding anniversaries and cultural celebrations.",
                authenticityGuidelines = "Look for uneven stitch rhythms on reverse proving 100% human hand-crafting over automated machines.",
                faqs = listOf(
                    KnowledgeFaqDto("Is this authentic hand-stitched Kantha?", "Yes, certified by Amar Kutir Rural Cooperative under GI-0078."),
                    KnowledgeFaqDto("How long does one piece take to make?", "Approximately 120-140 artisan work hours.")
                ),
                lastVerifiedTimestamp = "2026-08-15"
            ),
            attributes = listOf(
                ProductAttributeDto("material", "Primary Material", "Pure Silk", "MATERIAL"),
                ProductAttributeDto("technique", "Embroidery Technique", "Nakshi Running Stitch", "TECHNIQUE"),
                ProductAttributeDto("craft_type", "Craft Category", "Handloom Embroidery", "CRAFT_GENRE"),
                ProductAttributeDto("origin_geo", "Origin Village", "Bolpur, Santiniketan", "ORIGIN_GEO")
            ),
            sources = listOf(
                ProductSourceDto(
                    source = SourceDto(
                        id = "src_gi_registry_0078",
                        title = "Geographical Indications Journal No. 25: Nakshi Kantha",
                        publisher = "Geographical Indications Registry, Government of India",
                        url = "https://search.ipindia.gov.in/GIRPublic/",
                        sourceType = "GOVERNMENT_GI_REGISTRY",
                        publicationDate = "2008-03-28",
                        retrievedDate = "2026-08-15",
                        lastVerifiedDate = "2026-08-15",
                        excerpt = "Nakshi Kantha registered under class 24 & 25 originating exclusively from Birbhum, Murshidabad and surrounding districts."
                    ),
                    claimType = "GI Provenance & Origin",
                    verificationStatus = "VERIFIED"
                )
            ),
            ratingsSummary = RatingsSummaryDto(averageRating = 4.9, totalReviews = 42, aiCraftSummary = "Praised by collectors for exquisite silk sheen and intricate border needlework."),
            tags = listOf("kantha", "silk", "bengal", "gi_certified", "dupatta", "handloom"),
            createdAt = "2026-01-10T00:00:00Z",
            updatedAt = "2026-08-15T00:00:00Z",
            publishedAt = "2026-01-15T00:00:00Z"
        ),
        ProductDto(
            id = "prod_blue_pottery_jaipur",
            slug = "jaipur-traditional-blue-pottery-vase",
            name = "Jaipur Traditional Blue Pottery Floral Vase",
            regionalName = "जयपुर नीली मिट्टी का फूलदान (Jaipur Neeli Mitti)",
            shortDescription = "Non-clay quartz & fuller's earth turquoise glazed vase with Persian floral motifs.",
            longDescription = "Crafted from ground quartz stone, glass, and natural multani mitti without clay. Hand-painted with cobalt oxide and copper turquoise pigments by master potters of Kot Jewar.",
            categoryId = "cat_pottery",
            categorySlug = "pottery-ceramics",
            categoryName = "Pottery & Ceramics",
            subcategoryId = "sub_bluepottery",
            subcategoryName = "Jaipur Quartz Blue Pottery",
            artisanId = "art_blue_pottery_1",
            artisanName = "Mahesh Sharma",
            sellerId = "sel_kot_jewar",
            sellerName = "Kot Jewar Village Craft Producer Co.",
            location = locations[1],
            isHandmade = true,
            isFeatured = true,
            pricing = PriceDto(
                mrp = 2200.0,
                sellingPrice = 1890.0,
                discountPercentage = 14,
                priceSource = "Direct Artisan Cooperative Price",
                priceHistory = listOf(
                    PriceHistoryPointDto("2026-04-01", 1990.0, "Spring Batch"),
                    PriceHistoryPointDto("2026-08-01", 1890.0, "Cluster Direct Price")
                )
            ),
            inventory = InventoryDto(availableQuantity = 9, lowStockThreshold = 2, stockStatus = "IN_STOCK"),
            variants = emptyList(),
            media = listOf(
                ProductMediaDto(
                    id = "med_blue_1",
                    role = "PRIMARY",
                    altText = "Jaipur Blue Pottery floral urn on marble pedestal",
                    urls = MediaResolutionsDto(
                        original = "https://cdn.gaonova.com/products/blue_pottery/hero_orig.webp",
                        large = "https://cdn.gaonova.com/products/blue_pottery/hero_lg.webp",
                        medium = "https://cdn.gaonova.com/products/blue_pottery/hero_md.webp",
                        thumbnail = "https://cdn.gaonova.com/products/blue_pottery/hero_thumb.webp"
                    )
                )
            ),
            provenance = ProductProvenanceDto(
                productId = "prod_blue_pottery_jaipur",
                artisanId = "art_blue_pottery_1",
                craftTradition = "Jaipur Traditional Blue Pottery",
                locationHierarchy = locations[1],
                giRegistrationNo = "GI-REG-IN-0002",
                giCertificateUrl = "https://cdn.gaonova.com/certificates/GI-0002.pdf",
                gazetteNotificationDate = "2005-06-15",
                authenticityScore = 98,
                authenticityChecklist = listOf(
                    "GI Tag #0002 Certified Origin",
                    "Lead-free quartz glaze certified",
                    "Hand-painted brush stroke inspection"
                ),
                rawMaterialOrigin = "Jaipur Quartz & Multani Mitti",
                verificationTimestamp = "2026-08-10T10:00:00Z"
            ),
            knowledge = ProductKnowledgeDto(
                historyAndOrigins = "Introduced to Jaipur during the reign of Sawai Ram Singh II via Persian masters in the 19th century.",
                culturalSignificance = "Unique ceramic craft globally made entirely without clay, symbol of Jaipur royal architecture.",
                manufacturingProcess = "1. Grinding quartz powder with gum\n2. Hand molding in open plaster casts\n3. Painting with copper and cobalt pigments\n4. Single firing in wood kiln.",
                rawMaterials = listOf("Quartz Powder", "Fuller's Earth", "Copper Oxide", "Cobalt Oxide"),
                careInstructions = "Wipe with damp cloth. Not dishwasher safe. Use as dry floral display.",
                storageGuidelines = "Place on steady surface. Protect from sudden sharp impacts.",
                packagingDetails = "Multi-layer eco honeycomb paper wrap in reinforced corrugated craft box.",
                giftingEtiquette = "Ideal housewarming centerpiece and heritage desk collectible.",
                authenticityGuidelines = "Slight glaze crackles (crazing) are signature proof of natural Egyptian paste firing.",
                faqs = listOf(
                    KnowledgeFaqDto("Can I put water in this vase?", "Use an inner glass liner if holding fresh water stems.")
                ),
                lastVerifiedTimestamp = "2026-08-10"
            ),
            attributes = listOf(
                ProductAttributeDto("material", "Body Material", "Quartz Egyptian Paste", "MATERIAL"),
                ProductAttributeDto("technique", "Firing Technique", "Low-temperature Glazed Kiln", "TECHNIQUE")
            ),
            sources = listOf(
                ProductSourceDto(
                    source = SourceDto(
                        id = "src_gi_registry_0002",
                        title = "GI Journal: Jaipur Blue Pottery",
                        publisher = "Government of India GI Registry",
                        url = "https://search.ipindia.gov.in/GIRPublic/",
                        sourceType = "GOVERNMENT_GI_REGISTRY",
                        publicationDate = "2005-06-15",
                        retrievedDate = "2026-08-10",
                        lastVerifiedDate = "2026-08-10",
                        excerpt = "Registered GI entry for Jaipur Blue Pottery craft cluster."
                    ),
                    claimType = "Authenticity & Material Formulation",
                    verificationStatus = "VERIFIED"
                )
            ),
            ratingsSummary = RatingsSummaryDto(averageRating = 4.8, totalReviews = 29, aiCraftSummary = "Celebrated for luminous cobalt hues and authentic weight."),
            tags = listOf("pottery", "jaipur", "rajasthan", "blue_pottery", "quartz", "decor"),
            createdAt = "2026-01-20T00:00:00Z",
            updatedAt = "2026-08-10T00:00:00Z",
            publishedAt = "2026-01-25T00:00:00Z"
        )
    )

    val collections: List<CollectionDto> = listOf(
        CollectionDto(
            id = "col_gi_living_heritage",
            slug = "gi-living-heritage",
            title = "Sacred Earth & Handloom Weaves",
            subtitle = "Direct from 100% Certified Village Guilds",
            description = "Explore museum-grade artisanal creations verified under India's Geographical Indications registry.",
            coverImageUrl = "https://cdn.gaonova.com/collections/heritage_cover.webp",
            themeColorHex = 0xFF7C2D12,
            productIds = listOf("prod_kantha_bolpur", "prod_blue_pottery_jaipur"),
            isFeatured = true,
            publishedAt = "2026-08-01T00:00:00Z"
        )
    )

    val stories: List<VillageStoryDto> = listOf(
        VillageStoryDto(
            id = "story_bolpur_red_soil",
            slug = "echoes-of-the-red-soil-bolpur",
            title = "Echoes of the Red Soil: The Women of Bolpur",
            craftTradition = "Nakshi Kantha Folk Needlework",
            location = locations[0],
            artisanId = "art_kantha_1",
            summary = "How 120 women in Birbhum turned generational quilting into economic independence.",
            fullStory = "Under the shade of sprawling banyan trees in Bolpur, songs of the monsoon mingle with the rhythmic movement of needle and thread...",
            heroImageUrl = "https://cdn.gaonova.com/stories/bolpur_hero.webp",
            galleryImages = listOf("https://cdn.gaonova.com/stories/bolpur_1.webp", "https://cdn.gaonova.com/stories/bolpur_2.webp"),
            readTimeMinutes = 4,
            publishedAt = "2026-08-12T00:00:00Z"
        )
    )
}
