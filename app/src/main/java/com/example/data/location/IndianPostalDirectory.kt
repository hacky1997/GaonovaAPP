package com.example.data.location

import kotlin.math.*

/**
 * Metadata about an Indian postal cluster.
 */
data class PostalClusterInfo(
    val pinCode: String,
    val city: String,
    val district: String,
    val state: String,
    val hubCode: String,
    val latitude: Double,
    val longitude: Double,
    val nearbyCraftHeritage: String,
    val estimatedStandardDeliveryDays: Int = 2
)

/**
 * Authentic directory of major Indian postal hubs and clusters,
 * with validation rules and geocoding fallbacks.
 */
object IndianPostalDirectory {

    private val pinCodeRegex = Regex("^[1-9][0-9]{5}$")

    val postalClusters: List<PostalClusterInfo> = listOf(
        // Eastern Region (West Bengal, Odisha, Assam, Bihar)
        PostalClusterInfo("700001", "Kolkata", "Kolkata", "West Bengal", "KOL", 22.5726, 88.3639, "Nakshi Kantha, Santiniketan Leather & Bengal Silk", 1),
        PostalClusterInfo("700091", "Kolkata (Salt Lake)", "North 24 Parganas", "West Bengal", "KOL", 22.5867, 88.4178, "Bengal Handloom & Terracotta", 1),
        PostalClusterInfo("731204", "Bolpur Santiniketan", "Birbhum", "West Bengal", "BHP", 23.6693, 87.6963, "Santiniketan Leather & Nakshi Kantha", 2),
        PostalClusterInfo("722122", "Bishnupur", "Bankura", "West Bengal", "BSN", 23.0760, 87.3200, "Bankura Terracotta Horses & Baluchari Silk", 2),
        PostalClusterInfo("751001", "Bhubaneswar", "Khurda", "Odisha", "BBI", 20.2961, 85.8245, "Raghurajpur Pattachitra & Pipili Applique", 2),
        PostalClusterInfo("752012", "Puri", "Puri", "Odisha", "PRI", 19.8135, 85.8312, "Stone Carvings & Palm Leaf Engraving", 2),
        PostalClusterInfo("781001", "Guwahati", "Kamrup", "Assam", "GAU", 26.1445, 91.7362, "Muga Golden Silk & Assamese Japi", 3),
        PostalClusterInfo("800001", "Patna", "Patna", "Bihar", "PAT", 25.5941, 85.1376, "Madhubani Paintings & Sikki Grass", 2),

        // Northern & Western Region (Rajasthan, UP, Delhi, Kashmir, Maharashtra, Gujarat)
        PostalClusterInfo("302001", "Jaipur", "Jaipur", "Rajasthan", "JAI", 26.9124, 75.7873, "Jaipur Blue Pottery, Bagru Block Prints & Meenakari", 1),
        PostalClusterInfo("342001", "Jodhpur", "Jodhpur", "Rajasthan", "JDH", 26.2389, 73.0243, "Mojari Footwear & Jodhpur Woodcraft", 2),
        PostalClusterInfo("190001", "Srinagar", "Srinagar", "Jammu & Kashmir", "SXR", 34.0837, 74.7973, "Changthangi Pashmina, Sozni Embroidery & Walnut Wood", 2),
        PostalClusterInfo("221001", "Varanasi", "Varanasi", "Uttar Pradesh", "VNS", 25.3176, 82.9739, "Banarasi Katan Brocade & Gulabi Meenakari", 2),
        PostalClusterInfo("209725", "Kannauj", "Kannauj", "Uttar Pradesh", "KNJ", 27.0543, 79.9142, "Hydro-distilled Mitti Attar & Gulab Ruh", 2),
        PostalClusterInfo("110001", "New Delhi", "Central Delhi", "Delhi", "DEL", 28.6139, 77.2090, "Dilli Haat Artisans & Zardozi Weaves", 1),
        PostalClusterInfo("400001", "Mumbai", "Mumbai", "Maharashtra", "BOM", 18.9220, 72.8347, "Kolhapuri Chappals & Paithani Silk", 1),
        PostalClusterInfo("380001", "Ahmedabad", "Ahmedabad", "Gujarat", "AMD", 23.0225, 72.5714, "Patan Patola, Ajrakh & Rogan Art", 2),

        // Southern Region (Karnataka, Tamil Nadu, Kerala, Telangana)
        PostalClusterInfo("560001", "Bengaluru", "Bengaluru Urban", "Karnataka", "BLR", 12.9716, 77.5946, "Channapatna Wooden Toys & Mysore Silk", 1),
        PostalClusterInfo("562160", "Channapatna", "Ramanagara", "Karnataka", "CPT", 12.6518, 77.2089, "GI Certified Lacquerware & Wooden Toys", 2),
        PostalClusterInfo("570001", "Mysuru", "Mysuru", "Karnataka", "MYS", 12.2958, 76.6394, "Mysore Sandalwood Inlay & Ganjifa Cards", 2),
        PostalClusterInfo("600001", "Chennai", "Chennai", "Tamil Nadu", "MAA", 13.0827, 80.2707, "Kanchipuram Silk & Thanjavur Paintings", 1),
        PostalClusterInfo("682001", "Kochi", "Ernakulam", "Kerala", "COK", 9.9312, 76.2673, "Aranmula Metal Mirror & Kasavu Handlooms", 2),
        PostalClusterInfo("500001", "Hyderabad", "Hyderabad", "Telangana", "HYD", 17.3850, 78.4867, "Bidriware Silver Inlay & Pochampally Ikat", 1)
    )

