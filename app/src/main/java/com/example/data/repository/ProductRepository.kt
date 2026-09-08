package com.example.data.repository

import com.example.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

object ProductRepository {

    val sampleArtisans = listOf(
        Artisan(
            id = "art_patachitra_lantern_1",
            name = "Gurupada Chitrakar",
            title = "Master Patachitra & Metal Enamel Artist",
            state = "West Bengal",
            district = "Paschim Medinipur",
            village = "Naya Patachitra Gram, Pingla",
            craftName = "Bengal Patachitra Folk Metal Art",
            experienceYears = 32,
            lineageGenerations = 4,
            biography = "Gurupada leads a family of Patuas in Naya village who sing ancient scrolls while hand-painting folk lanterns and metal artifacts with natural vegetable dyes and durable enamel finishes.",
            awards = listOf("Bengal State Handicrafts Award", "All India Craft Heritage Fellow"),
            verifiedArtisan = true,
            communityImpact = "Supports 30 young apprentice Patuas in Pingla with scroll singing and lantern painting workshops.",
            primaryColorHex = 0xFFC2185B
        ),
        Artisan(
            id = "art_painted_bottles_1",
            name = "Swarna Chitrakar & Pingla Collective",
            title = "Patachitra Natural Pigment Alchemist",
            state = "West Bengal",
            district = "Paschim Medinipur",
            village = "Naya Village, Pingla Cluster",
            craftName = "Patachitra & Folk Art on Steel",
            experienceYears = 26,
            lineageGenerations = 3,
            biography = "Swarna has revolutionized eco-conscious rural handicrafts by adapting ancestral Patachitra iconography (Tree of Life, Sacred Fish, Santhal dancers) onto reusable food-grade stainless steelware.",
            awards = listOf("National Women Artisan Award 2019", "UNESCO Craft Exchange Artist"),
            verifiedArtisan = true,
            communityImpact = "Provides daily livelihood to 40+ women artisans transforming sustainable utility items into wearable art.",
            primaryColorHex = 0xFF2E7D32
        ),
        Artisan(
            id = "art_natungram_owl_1",
            name = "Bhaskar Sutradhar",
            title = "Master Wood Sculptor & GI Custodian",
            state = "West Bengal",
            district = "Purba Bardhaman",
            village = "Natungram Woodcraft Village",
            craftName = "Natungram Wooden Dolls & Lokkhi Pecha",
            experienceYears = 38,
            lineageGenerations = 5,
            biography = "Bhaskar hand-chisels sacred Lokkhi Pecha (Goddess Lakshmi's owl) out of seasoned Gamhar wood. Each piece carries the distinct concentric eyes and tempera folk color geometry preserved over 200 years.",
            awards = listOf("President's National Award for Master Craftsmen", "Bengal Heritage GI Custodian"),
            verifiedArtisan = true,
            communityImpact = "Leads the Natungram Woodcarvers Cooperative sustaining 65 artisan families in Bardhaman.",
            primaryColorHex = 0xFFE65100
        ),
        Artisan(
            id = "art_dokra_durga_1",
            name = "Haradhan Karmakar",
            title = "National Awardee Dokra Metalsmith",
            state = "West Bengal",
            district = "Bankura",
            village = "Bikna Dokra Gram",
            craftName = "Bikna Dokra Lost-Wax Metal Casting",
            experienceYears = 45,
            lineageGenerations = 6,
            biography = "Haradhan is one of India's most celebrated non-ferrous metal sculptors, crafting intricate 10-armed Durga idols using beeswax coils, river clay molds, and pit-furnace molten brass casting.",
            awards = listOf("National Award for Master Craftsmen 2011", "Shilp Guru Nominee", "Bengal Ratna"),
            verifiedArtisan = true,
            communityImpact = "Mentors 50+ tribal Dhokra smiths in Bikna and secures fair export compensation.",
            primaryColorHex = 0xFFC5A059
        ),
        Artisan(
            id = "art_patachitra_plates_1",
            name = "Monimala Chitrakar",
            title = "Folk Scroll Painter & Terracotta Decorator",
            state = "West Bengal",
            district = "Paschim Medinipur",
            village = "Naya Village & Kalighat",
            craftName = "Bengal Patachitra Wall Plates & Clay Shorai",
            experienceYears = 29,
            lineageGenerations = 4,
            biography = "Monimala paints vibrant circular Shorai plates depicting Durga Mukha, Radha-Krishna, and Kalighat folk legends using organic mineral colors extracted from crushed stones, turmeric, and lamp soot.",
            awards = listOf("State Academy Award 2016", "Folk Art Biennial Award"),
            verifiedArtisan = true,
            communityImpact = "Reinvests 20% of sales into village clay preparation and organic dye plant cultivation.",
            primaryColorHex = 0xFFB71C1C
        ),
        Artisan(
            id = "art_kantha_1",
            name = "Sujata Mondal",
            title = "Master Kantha Needlecraft Artisan",
            state = "West Bengal",
            district = "Birbhum",
            village = "Bolpur (Santiniketan)",
            craftName = "Nakshi Kantha Folk Embroidery",
            experienceYears = 28,
            lineageGenerations = 4,
            biography = "Sujata leads a collective of 120 rural women artisans in the red soil villages surrounding Santiniketan. Preserving centuries-old run-stitch folk patterns inspired by rural Bengali flora, fauna, and village folklore.",
            awards = listOf("National Handicrafts Award 2018", "Shilp Guru Nominee", "Bengal State Craft Merit"),
            verifiedArtisan = true,
            communityImpact = "Empowers 120+ rural women with sustainable dignified household income and fair trade craft dividends.",
            primaryColorHex = 0xFF94442A
        ),
        Artisan(
            id = "art_blue_pottery_1",
            name = "Kripal Singh Kripawat Guild (Mahesh Sharma)",
            title = "Master Ceramicist & Glaze Alchemist",
            state = "Rajasthan",
            district = "Jaipur",
            village = "Kot Jewar & Sanganer",
            craftName = "Jaipur Traditional Blue Pottery",
            experienceYears = 34,
            lineageGenerations = 3,
            biography = "Mahesh carries on the legendary revival of non-clay Egyptian paste blue pottery, using natural quartz, fuller's earth, gum, and copper oxide turquoise pigments fired in traditional wood kilns.",
            awards = listOf("UNESCO Seal of Excellence", "Rajasthan State Craft Award 2015"),
            verifiedArtisan = true,
            communityImpact = "Sustains 45 artisan households in Kot Jewar village cluster with clean artisan solar kilns.",
            primaryColorHex = 0xFF3A6167
        ),
        Artisan(
            id = "art_pashmina_1",
            name = "Ghulam Mohammad Mir",
            title = "Master Pashmina Sozni Weaver",
            state = "Jammu & Kashmir",
            district = "Srinagar",
            village = "Downtown Craft Cluster, Srinagar",
            craftName = "Handspun Changthangi Pashmina",
            experienceYears = 42,
            lineageGenerations = 5,
            biography = "Ghulam Mohammad spins and weaves ultra-fine 12-14 micron wool harvested from Changthangi goats in Ladakh. Each heirloom shawl takes over 180 hours of meticulous handloom weaving and needlepoint Sozni embroidery.",
            awards = listOf("President of India Craft Medal 2012", "J&K Master Craftsman Honour"),
            verifiedArtisan = true,
            communityImpact = "Directly pays premium wages to Changpa nomadic herders and local Kashmiri women hand-spinners.",
            primaryColorHex = 0xFF6E2E1A
        ),
        Artisan(
            id = "art_channapatna_1",
            name = "B. V. Venkatesh",
            title = "Master Wood Turner & Natural Lacquerer",
            state = "Karnataka",
            district = "Ramanagara",
            village = "Channapatna Craft Town",
            craftName = "Channapatna Non-Toxic Wooden Toys",
            experienceYears = 25,
            lineageGenerations = 3,
            biography = "Crafting ivory-wood (Wrightia tinctoria) toys on traditional hand lathes seasoned with natural non-toxic vegetable dyes extracted from turmeric, indigo, kumkum, and acacia bark.",
            awards = listOf("Karnataka Rajyotsava Craft Award", "Toy Fair Gold Medal"),
            verifiedArtisan = true,
            communityImpact = "Preserves child-safe, 100% biodegradable zero-plastic wooden heritage toy making.",
            primaryColorHex = 0xFFA67B4C
        ),
        Artisan(
            id = "art_pattachitra_1",
            name = "Pranab Kishore Das",
            title = "Master Pattachitra Palm Leaf Painter",
            state = "Odisha",
            district = "Puri",
            village = "Raghurajpur Heritage Crafts Village",
            craftName = "Raghurajpur Pattachitra Scroll Art",
            experienceYears = 31,
            lineageGenerations = 6,
            biography = "Resident of the historic heritage village of Raghurajpur, painting epic narratives with natural mineral colors (hingula, haritala, conch shell white) on treated tussar silk and palm leaves.",
            awards = listOf("National Lalit Kala Akademi Fellow", "Odisha State Award"),
            verifiedArtisan = true,
            communityImpact = "Every house in Raghurajpur is an open studio; funds village Sanskrit and craft gurukuls.",
            primaryColorHex = 0xFF3B6E6A
        ),
        Artisan(
            id = "art_attar_1",
            name = "Haji Munna Lal & Sons",
            title = "Traditional Deg-Bhapka Master Distiller",
            state = "Uttar Pradesh",
            district = "Kannauj",
            village = "Bada Bazar Heritage Distilleries, Kannauj",
            craftName = "Mitti & Gulab Deg-Bhapka Attar",
            experienceYears = 50,
            lineageGenerations = 5,
            biography = "Hydro-distilling fresh Damask roses and baked monsoon earth (Mitti) using ancient copper pots (Degs), bamboo pipes (Chonga), and aged sandalwood oil receivers over wood fire kilns.",
            awards = listOf("Kannauj Perfumery Heritage Guild Recognition", "GI Custodian"),
            verifiedArtisan = true,
            communityImpact = "Contracts 300+ local smallholder rose and jasmine farmers with guaranteed buyback prices.",
            primaryColorHex = 0xFF8C6839
        )
    )

