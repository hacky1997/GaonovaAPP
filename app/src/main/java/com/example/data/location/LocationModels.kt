package com.example.data.location

/**
 * Location Confidence levels reflecting data accuracy and postal validity.
 */
enum class LocationConfidence {
    HIGH,     // Precise coordinates + verified Indian postal PIN match
    MEDIUM,   // City + State resolved, postal code approximate/unconfirmed
    LOW,      // Ambiguous boundary or weak geocoding
    MANUAL    // Explicitly chosen or entered by user
}

/**
 * Source of location determination.
 */
enum class LocationSource {
    GPS_PRECISE,
    CELL_APPROXIMATE,
    MANUAL_PIN,
    MANUAL_SELECTION,
    CACHED_FALLBACK,
    DEFAULT
}

/**
 * Immutable location context representing current discovery / regional area.
 */
data class LocationContext(
    val city: String = "Kolkata",
    val district: String? = "Kolkata",
    val state: String = "West Bengal",
    val postalCode: String? = "700001",
    val country: String = "India",
    val latitude: Double? = 22.5726,
    val longitude: Double? = 88.3639,
    val hubCode: String = "KOL",
    val confidence: LocationConfidence = LocationConfidence.HIGH,
    val source: LocationSource = LocationSource.DEFAULT,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
) {
    fun formattedDisplay(): String {
        return if (!postalCode.isNullOrBlank()) {
            "$city, $state · $postalCode"
        } else {
            "$city, $state"
        }
    }

    fun shortDisplay(): String {
        return if (!postalCode.isNullOrBlank()) {
            "$city · $postalCode"
        } else {
            city
        }
    }

    fun hubBadge(): String = hubCode
}

/**
 * Status of location resolution lifecycle.
 */
sealed class LocationStatus {
    object Idle : LocationStatus()
    object Resolving : LocationStatus()
    data class Success(val location: LocationContext) : LocationStatus()
    object PermissionRationaleRequired : LocationStatus()
    data class PermissionDenied(val isPermanent: Boolean = false) : LocationStatus()
    object LocationServicesDisabled : LocationStatus()
    data class ResolutionFailed(val userFriendlyReason: String) : LocationStatus()
}