    /**
     * Validates if a string is a valid 6-digit Indian PIN code.
     */
    fun isValidPinCode(pin: String): Boolean {
        val trimmed = pin.trim()
        return pinCodeRegex.matches(trimmed)
    }

    /**
     * Finds cluster metadata by 6-digit PIN code.
     */
    fun findByPin(pin: String): PostalClusterInfo? {
        val trimmed = pin.trim()
        return postalClusters.find { it.pinCode == trimmed }
    }

    /**
     * Finds cluster metadata by city or district name.
     */
    fun findByCity(cityName: String): PostalClusterInfo? {
        val query = cityName.trim().lowercase()
        return postalClusters.find {
            it.city.lowercase().contains(query) ||
                    it.district.lowercase().contains(query) ||
                    query.contains(it.city.lowercase())
        }
    }

    /**
     * Finds cluster metadata by state name.
     */
    fun findByState(stateName: String): PostalClusterInfo? {
        val query = stateName.trim().lowercase()
        return postalClusters.find {
            it.state.lowercase().contains(query) || query.contains(it.state.lowercase())
        }
    }

    /**
     * Finds nearest Indian postal hub based on geographical coordinates (Haversine formula).
     */
    fun findNearest(latitude: Double, longitude: Double): PostalClusterInfo {
        var closest = postalClusters.first()
        var minDistanceKm = Double.MAX_VALUE

        for (cluster in postalClusters) {
            val dist = calculateDistanceKm(latitude, longitude, cluster.latitude, cluster.longitude)
            if (dist < minDistanceKm) {
                minDistanceKm = dist
                closest = cluster
            }
        }
        return closest
    }

    /**
     * Synthesizes a valid LocationContext from user-entered PIN or city.
     */
    fun resolvePinToLocation(pin: String): LocationContext? {
        val trimmed = pin.trim()
        if (!isValidPinCode(trimmed)) return null

        val exactMatch = findByPin(trimmed)
        if (exactMatch != null) {
            return LocationContext(
                city = exactMatch.city,
                district = exactMatch.district,
                state = exactMatch.state,
                postalCode = exactMatch.pinCode,
                country = "India",
                latitude = exactMatch.latitude,
                longitude = exactMatch.longitude,
                hubCode = exactMatch.hubCode,
                confidence = LocationConfidence.HIGH,
                source = LocationSource.MANUAL_PIN,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        }

        // Infer region from PIN code prefix (1st digit zone rule)
        val zoneDigit = trimmed.first()
        val inferredState = when (zoneDigit) {
            '1' -> "Delhi / Haryana / Punjab"
            '2' -> "Uttar Pradesh / Uttarakhand"
            '3' -> "Rajasthan / Gujarat"
            '4' -> "Maharashtra / Goa"
            '5' -> "Andhra Pradesh / Karnataka / Telangana"
            '6' -> "Tamil Nadu / Kerala"
            '7' -> "West Bengal / Odisha / North East"
            '8' -> "Bihar / Jharkhand"
            else -> "India"
        }

        return LocationContext(
            city = "India ($trimmed)",
            district = null,
            state = inferredState,
            postalCode = trimmed,
            country = "India",
            latitude = 22.5726,
            longitude = 88.3639,
            hubCode = "IND",
            confidence = LocationConfidence.MEDIUM,
            source = LocationSource.MANUAL_PIN,
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