    val sampleProducts = listOf(
        Product(
            id = "prod_patachitra_lantern",
            name = "Bengali Patachitra Hand-Painted Folk Lalten (Lantern)",
            regionalName = "পটচিত্র আঁকা রঙিন লণ্ঠন (Patachitra Lalten)",
            category = ProductCategory.WOOD_METAL_CRAFT,
            state = "West Bengal",
            district = "Paschim Medinipur",
            villageOrCluster = "Naya Patachitra Gram, Pingla",
            price = 1850.0,
            originalPrice = 2400.0,
            rating = 4.9,
            reviewCount = 88,
            giCertified = true,
            giRegistrationNo = "GI Application No. 562 (Bengal Patachitra)",
            artisanId = "art_patachitra_lantern_1",
            artisanName = "Gurupada Chitrakar & Guild",
            stockCount = 14,
            shortDescription = "Hand-formed metal hurricane lantern with vivid magenta pink enamel body, carrying handle, ventilated chimney, and glass painted with traditional Bengal Patachitra folk motifs.",
            whatDescription = "An iconic vintage metal hurricane lantern ('Lalten') reimagined with vibrant magenta-pink body and clear glass panels intricately hand-painted by master Patuas. Depicts Radha-Krishna in forest groves and Santhal tribal dance narratives using natural mineral pigments sealed with high-durability heat-resistant transparent glaze.",
            whereContext = "Naya village in Pingla block, Paschim Medinipur, West Bengal. A unique heritage village where over 300 Patua painters reside, renowned worldwide for scroll painting and melodic storytelling (Pater Gaan).",
            whySignificance = "Breathes modern everyday utility into centuries-old scroll painting traditions. Creates an ambient warm glow, illuminating traditional Bengali folk motifs during festive evenings and indoor tea spaces.",
            whoArtisanStory = "Crafted by master Patua Gurupada Chitrakar and his apprentice family members. Each lantern requires 12 hours of meticulous freehand brushwork without stencils or tracing.",
            howCareAndBuy = "Wipe metal surfaces with soft dry microfiber cloth. Avoid harsh abrasive cleaners on hand-painted glass. Suitable for tea light candles, small oil wicks, or LED fairy strings.",
            authenticityScore = 99,
            authenticityChecklist = listOf(
                "Authentic Pingla Patachitra Hand-Painting Certification",
                "100% Freehand Mineral & Organic Pigment Artwork",
                "Rust-resistant Powder-Coated Metal Casing with Working Chimney",
                "Direct Source Tracking to Naya Village Craft Guild"
            ),
            materials = listOf("Galvanized Metal Sheet & Wire", "Heat-Resistant Tempered Glass", "Natural Mineral & Enamel Dyes"),
            certifications = listOf("GI Tag: Bengal Patachitra #562", "West Bengal Rural Craft Hub Verified", "Direct Artisan Seal"),
            citations = listOf(
                Citation(
                    id = "cit_lantern_1",
                    title = "Geographical Indications Journal - Bengal Patachitra",
                    publisher = "Intellectual Property Office, Govt. of India",
                    sourceType = SourceType.GOVERNMENT_GI_REGISTRY,
                    urlOrReference = "https://ipindia.gov.in/gi-bengal-patachitra",
                    publicationYear = "2018",
                    lastVerifiedDate = "2026-08-20",
                    reliabilityScore = "High (Government Registry)",
                    excerpt = "Grants GI protection to the ancient scroll and folk painting tradition of Pingla cluster, Paschim Medinipur."
                )
            ),
            priceHistory = listOf(
                PricePoint("Jan 2026", 2400.0, "Artisan Release"),
                PricePoint("May 2026", 2100.0, "Craft Fair Batch"),
                PricePoint("Aug 2026", 1850.0, "Direct Cooperative Price")
            ),
            primaryColorHex = 0xFFC2185B,
            tags = listOf("Lantern", "Lalten", "Patachitra", "Bengal", "Home Decor", "GI Tag", "Folk Art"),
            isSeasonal = true,
            festivalTag = "Festive Glow & Puja Special",
            isPriceDrop = true,
            isProductOfTheDay = true
        ),
        Product(
            id = "prod_painted_steel_bottles",
            name = "Hand-Painted Patachitra Eco Stainless Steel Bottles (Set of 4)",
            regionalName = "হাতে আঁকা নকশী স্টিল বোতল (Nokshi Steel Bottles)",
            category = ProductCategory.PAINTINGS_FOLK_ART,
            state = "West Bengal",
            district = "Paschim Medinipur",
            villageOrCluster = "Naya Village, Pingla Cluster",
            price = 1299.0,
            originalPrice = 1750.0,
            rating = 4.9,
            reviewCount = 135,
            giCertified = true,
            giRegistrationNo = "GI Application No. 562 (Bengal Patachitra)",
            artisanId = "art_painted_bottles_1",
            artisanName = "Swarna Chitrakar & Pingla Collective",
            stockCount = 25,
            shortDescription = "Set of 4 food-grade insulated stainless steel bottles hand-painted with iconic Bengal folk motifs: Tree of Life, Mayur, Sacred Fish, and Village Musician.",
            whatDescription = "A set of four premium 304 food-grade stainless steel eco bottles, individually hand-painted with iconic Bengali rural motifs — including the mythical Padma fish, dancing peacock, tree of life, and Santhal flute player. Sealed with a clear, food-safe scratch-resistant polyurethane lacquer.",
            whereContext = "Pingla rural craft hub in Paschim Medinipur, West Bengal. Born out of an artisan initiative to replace single-use plastics with reusable, culturally rich artistic lifestyle products.",
            whySignificance = "Combines modern zero-waste hydration with ancient narrative folk art. Every sip honors centuries of Bengali indigenous folklore while supporting rural women artists.",
            whoArtisanStory = "Hand-painted by Swarna Chitrakar and 15 rural women painters of Naya village. Includes custom hand-painted floral wooden base plate.",
            howCareAndBuy = "Gentle hand wash with soft sponge and mild soap. Do not scrub with metallic scouring pads or place in commercial high-heat dishwashers.",
            authenticityScore = 98,
            authenticityChecklist = listOf(
                "304 Premium Food-Grade Rust-Proof Stainless Steel",
                "Non-Toxic Food-Safe Protective Sealant",
                "100% Hand-Painted Original Patachitra Folk Icons",
                "Pingla Women's Cooperative Fair-Trade Guarantee"
            ),
            materials = listOf("Grade 304 Stainless Steel", "Eco-friendly Acrylic & Mineral Pigments", "Polyurethane Protective Topcoat"),
            certifications = listOf("GI Tag: Bengal Patachitra #562", "BPA-Free Certified", "Zero-Plastic Living"),
            citations = listOf(
                Citation(
                    id = "cit_bottles_1",
                    title = "Sustainable Craft Innovation in Pingla Artisan Hubs",
                    publisher = "Crafts Council of India & Banglanatak",
                    sourceType = SourceType.CRAFTS_COUNCIL_INDIA,
                    urlOrReference = "https://craftscouncilindia.org/pingla-sustainable-crafts",
                    publicationYear = "2022",
                    lastVerifiedDate = "2026-08-18",
                    reliabilityScore = "High (Crafts Council)",
                    excerpt = "Documentation of Pingla Patua community applying GI-recognized natural iconography to eco-friendly stainless steel lifestyle accessories."
                )
            ),
            priceHistory = listOf(
                PricePoint("Feb 2026", 1750.0, "Set Introduction"),
                PricePoint("Jun 2026", 1499.0, "Eco-Living Campaign"),
                PricePoint("Aug 2026", 1299.0, "Artisan Direct Deal")
            ),
            primaryColorHex = 0xFF2E7D32,
            tags = listOf("Bottles", "Eco-friendly", "Stainless Steel", "Patachitra", "Kitchen", "Gift Set"),
            isSeasonal = false,
            isPriceDrop = true
        ),
        Product(
            id = "prod_natungram_wooden_owl",
            name = "Natungram GI-Certified Carved Wooden Owl (Lokkhi Pecha)",
            regionalName = "নতুনগ্রামের দারুশিল্প লক্ষ্মী প্যাঁচা (Lokkhi Pecha)",
            category = ProductCategory.WOOD_METAL_CRAFT,
            state = "West Bengal",
            district = "Purba Bardhaman",
            villageOrCluster = "Natungram Woodcraft Village",
            price = 1450.0,
            originalPrice = 1900.0,
            rating = 5.0,
            reviewCount = 162,
            giCertified = true,
            giRegistrationNo = "GI Application No. 560 (Bengal Wooden Dolls & Toys)",
            artisanId = "art_natungram_owl_1",
            artisanName = "Bhaskar Sutradhar, Master Carver",
            stockCount = 19,
            shortDescription = "Authentic hand-chiselled Gamhar wood owl idol with signature concentric round eyes, hooked beak, and yellow-red-black tempera folk painting.",
            whatDescription = "An iconic sacred wooden sculpture handcrafted from seasoned Gamhar (white teak) or mango wood. Features prominent large circular eyes painted in concentric white, black, and red rings, a sharp yellow curved beak, and rhythmic feather chisel carvings along the body, crowned with traditional temple alpana patterns.",
            whereContext = "Natungram village near Agradwip in Purba Bardhaman district, West Bengal. A dedicated artisan hamlet where generations of Sutradhar (carpenter) families have carved sacred idols and folk toys for over two centuries.",
            whySignificance = "The Lokkhi Pecha (Barn Owl) is the revered sacred mount (Vahana) of Goddess Lakshmi, symbolizing prosperity, wisdom, and vigilance against negativity. It is an indispensable auspicious element in traditional Bengali households.",
            whoArtisanStory = "Hand-carved using hand chisels ('Batal') and mallets by master sculptor Bhaskar Sutradhar, seasoned for 3 months, and hand-painted with traditional mineral-gum tempera colors.",
            howCareAndBuy = "Dust regularly with a dry soft bristle brush. Keep away from excessive dampness or direct moisture. Perfect for living room consoles, home alters, or study desks.",
            authenticityScore = 100,
            authenticityChecklist = listOf(
                "Official GI Tag #560 Bengal Wooden Dolls Authenticity Stamp",
                "Solid Single-Block Seasoned Gamhar Timber",
                "Traditional Chisel Markings on Reverse",
                "Non-Toxic Traditional Mineral Tempera Paint"
            ),
            materials = listOf("Seasoned Gamhar Wood (Gmelina arborea)", "Natural Mineral Tempera", "Organic Tree Resin Varnish"),
            certifications = listOf("GI Tag: Bengal Wooden Dolls & Toys #560", "National Handicrafts Board Certified"),
            citations = listOf(
                Citation(
                    id = "cit_owl_1",
                    title = "Geographical Indications Registry - Wooden Dolls of Natungram",
                    publisher = "Ministry of Commerce and Industry, Government of India",
                    sourceType = SourceType.GOVERNMENT_GI_REGISTRY,
                    urlOrReference = "https://ipindia.gov.in/gi-natungram-wooden-dolls",
                    publicationYear = "2018",
                    lastVerifiedDate = "2026-08-14",
                    reliabilityScore = "High (Government Registry)",
                    excerpt = "Defines the specific Gamhar wood chiselling techniques and iconographic proportions of Natungram Lokkhi Pecha."
                )
            ),
            priceHistory = listOf(
                PricePoint("Jan 2026", 1900.0, "Artisan Exhibition"),
                PricePoint("May 2026", 1650.0, "Monsoon Batch"),
                PricePoint("Aug 2026", 1450.0, "Artisan Direct Price")
            ),
            primaryColorHex = 0xFFE65100,
            tags = listOf("Wooden", "Owl", "Lokkhi Pecha", "Natungram", "GI Tag", "Sculpture", "Auspicious"),
            isSeasonal = true,
            festivalTag = "Kojagari Lakshmi Puja Special",
            isPriceDrop = true
        ),
        Product(
            id = "prod_dokra_durga_idol",
            name = "Bikna Dokra Brass Lost-Wax Durga Parivar Idol",
            regionalName = "বিকনা ডোকরা মা দুর্গা প্রতিমা (Bikna Dokra Durga)",
            category = ProductCategory.WOOD_METAL_CRAFT,
            state = "West Bengal",
            district = "Bankura",
            villageOrCluster = "Bikna Dokra Gram",
            price = 6800.0,
            originalPrice = 8500.0,
            rating = 5.0,
            reviewCount = 74,
            giCertified = true,
            giRegistrationNo = "GI Application No. 565 (Bengal Dokra)",
            artisanId = "art_dokra_durga_1",
            artisanName = "Haradhan Karmakar, National Awardee",
            stockCount = 7,
            shortDescription = "4,000-year-old lost-wax hollow brass casting depicting ten-armed Goddess Durga slaying Mahishasura with Lakshmi, Saraswati, Ganesha, and Kartikeya under ornate filigree arch.",
            whatDescription = "A masterwork of ancient metallurgical art created using non-ferrous lost-wax hollow casting (Cire Perdue). Depicts Mahishasuramardini with 10 arms holding divine celestial weapons, slaying the buffalo demon Mahishasura, mounted on her fierce lion vahana, framed under a majestic openwork brass arch (Chala) with her four children.",
            whereContext = "Bikna village on the outskirts of Bankura town, West Bengal. Home to the Karmakar tribal metalsmiths whose casting lineage stretches back to the Indus Valley Dancing Girl (2500 BCE).",
            whySignificance = "Every single piece is completely unique and impossible to duplicate — the clay mold is shattered during cooling to release the golden metal sculpture. Embodies divine feminine triumph and primal tribal metallurgical heritage.",
            whoArtisanStory = "Meticulously coiled with pure natural beeswax strings, clay-molded, and fired in traditional charcoal-wood pit kilns by National Awardee Haradhan Karmakar over 3 weeks.",
            howCareAndBuy = "Clean with a soft dry lint-free cloth or gentle brass polish (Pitambari) for high shine, or leave natural for authentic antique museum patina. Avoid acidic liquids.",
            authenticityScore = 100,
            authenticityChecklist = listOf(
                "Official GI Tag #565 Bengal Dokra Registry Tag",
                "100% Hand-wound Beeswax Coil Construction",
                "Single-Cast Non-Ferrous Bell Brass Alloy",
                "Master Artisan Seal & Serial Number Included"
            ),
            materials = listOf("Recycled Bell Metal & Brass Alloy", "Natural Beeswax & Dammar Resin Coils", "Riverbed Clay Core"),
            certifications = listOf("GI Tag: Bengal Dokra #565", "National Crafts Council Masterpiece Grade"),
            citations = listOf(
                Citation(
                    id = "cit_dokra_1",
                    title = "Geographical Indications Registry - Bengal Dokra of Bikna",
                    publisher = "Controller General of Patents, Designs and Trade Marks",
                    sourceType = SourceType.GOVERNMENT_GI_REGISTRY,
                    urlOrReference = "https://ipindia.gov.in/gi-bengal-dokra",
                    publicationYear = "2018",
                    lastVerifiedDate = "2026-08-16",
                    reliabilityScore = "High (Government Registry)",
                    excerpt = "Certifies Bikna in Bankura district as the prime cluster for intricate lost-wax brass and bell-metal sculpture."
                )
            ),
            priceHistory = listOf(
                PricePoint("Jan 2026", 8500.0, "Master Casting Price"),
                PricePoint("Jun 2026", 7600.0, "Pre-Festive Guild Rate"),
                PricePoint("Aug 2026", 6800.0, "Direct Foundry Price")
            ),
            primaryColorHex = 0xFFC5A059,
            tags = listOf("Dokra", "Brass", "Durga", "Bankura", "GI Tag", "Lost-Wax", "Masterpiece"),
            isSeasonal = true,
            festivalTag = "Durga Puja Heritage Centerpiece",
            isPriceDrop = true
        ),
        Product(
            id = "prod_patachitra_wall_plates",
            name = "Bengal Patachitra Hand-Painted Wall Display Plates (Shorai Set)",
            regionalName = "পটচিত্র আঁকা মাটির ও কাঠের সরা (Patachitra Shorai)",
            category = ProductCategory.PAINTINGS_FOLK_ART,
            state = "West Bengal",
            district = "Paschim Medinipur",
            villageOrCluster = "Naya, Pingla & Kalighat",
            price = 2250.0,
            originalPrice = 2900.0,
            rating = 4.9,
            reviewCount = 112,
            giCertified = true,
            giRegistrationNo = "GI Application No. 562 (Bengal Patachitra)",
            artisanId = "art_patachitra_plates_1",
            artisanName = "Monimala Chitrakar & Studio",
            stockCount = 16,
            shortDescription = "Set of hand-painted circular wooden and terracotta wall hanging plates (Shorai) depicting Durga Mukha, Radha-Krishna, and Kalighat motifs with painted Matkas.",
            whatDescription = "A stunning set of decorative wall-hanging plates (traditional Bengal 'Shorai' / Sara) crafted from seasoned wood and terracotta. Adorned with expressive circular compositions of Maa Durga's divine gaze, Radha-Krishna melodies, and Kalighat folklore bordered with concentric alpana motifs in rich natural mineral tones.",
            whereContext = "Naya village in Pingla, Paschim Medinipur, and Kalighat folk art schools in West Bengal. Tracing roots back to ritual wedding and temple pot lid paintings used in rural harvest celebrations.",
            whySignificance = "Transforms traditional ritual clay canvases into timeless contemporary wall art. The bold graphic eyes, lyrical hand movements, and mineral colors reflect the living heart of Bengali folk identity.",
            whoArtisanStory = "Hand-painted by Monimala Chitrakar using natural pigments derived from crushed semi-precious stones, indigo leaves, turmeric roots, and bel fruit gum binder.",
            howCareAndBuy = "Includes rear brass hanging loop for easy wall mounting. Wipe surface with dry feather duster. Do not expose to heavy moisture or direct rain.",
            authenticityScore = 99,
            authenticityChecklist = listOf(
                "Official GI Tag #562 Bengal Patachitra Authenticity Label",
                "Natural Mineral & Herbal Pigments (Zero Chemical Paints)",
                "Solid Wood & Terracotta Base with Protective Natural Seal",
                "Ready-to-Hang Brass Wall Fixture Mounted"
            ),
            materials = listOf("Seasoned Mango Wood & Terracotta Disc", "Natural Mineral Earth Dyes", "Organic Tree Resin Varnish"),
            certifications = listOf("GI Tag: Bengal Patachitra #562", "Bengal Folk Art Guild Certified"),
            citations = listOf(
                Citation(
                    id = "cit_plates_1",
                    title = "Ritual Shorai Painting and Patachitra of Southern Bengal",
                    publisher = "State Academy of Folk and Tribal Culture, West Bengal",
                    sourceType = SourceType.ACADEMIC_RESEARCH,
                    urlOrReference = "https://folkculturewb.gov.in/shorai-patachitra",
                    publicationYear = "2020",
                    lastVerifiedDate = "2026-08-11",
                    reliabilityScore = "High (State Cultural Academy)",
                    excerpt = "Analysis of the transition of circular terracotta ritual saras into modern artisanal collector wall hangings."
                )
            ),
            priceHistory = listOf(
                PricePoint("Feb 2026", 2900.0, "Artisan Release"),
                PricePoint("Jun 2026", 2550.0, "Mid-Year Festival"),
                PricePoint("Aug 2026", 2250.0, "Direct Studio Price")
            ),
            primaryColorHex = 0xFFB71C1C,
            tags = listOf("Wall Art", "Plates", "Patachitra", "Shorai", "Durga", "Home Decor", "GI Tag"),
            isSeasonal = true,
            festivalTag = "Durga Puja & Home Renovation Special",
            isPriceDrop = true
        ),
        Product(
            id = "prod_kantha_bolpur",
            name = "Santiniketan Nakshi Kantha Silk Dupatta",
            regionalName = "",
            category = ProductCategory.TEXTILES_HANDLOOM,
            state = "West Bengal",
            district = "Birbhum",
            villageOrCluster = "Bolpur & Suri Clusters",
            price = 3450.0,
            originalPrice = 4200.0,
            rating = 4.9,
            reviewCount = 142,
            giCertified = true,
            giRegistrationNo = "GI Application No. 138 (Nakshi Kantha)",
            artisanId = "art_kantha_1",
            artisanName = "Sujata Mondal Collective",
            stockCount = 18,
            shortDescription = "Handcrafted pure Murshidabad Tussar silk dupatta embroidered with ancestral Nakshi Kantha running stitch motifs.",
            whatDescription = "An heirloom textile crafted from pure handloom Tussar silk, featuring thousands of dense, rhythmic running stitches ('kantha'). The central medallion depicts the tree of life (Kalka/Alpana), framed by village harvest scenes and floral vines.",
            whereContext = "Bolpur village cluster in the red-earth Rarh region of Birbhum, West Bengal. Born out of centuries-old rural Bengali traditions where women repurposed soft layers of vintage sarees with needlework.",
            whySignificance = "Kantha is a poignant visual chronicler of rural women's lives, emotions, and poetic folklore. Endorsed by Rabindranath Tagore at Visva-Bharati, Santiniketan for its artistic distinction.",
            whoArtisanStory = "Hand-embroidered over 6 weeks by master artisan Sujata Mondal and her rural women's collective in Bolpur, ensuring fair wages and preserving century-old stitch variations.",
            howCareAndBuy = "Dry clean recommended for the first two washes; gentle cold water hand wash thereafter with mild silk detergent. Iron inside out on low silk setting.",
            authenticityScore = 99,
            authenticityChecklist = listOf(
                "GI Registry Certification Verified",
                "100% Pure Mulberry & Tussar Silk Base (Silk Mark Certified)",
                "Handmade Needlework with Authentic Running Stitches",
                "Direct Source Tracking from Birbhum Artisan Guild"
            ),
            materials = listOf("Pure Handloom Tussar Silk", "Resham (Silk Thread) Embroidery", "Organic Vegetable Dyes"),
            certifications = listOf("GI Tag: Nakshi Kantha #138", "Handloom Mark", "Silk Mark India"),
            citations = listOf(
                Citation(
                    id = "cit_kantha_1",
                    title = "Geographical Indications Registry - Nakshi Kantha of Bengal",
                    publisher = "Government of India, Controller General of Patents, Designs and Trade Marks",
                    sourceType = SourceType.GOVERNMENT_GI_REGISTRY,
                    urlOrReference = "https://ipindia.gov.in/gi-registry-nakshi-kantha",
                    publicationYear = "2008",
                    lastVerifiedDate = "2026-08-10",
                    reliabilityScore = "High (Government Official)",
                    excerpt = "Nakshi Kantha was granted GI status recognizing Birbhum and adjacent Bengal districts as the protected geographical origin for this unique narrative needle embroidery."
                ),
                Citation(
                    id = "cit_kantha_2",
                    title = "The Craft Traditions of Bengal: Kantha and Santiniketan Revivals",
                    publisher = "Crafts Council of India & Visva-Bharati Archives",
                    sourceType = SourceType.CRAFTS_COUNCIL_INDIA,
                    urlOrReference = "https://craftscouncilindia.org/archives/bengal-kantha",
                    publicationYear = "2019",
                    lastVerifiedDate = "2026-07-22",
                    reliabilityScore = "High (Academic & Cultural Guild)",
                    excerpt = "Documenting the transition of domestic quilt stitching into fine silk wearable art spearheaded by Pratima Devi and Santiniketan artisans in the early 20th century."
                )
            ),
            sourceConflictNote = "While folklore traces primitive Kantha quilts to ancient Buddhist stupa relics (1st millennium BCE), formal commercial silk-embroidered apparel began primarily during the Santiniketan revival in 1920s.",
            priceHistory = listOf(
                PricePoint("Jan 2026", 4200.0, "Artisan Base Price"),
                PricePoint("Apr 2026", 3900.0, "Spring Weave Batch"),
                PricePoint("Jun 2026", 3650.0, "Pre-Festive Price"),
                PricePoint("Aug 2026", 3450.0, "Special Artisan Direct Price")
            ),
            primaryColorHex = 0xFF94442A,
            tags = listOf("Silk", "Handmade", "Kantha", "Bengal", "GI Tag", "Dupatta"),
            isSeasonal = true,
            festivalTag = "Durga Puja & Festive Collection",
            isPriceDrop = true,
            isProductOfTheDay = true
        ),
        Product(
            id = "prod_blue_pottery_jaipur",
            name = "Jaipur Royal Turquoise Floral Blue Pottery Urn",
            regionalName = "",
            category = ProductCategory.POTTERY_CERAMICS,
            state = "Rajasthan",
            district = "Jaipur",
            villageOrCluster = "Kot Jewar & Sanganer Artisans",
            price = 1890.0,
            originalPrice = 2400.0,
            rating = 4.8,
            reviewCount = 98,
            giCertified = true,
            giRegistrationNo = "GI Application No. 20 (Jaipur Blue Pottery)",
            artisanId = "art_blue_pottery_1",
            artisanName = "Kripal Singh Kripawat Guild",
            stockCount = 12,
            shortDescription = "Vibrant cobalt and turquoise hand-painted ceramic urn made without clay using ground quartz crystal.",
            whatDescription = "An authentic non-clay ceramic decorative urn crafted using a unique dough made from powdered quartz stone, cullet glass, fuller's earth (Multani Mitti), and gum. Adorned with cobalt blue and copper turquoise Persian floral arabesques.",
            whereContext = "Originating in Kot Jewar and Jaipur city clusters. Introduced to Rajasthan under Sawai Ram Singh II in the 19th century through traditional artisan exchanges with Turko-Persian masters.",
            whySignificance = "One of the world's rare ceramic arts that does not use traditional river clay. The pieces are semi-translucent, impervious to moisture, and retain their luminous blue brilliance for generations.",
            whoArtisanStory = "Crafted by Mahesh Sharma and his village pottery guild in Kot Jewar, utilizing natural mineral pigments and solar-fired traditional kilns.",
            howCareAndBuy = "Wipe with a damp micro-fiber cloth; do not scrub with metallic wire. Keep as decorative urn or indoor dry botanical vase. Handle with care.",
            authenticityScore = 97,
            authenticityChecklist = listOf(
                "Non-clay quartz paste body test verified",
                "Cobalt & Copper oxide natural mineral glaze",
                "Jaipur GI certified registry tag attached",
                "Hand-painted brush stroke variances confirming no screen printing"
            ),
            materials = listOf("Ground Quartz Crystal", "Glass Powder", "Fuller's Earth", "Copper Turquoise Glaze"),
            certifications = listOf("GI Tag: Jaipur Blue Pottery #20", "Rajasthan Handicrafts Board Verified"),
            citations = listOf(
                Citation(
                    id = "cit_bp_1",
                    title = "Geographical Indications Registry - Blue Pottery of Jaipur",
                    publisher = "Ministry of Commerce & Industry, Government of India",
                    sourceType = SourceType.GOVERNMENT_GI_REGISTRY,
                    urlOrReference = "https://ipindia.gov.in/gi-blue-pottery-jaipur",
                    publicationYear = "2006",
                    lastVerifiedDate = "2026-08-01",
                    reliabilityScore = "High (Government Registry)",
                    excerpt = "Official confirmation of the geographic boundaries of Jaipur district and specific quartz paste formulation criteria for authorized GI mark users."
                )
            ),
            priceHistory = listOf(
                PricePoint("Feb 2026", 2400.0, "Kiln Opening Batch"),
                PricePoint("May 2026", 2100.0, "Direct Summer Harvest"),
                PricePoint("Aug 2026", 1890.0, "Monsoon Cultural Drop")
            ),
            primaryColorHex = 0xFF3A6167,
            tags = listOf("Ceramics", "Blue Pottery", "Jaipur", "GI Tag", "Decor"),
            isSeasonal = false,
            isPriceDrop = true
        ),
        Product(
            id = "prod_pashmina_srinagar",
            name = "Kashmiri Handspun Sozni Pashmina Heirloom Shawl",
            regionalName = "",
            category = ProductCategory.TEXTILES_HANDLOOM,
            state = "Jammu & Kashmir",
            district = "Srinagar",
            villageOrCluster = "Old Downtown Craft Ward & Budgam Weavers",
            price = 14500.0,
            originalPrice = 17500.0,
            rating = 5.0,
            reviewCount = 64,
            giCertified = true,
            giRegistrationNo = "GI Application No. 46 (Kashmir Pashmina)",
            artisanId = "art_pashmina_1",
            artisanName = "Ghulam Mohammad Mir Studio",
            stockCount = 6,
            shortDescription = "Authentic certified 100% pure Changthangi cashmere with delicate hand needle Sozni embroidery.",
            whatDescription = "An ultra-luxurious, featherweight heirloom wrap spun from grade-A underfleece of Capra Hircus goats native to the high-altitude Changthang plateau (14,000+ ft). Featuring border Sozni needle embroidery done with single silk strands.",
            whereContext = "Woven on traditional wooden pit looms in the historic artisan alleyways of Downtown Srinagar, Kashmir, where the craft was institutionalized in the 15th century by Sultan Zain-ul-Abidin.",
            whySignificance = "Considered the pinnacle of global luxury textiles. Each thread is less than 15 microns thick (6x finer than human hair), providing extraordinary warmth while passing effortlessly through a finger ring.",
            whoArtisanStory = "Spun on the traditional Kashmiri spinning wheel ('Yender') by elderly women artisans in Budgam and woven by master weaver Ghulam Mohammad Mir over 120 artisan days.",
            howCareAndBuy = "Store wrapped in breathable muslin with natural cedar balls; strictly dry clean only. Protect from moisture and direct prolonged sunlight.",
            authenticityScore = 100,
            authenticityChecklist = listOf(
                "Indian Institute of Carpet Technology (IICT) Microscopic Fiber Certificate",
                "Non-synthetic pure 13.5 micron Changthangi Cashmere confirmed",
                "GI Secure Hologram Label attached with unique QR traceability",
                "100% Handloom Woven with unfinished diamond weave edge"
            ),
            materials = listOf("100% Raw Changthangi Cashmere", "Fine Mulberry Silk Sozni Thread"),
            certifications = listOf("GI Tag: Kashmir Pashmina #46", "IICT Tested & Tagged", "Craft Development Institute Kashmir"),
            citations = listOf(
                Citation(
                    id = "cit_pash_1",
                    title = "Kashmir Pashmina GI Verification & Testing Protocol",
                    publisher = "Craft Development Institute (CDI) Srinagar & Ministry of Textiles",
                    sourceType = SourceType.GOVERNMENT_GI_REGISTRY,
                    urlOrReference = "https://cdikashmir.com/pashmina-testing-guidelines",
                    publicationYear = "2010",
                    lastVerifiedDate = "2026-08-12",
                    reliabilityScore = "High (Official Standards Authority)",
                    excerpt = "Sets rigorous laboratory test requirements for mean fiber diameter under 16 microns and absence of powerloom machine creasing."
                )
            ),
            priceHistory = listOf(
                PricePoint("Jan 2026", 17500.0, "Winter Weave Release"),
                PricePoint("Jun 2026", 15800.0, "Summer Artisan Subsidy"),
                PricePoint("Aug 2026", 14500.0, "Direct-to-Collector Price")
            ),
            primaryColorHex = 0xFF6E2E1A,
            tags = listOf("Pashmina", "Cashmere", "Luxury", "Kashmir", "GI Tag", "Shawl"),
            isSeasonal = true,
            festivalTag = "Royal Gifting & Winter Heirloom",
            isPriceDrop = true
        ),
        Product(
            id = "prod_channapatna_toys",
            name = "Channapatna Non-Toxic Wooden Balancing Toy Set",
            regionalName = "",
            category = ProductCategory.WOOD_METAL_CRAFT,
            state = "Karnataka",
            district = "Ramanagara",
            villageOrCluster = "Channapatna Toy Town",
            price = 850.0,
            originalPrice = 1100.0,
            rating = 4.9,
            reviewCount = 210,
            giCertified = true,
            giRegistrationNo = "GI Application No. 23 (Channapatna Toys & Dolls)",
            artisanId = "art_channapatna_1",
            artisanName = "Venkatesh Woodcraft Studio",
            stockCount = 35,
            shortDescription = "Eco-friendly, smooth ivory-wood stacking game polished with natural lacquer and non-toxic turmeric & indigo dyes.",
            whatDescription = "A safe, tactile 7-piece balancing animal puzzle handcrafted on wood-turning lathes using soft Wrightia tinctoria (Aale Mara) timber and polished using the friction heat of dry palm leaves and vegetable lac.",
            whereContext = "Channapatna town, located 60 km from Bengaluru on the Mysore highway. Originally patronized by Tipu Sultan who invited Persian artisans to train local wood turners in the late 18th century.",
            whySignificance = "World-renowned as a 100% non-toxic, child-safe, biodegradable alternative to plastic toys. Free of sharp edges, micro-plastics, lead, or synthetic binders.",
            whoArtisanStory = "Shaped and polished by master toy maker B.V. Venkatesh, continuing his grandfather's eco-lathe practice in Ramanagara district.",
            howCareAndBuy = "Wipe clean with a slightly dry cotton cloth. Do not soak in water to preserve the lustrous natural vegetable lacquer gloss.",
            authenticityScore = 98,
            authenticityChecklist = listOf(
                "Certified Ivory Wood (Wrightia tinctoria) source",
                "Natural vegetable dyes (Turmeric, Kumkum, Indigo) chemical-free",
                "Channapatna Artisans Guild GI stamp",
                "Smooth rounded edges complying with international child safety standards"
            ),
            materials = listOf("Seasoned Ivory Wood (Aale Mara)", "Natural Shellac", "Turmeric & Indigo Pigments"),
            certifications = listOf("GI Tag: Channapatna Toys #23", "BIS Certified Child-Safe", "Zero-Plastic Certified"),
            citations = listOf(
                Citation(
                    id = "cit_cp_1",
                    title = "Geographical Indications Registry - Channapatna Toys and Dolls",
                    publisher = "Controller General of Patents, Designs and Trade Marks, India",
                    sourceType = SourceType.GOVERNMENT_GI_REGISTRY,
                    urlOrReference = "https://ipindia.gov.in/gi-channapatna-toys",
                    publicationYear = "2006",
                    lastVerifiedDate = "2026-07-15",
                    reliabilityScore = "High (Government Registry)",
                    excerpt = "Designated Channapatna region as the sole authentic production cluster for natural lacquer turned woodcrafts."
                )
            ),
            priceHistory = listOf(
                PricePoint("Mar 2026", 1100.0, "Standard Retail"),
                PricePoint("Jul 2026", 950.0, "Monsoon Community Price"),
                PricePoint("Aug 2026", 850.0, "Fair Trade Price")
            ),
            primaryColorHex = 0xFFA67B4C,
            tags = listOf("Toys", "Wooden", "Kids", "Non-toxic", "Eco-friendly", "Karnataka", "GI Tag"),
            isSeasonal = false,
            isPriceDrop = true
        ),
        Product(
            id = "prod_pattachitra_puri",
            name = "Raghurajpur Heritage Tree of Life Pattachitra Scroll",
            regionalName = "",
            category = ProductCategory.PAINTINGS_FOLK_ART,
            state = "Odisha",
            district = "Puri",
            villageOrCluster = "Raghurajpur Heritage Crafts Village",
            price = 4200.0,
            originalPrice = 5000.0,
            rating = 4.9,
            reviewCount = 76,
            giCertified = true,
            giRegistrationNo = "GI Application No. 87 (Odisha Pattachitra)",
            artisanId = "art_pattachitra_1",
            artisanName = "Pranab Kishore Das Atelier",
            shortDescription = "Traditional mineral-pigment scroll painting on treated cotton canvas detailing the cosmic Tree of Life and avian folklore.",
            whatDescription = "An authentic Pattachitra painting executed on handmade 'patti' (layered cotton canvas treated with tamarind seed paste and chalk powder), painted using fine squirrel hair brushes with 100% natural mineral and vegetable colors.",
            whereContext = "Raghurajpur village near Puri, declared India's first Heritage Crafts Village where every resident household practices ancient Chitrakar traditions linked to the Jagannath temple.",
            whySignificance = "Dating back to the 12th century CE, Pattachitra is characterized by intricate linework, bold floral borders, expressive facial profiles, and natural permanence that survives centuries without fading.",
            whoArtisanStory = "Painted by 6th-generation Chitrakar Pranab Kishore Das over 3 weeks of uninterrupted meditative line drawing.",
            howCareAndBuy = "Frame under non-reflective UV glass for archival display. Avoid placing in damp walls or direct humid water mist.",
            stockCount = 8,
            authenticityScore = 99,
            authenticityChecklist = listOf(
                "Handmade Tamarind-seed primed canvas base",
                "Natural stone & conch shell ground pigments (no acrylics)",
                "Raghurajpur Chitrakar Guild Authentication Seal",
                "Intricate multi-tier decorative borders according to Silpa Sastra canons"
            ),
            materials = listOf("Tamarind Seed Coated Cotton Canvas", "Natural Mineral Pigments", "Neem Wood Frame ready"),
            certifications = listOf("GI Tag: Odisha Pattachitra #87", "Lalit Kala Akademi Verified"),
            citations = listOf(
                Citation(
                    id = "cit_patta_1",
                    title = "The Chitrakars of Raghurajpur: Temple Arts of Odisha",
                    publisher = "National Museum of Handicrafts and Handlooms & IGNCA",
                    sourceType = SourceType.ACADEMIC_RESEARCH,
                    urlOrReference = "https://ignca.gov.in/pattachitra-heritage-study",
                    publicationYear = "2017",
                    lastVerifiedDate = "2026-08-05",
                    reliabilityScore = "High (National Cultural Institute)",
                    excerpt = "Comprehensive study on the continuous iconography, natural color extraction formulas, and master-disciple lineages of Raghurajpur."
                )
            ),
            priceHistory = listOf(
                PricePoint("Jan 2026", 5000.0, "Gallery Price"),
                PricePoint("May 2026", 4500.0, "Artisan Direct"),
                PricePoint("Aug 2026", 4200.0, "Gaonova Exclusive Direct")
            ),
            primaryColorHex = 0xFF3B6E6A,
            tags = listOf("Painting", "Folk Art", "Pattachitra", "Odisha", "GI Tag", "Wall Art"),
            isSeasonal = false
        ),
        Product(
            id = "prod_kannauj_mitti_attar",
            name = "Kannauj Petrichor Deg-Bhapka Mitti Attar (Baked Earth)",
            regionalName = "",
            category = ProductCategory.NATURAL_FRAGRANCE,
            state = "Uttar Pradesh",
            district = "Kannauj",
            villageOrCluster = "Bada Bazar & Saraimeera Distilleries",
            price = 2250.0,
            originalPrice = 2800.0,
            rating = 5.0,
            reviewCount = 189,
            giCertified = true,
            giRegistrationNo = "GI Application No. 433 (Kannauj Perfume)",
            artisanId = "art_attar_1",
            artisanName = "Haji Munna Lal Distilleries",
            stockCount = 22,
            shortDescription = "World's most famous natural petrichor scent — the sublime aroma of fresh monsoon rain on baked Gangetic clay, hydro-distilled in pure sandalwood base.",
            whatDescription = "Authentic hydro-distilled Mitti Attar capturing the ephemeral scent of first monsoon rain on sun-parched earth. Made by baking clean alluvial Gangetic clay tablets, distilling them in copper cauldrons ('Degs'), and infusing the vapor into pure organic Mysore sandalwood oil base.",
            whereContext = "Kannauj city on the banks of the Ganges, known as the 'Grasse of the East', where hydro-distillation has been practiced uninterrupted since the Vedic and Harshavardhana eras (7th century CE).",
            whySignificance = "100% alcohol-free, non-synthetic, and deeply grounding. It is one of humanity's most ancient perfume formulas, completely non-toxic and skin therapeutic.",
            whoArtisanStory = "Distilled by 5th-generation master perfumer Haji Munna Lal using fire-wood copper stills and camel-hide curing flasks ('Kuppi') for natural mellowing.",
            howCareAndBuy = "Apply a small dab to pulse points (wrists, collarbone) using the glass wand. Store in a cool dry place away from heat. Matures like fine wine over years.",
            authenticityScore = 99,
            authenticityChecklist = listOf(
                "Hydro-distilled via copper Deg-Bhapka system (Zero synthetic aroma chemicals)",
                "Pure Indian Sandalwood oil carrier base verified",
                "GI Tagged Kannauj Perfume Authenticity Seal",
                "Alcohol-Free 100% Pure Botanical Concentrate"
            ),
            materials = listOf("Baked Alluvial Earth (Mitti)", "Hydro-Distilled Water Vapour", "Pure Sandalwood Oil (Santalum album)"),
            certifications = listOf("GI Tag: Kannauj Perfume #433", "FFDC Certified Pure Natural", "IFRA Safety Standards Compliant"),
            citations = listOf(
                Citation(
                    id = "cit_attar_1",
                    title = "Fragrance and Flavour Development Centre (FFDC) Kannauj Quality Reports",
                    publisher = "Ministry of MSME, Government of India",
                    sourceType = SourceType.GOVERNMENT_GI_REGISTRY,
                    urlOrReference = "https://ffdcindia.org/kannauj-mitti-attar-standards",
                    publicationYear = "2021",
                    lastVerifiedDate = "2026-08-08",
                    reliabilityScore = "High (National Fragrance Authority)",
                    excerpt = "Technical chemical profiling confirming the unique presence of natural geosmin and petrichor fractions hydro-distilled into santalol matrices."
                )
            ),
            priceHistory = listOf(
                PricePoint("Jan 2026", 2800.0, "Season Opening"),
                PricePoint("Jun 2026", 2500.0, "Monsoon Batch Release"),
                PricePoint("Aug 2026", 2250.0, "Artisan Direct Price")
            ),
            primaryColorHex = 0xFF8C6839,
            tags = listOf("Fragrance", "Attar", "Petrichor", "Kannauj", "Natural", "GI Tag"),
            isSeasonal = true,
            festivalTag = "Monsoon & Festive Gifting"
        ),
        Product(
            id = "prod_bankura_horse",
            name = "Bishnupur Terracotta Bankura Heritage Horse",
            regionalName = "",
            category = ProductCategory.POTTERY_CERAMICS,
            state = "West Bengal",
            district = "Bankura",
            villageOrCluster = "Panchmura Village Craft Cluster",
            price = 1250.0,
            originalPrice = 1600.0,
            rating = 4.8,
            reviewCount = 115,
            giCertified = true,
            giRegistrationNo = "GI Application No. 544 (Bankura Panchmura Terracotta Craft)",
            artisanId = "art_kantha_1",
            artisanName = "Panchmura Kumbhakar Collective",
            stockCount = 20,
            shortDescription = "Iconic erect-eared terracotta horse crafted by master potters of Panchmura, the global emblem of Indian handicrafts.",
            whatDescription = "Hand-thrown hollow terracotta horse sculpture characterized by elongated erect neck, tall pointed ears, symmetrically arched eyebrows, and circular disc ornaments crafted on a potter's wheel and wood-kiln fired to warm burnt sienna terracotta.",
            whereContext = "Panchmura village in Bankura district, West Bengal, patronized by Malla kings who built the famous 17th-century terracotta temples of Bishnupur.",
            whySignificance = "Chosen by the All India Handicrafts Board as the official symbol of Indian artisan heritage. Originally offered to village folk deities (Dharmathakur) as sacred protectors of the hearth.",
            whoArtisanStory = "Shaped in separate geometric hollow components by master Kumbhakar potters of Panchmura and assembled while clay is leather-hard.",
            howCareAndBuy = "Dust gently with a soft dry brush. Safe for indoor display or shaded patio. Avoid dropping on hard stone floors.",
            authenticityScore = 98,
            authenticityChecklist = listOf(
                "Panchmura Alluvial Riverbed Clay composition",
                "Hand-wheel thrown symmetrical hollow sections",
                "Natural kiln oxidized burnt terracotta color (No chemical paint)",
                "Official Panchmura Terracotta GI seal"
            ),
            materials = listOf("Bankura Alluvial Clay", "Natural Straw & Sand Temper", "Wood-fired Kiln Finish"),
            certifications = listOf("GI Tag: Bankura Panchmura Terracotta #544", "West Bengal Handicrafts Development Corp"),
            citations = listOf(
                Citation(
                    id = "cit_bankura_1",
                    title = "Geographical Indications Registry - Bankura Panchmura Terracotta Craft",
                    publisher = "Government of India Intellectual Property Office",
                    sourceType = SourceType.GOVERNMENT_GI_REGISTRY,
                    urlOrReference = "https://ipindia.gov.in/gi-bankura-terracotta",
                    publicationYear = "2018",
                    lastVerifiedDate = "2026-07-20",
                    reliabilityScore = "High (Government Registry)",
                    excerpt = "Recognizes the distinctive hollow cylindrical throwing technique and sacred temple votive tradition of Panchmura village."
                )
            ),
            priceHistory = listOf(
                PricePoint("Jan 2026", 1600.0, "Standard Price"),
                PricePoint("May 2026", 1400.0, "Pre-Festive"),
                PricePoint("Aug 2026", 1250.0, "Artisan Direct")
            ),
            primaryColorHex = 0xFF94442A,
            tags = listOf("Terracotta", "Pottery", "Horse", "Bengal", "GI Tag", "Home Decor"),
            isSeasonal = false
        ),
        Product(
            id = "prod_assam_muga_silk",
            name = "Assam Royal Golden Muga Silk Mekhela Chador",
            regionalName = "",
            category = ProductCategory.TEXTILES_HANDLOOM,
            state = "Assam",
            district = "Kamrup",
            villageOrCluster = "Sualkuchi Silk Village",
            price = 11200.0,
            originalPrice = 13500.0,
            rating = 5.0,
            reviewCount = 48,
            giCertified = true,
            giRegistrationNo = "GI Application No. 55 (Muga Silk of Assam)",
            artisanId = "art_kantha_1",
            artisanName = "Sualkuchi Weavers Cooperative",
            stockCount = 5,
            shortDescription = "Naturally shimmering golden silk found exclusively in Assam, becomes more lustrous with every wash and lasts over 50 years.",
            whatDescription = "An authentic handloom Mekhela Chador woven from pure, undyed Muga silk produced by the wild Antheraea assamensis silkworm that feeds on Som and Soalu leaves. Features traditional Kingkhap and Jappi woven motifs.",
            whereContext = "Sualkuchi village, known as the 'Manchester of the East', nestled along the Brahmaputra River in Kamrup district, Assam.",
            whySignificance = "Muga is India's most exclusive natural wild silk, historically reserved solely for Ahom royalty. It possesses natural UV resistance and a rich natural golden sheen that never requires artificial dyeing.",
            whoArtisanStory = "Woven on wooden frame looms by women weavers of Sualkuchi who spend up to 40 days crafting a single authentic two-piece set.",
            howCareAndBuy = "Wash gently in cold water with mild reetha (soapnut) or dry clean. Sunlight enhances its natural golden luster.",
            authenticityScore = 100,
            authenticityChecklist = listOf(
                "100% Pure Organic Undyed Muga Wild Silk Certificate",
                "Silk Mark India Certified with barcode verification",
                "Assam Muga GI Tag Registration label",
                "Natural golden amber sheen test verified"
            ),
            materials = listOf("Pure Natural Muga Wild Silk", "Traditional Zari Accents"),
            certifications = listOf("GI Tag: Muga Silk #55", "Silk Mark India", "Assam Apex Weavers Board"),
            citations = listOf(
                Citation(
                    id = "cit_muga_1",
                    title = "Muga Silk of Assam - Geographical Indication Status & Sericulture Study",
                    publisher = "Central Silk Board & Ministry of Textiles, Govt. of India",
                    sourceType = SourceType.GOVERNMENT_GI_REGISTRY,
                    urlOrReference = "https://csb.gov.in/sericulture/assam-muga-silk",
                    publicationYear = "2007",
                    lastVerifiedDate = "2026-08-02",
                    reliabilityScore = "High (Central Silk Board Authority)",
                    excerpt = "Confirms that the wild silkworm Antheraea assamensis cannot survive outside the unique subtropical microclimate of the Brahmaputra valley."
                )
            ),
            priceHistory = listOf(
                PricePoint("Feb 2026", 13500.0, "Spring Harvest Release"),
                PricePoint("Jun 2026", 12000.0, "Cooperative Subsidy"),
                PricePoint("Aug 2026", 11200.0, "Artisan Direct Price")
            ),
            primaryColorHex = 0xFFA67B4C,
            tags = listOf("Silk", "Muga", "Assam", "Handloom", "GI Tag", "Heirloom"),
            isSeasonal = true,
            festivalTag = "Bihu & Wedding Royalty"
        )
    )

    val sampleRegions = listOf(
        RegionInfo(
            state = "West Bengal",
            district = "Birbhum & Bankura",
            villageOrCluster = "Bolpur, Santiniketan, Panchmura & Bishnupur",
            stateCapital = "Kolkata",
            geographicOverview = "The Rarh region characterized by rich red laterite soil, lush terracotta temples, and the cultural heritage of Tagore's Santiniketan.",
            famousCrafts = listOf("Nakshi Kantha Embroidery", "Terracotta Bankura Horse", "Dokra Lost Wax Metal", "Baluchari Silk"),
            culturalHeritageSummary = "A powerhouse of folk ballads (Baul), terracotta architecture, and literary revivals celebrating rural dignity.",
            artisanCommunitiesCount = 420,
            climateAndMaterials = "Hot tropical summers with alluvial red clays and fine mulberry sericulture.",
            coordinatesDisplay = "23.67° N, 87.72° E (Birbhum)"
        ),
        RegionInfo(
            state = "Rajasthan",
            district = "Jaipur & Nagaur",
            villageOrCluster = "Kot Jewar, Sanganer, Bagru & Makrana",
            stateCapital = "Jaipur",
            geographicOverview = "The regal Aravalli heartland rich in mineral pigments, quartz deposits, and centuries-old royal guild workshops.",
            famousCrafts = listOf("Jaipur Blue Pottery", "Bagru Dabu Block Printing", "Makrana Marble Art", "Kathputli Marionettes"),
            culturalHeritageSummary = "Courts of Rajput kings patronized master artisans across Central Asia, creating a distinct synthesis of desert resilience and royal refinement.",
            artisanCommunitiesCount = 680,
            climateAndMaterials = "Semi-arid desert climate ideal for rapid natural solar drying of glazes and natural indigo dye vats.",
            coordinatesDisplay = "26.91° N, 75.78° E (Jaipur)"
        ),
        RegionInfo(
            state = "Jammu & Kashmir",
            district = "Srinagar & Pulwama",
            villageOrCluster = "Downtown Shahr-e-Khaas, Budgam & Pampore",
            stateCapital = "Srinagar (Summer) / Jammu (Winter)",
            geographicOverview = "Alpine Kashmir valley cradled by the Himalayas, fed by the Jhelum river, home to ancient mulberry groves and high-altitude pastures.",
            famousCrafts = listOf("Changthangi Pashmina Shawls", "Kashmiri Walnut Wood Carving", "Pampore Mogra Saffron", "Papier-Mâché"),
            culturalHeritageSummary = "Artisan traditions codified under the Sufi saint Mir Sayyid Ali Hamadani and royal workshops of the 15th century.",
            artisanCommunitiesCount = 350,
            climateAndMaterials = "Temperate alpine climate producing 12-14 micron cashmere and dense Juglans regia walnut wood.",
            coordinatesDisplay = "34.08° N, 74.79° E (Srinagar)"
        ),
        RegionInfo(
            state = "Karnataka",
            district = "Ramanagara & Mysore",
            villageOrCluster = "Channapatna Toy Town & Bidar Guilds",
            stateCapital = "Bengaluru",
            geographicOverview = "Deccan plateau bordered by the Western Ghats, renowned for sacred sandalwood forests and artisan toy clusters.",
            famousCrafts = listOf("Channapatna Wooden Toys", "Mysore Silk Crepe", "Bidriware Silver Inlay", "Kinhal Woodcraft"),
            culturalHeritageSummary = "Pioneering eco-artisan hub initiated under Tipu Sultan and expanded by Mysore Wodeyar rulers with natural botanical dyes.",
            artisanCommunitiesCount = 510,
            climateAndMaterials = "Tropical savanna climate favoring Wrightia tinctoria (ivory-wood) timber and natural lac production.",
            coordinatesDisplay = "12.65° N, 77.20° E (Channapatna)"
        ),
        RegionInfo(
            state = "Odisha",
            district = "Puri & Cuttack",
            villageOrCluster = "Raghurajpur Heritage Crafts Village & Pipli",
            stateCapital = "Bhubaneswar",
            geographicOverview = "Coastal eastern plains with sacred maritime trade legacy and ancient temple architecture.",
            famousCrafts = listOf("Raghurajpur Pattachitra", "Tarakasi Silver Filigree", "Pipli Appliqué Craft", "Sambalpuri Ikat"),
            culturalHeritageSummary = "Living cultural village of Raghurajpur where every single household is a practicing hereditary master studio.",
            artisanCommunitiesCount = 290,
            climateAndMaterials = "Coastal tropical climate yielding palm-leaf scrolls, natural mineral stones, and sea-conch pigments.",
            coordinatesDisplay = "19.81° N, 85.83° E (Puri)"
        ),
        RegionInfo(
            state = "Uttar Pradesh",
            district = "Kannauj & Varanasi",
            villageOrCluster = "Bada Bazar Kannauj & Varanasi Weaver Mohallas",
            stateCapital = "Lucknow",
            geographicOverview = "Fertile Gangetic plains with historic river trade routes and ancient perfumery kilns.",
            famousCrafts = listOf("Kannauj Mitti Attar", "Banarasi Katan Silk Brocade", "Moradabad Brassware", "Lucknow Chikankari"),
            culturalHeritageSummary = "Perfumery capital of India practicing unbroken Vedic hydro-distillation and sacred temple incense crafts.",
            artisanCommunitiesCount = 740,
            climateAndMaterials = "Alluvial Gangetic soils, Damask rose cultivation, and aged sandalwood oil carriers.",
            coordinatesDisplay = "27.05° N, 79.91° E (Kannauj)"
        )
    )

    val sampleGiftGuides = listOf(
        GiftingGuide(
            id = "gift_festive_parents",
            occasionTitle = "Diwali & Durga Puja Family Blessings",
            recipientType = "Parents, Elders & Puja Spaces",
            recommendedRegion = "West Bengal (Bankura & Pingla)",
            description = "Thoughtfully chosen traditional sacred heirlooms: authentic Bikna Dokra Durga idol, Natungram Lokkhi Pecha, and hand-painted Patachitra folk lanterns.",
            budgetRange = "₹1,450 - ₹6,800",
            culturalEtiquette = "Presenting sacred Dokra brass and wooden Lokkhi Pecha bestows prosperity, good health, and blessings to the household.",
            productIds = listOf("prod_dokra_durga_idol", "prod_natungram_wooden_owl", "prod_patachitra_lantern")
        ),
        GiftingGuide(
            id = "gift_kids_sustainable",
            occasionTitle = "Eco-Living & Sustainable Dining",
            recipientType = "Friends, Students & Mindful Homes",
            recommendedRegion = "West Bengal & Karnataka",
            description = "Zero-plastic lifestyle essentials: Hand-painted Patachitra stainless steel water bottles and natural vegetable-lacquered wooden toys.",
            budgetRange = "₹850 - ₹1,300",
            culturalEtiquette = "Gifting non-toxic reusable bottles and wooden handicrafts promotes mindful living and supports women artisan clusters.",
            productIds = listOf("prod_painted_steel_bottles", "prod_channapatna_toys")
        ),
        GiftingGuide(
            id = "gift_wedding_royal",
            occasionTitle = "Weddings, Housewarming & Grand Art",
            recipientType = "New Homeowners & Art Collectors",
            recommendedRegion = "Bengal, Kashmir & Rajasthan",
            description = "Masterpiece centerpieces: GI-certified Bengal Patachitra wall Shorai display plates, Dokra Durga idols, and Changthangi Pashmina.",
            budgetRange = "₹2,250 - ₹14,500+",
            culturalEtiquette = "Gifting GI-certified artisan wall plates and bronze sculptures celebrates traditional craftsmanship in modern spaces.",
            productIds = listOf("prod_patachitra_wall_plates", "prod_dokra_durga_idol", "prod_pashmina_srinagar")
        ),
        GiftingGuide(
            id = "gift_corporate_global",
            occasionTitle = "Corporate & International Delegations",
            recipientType = "Global Clients & Esteemed Partners",
            recommendedRegion = "West Bengal & Uttar Pradesh",
            description = "Distinctive conversation starters backed by verified GI documentation: Natungram Lokkhi Pecha, Patachitra folk lanterns, and Mitti Attar.",
            budgetRange = "₹1,450 - ₹2,250",
            culturalEtiquette = "Accompanied by an official provenance certificate and artisan biography that shares the authentic story of India.",
            productIds = listOf("prod_patachitra_lantern", "prod_natungram_wooden_owl", "prod_kannauj_mitti_attar")
        )
    )

    private val _products = MutableStateFlow(sampleProducts)
    val products = _products.asStateFlow()

    fun getProductById(id: String): Product? {
        return _products.value.find { it.id == id }
    }

    fun getArtisanById(id: String): Artisan? {
        return sampleArtisans.find { it.id == id }
    }

    fun getRegionByState(state: String): RegionInfo? {
        return sampleRegions.find { it.state.equals(state, ignoreCase = true) }
    }

    fun searchProducts(
        query: String,
        category: ProductCategory? = null,
        state: String? = null,
        maxPrice: Double? = null,
        onlyGi: Boolean = false,
        onlyPriceDrops: Boolean = false
    ): List<Product> {
        val q = query.trim().lowercase()
        return _products.value.filter { p ->
            val matchesQuery = q.isEmpty() ||
                    p.name.lowercase().contains(q) ||
                    p.regionalName.lowercase().contains(q) ||
                    p.state.lowercase().contains(q) ||
                    p.district.lowercase().contains(q) ||
                    p.villageOrCluster.lowercase().contains(q) ||
                    p.shortDescription.lowercase().contains(q) ||
                    p.whatDescription.lowercase().contains(q) ||
                    p.tags.any { it.lowercase().contains(q) }

            val matchesCategory = category == null || p.category == category
            val matchesState = state == null || state == "All India" || p.state.equals(state, ignoreCase = true)
            val matchesPrice = maxPrice == null || p.price <= maxPrice
            val matchesGi = !onlyGi || p.giCertified
            val matchesDrop = !onlyPriceDrops || p.isPriceDrop

            matchesQuery && matchesCategory && matchesState && matchesPrice && matchesGi && matchesDrop
        }
    }
}
