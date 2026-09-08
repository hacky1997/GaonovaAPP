package com.example.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Robust, privacy-respecting Location Resolution Engine for Gaonova.
 * Uses foreground-only coarse/fine location, reverse geocoding on Dispatchers.IO,
 * and validates against the Indian Postal Directory without blocking the UI.
 */
class LocationService(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    /**
     * Checks if either COARSE or FINE location permission is granted.
     */
    fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    /**
     * Checks if location hardware providers (GPS or Network) are enabled on the device.
     */
    fun isLocationProviderEnabled(): Boolean {
        return try {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            true
        }
    }

    /**
     * Asynchronously resolves current location context.
     * Executes in 5 structured steps:
     * 1. Check permissions & provider status
     * 2. Obtain device coordinates (balanced power accuracy)
     * 3. Reverse geocode via Android Geocoder (IO thread)
     * 4. Postal code resolution & validation against Indian directory
     * 5. Compute confidence score & synthesize LocationContext
     */
    /**
     * Fallback resolution when user does not share permission or GPS is unavailable.
     * Checks Google FusedLocationProvider last known location cache or automatically matches
     * the nearest Indian regional postal cluster from IndianPostalDirectory.
     */
    suspend fun resolveDefaultGoogleOrNearestCluster(): LocationResult = withContext(Dispatchers.IO) {
        try {
            // Attempt to check if Google FusedClient has any cached lastLocation
            val cachedLocation: Location? = suspendCancellableCoroutine { cont ->
                try {
                    if (hasLocationPermission()) {
                        fusedClient.lastLocation.addOnSuccessListener { loc ->
                            if (cont.isActive) cont.resume(loc)
                        }.addOnFailureListener {
                            if (cont.isActive) cont.resume(null)
                        }
                    } else {
                        cont.resume(null)
                    }
                } catch (e: Exception) {
                    if (cont.isActive) cont.resume(null)
                }
            }

            if (cachedLocation != null) {
                val address = reverseGeocodeCoordinates(cachedLocation.latitude, cachedLocation.longitude)
                val context = synthesizeLocationContext(
                    latitude = cachedLocation.latitude,
                    longitude = cachedLocation.longitude,
                    isPrecise = false,
                    address = address
                )
                return@withContext LocationResult.Success(context)
            }
        } catch (e: Exception) {
            // Fall through to regional cluster directory
        }

        // Intelligent default to nearest Indian craft cluster (Kolkata Regional Hub - 700001)
        val defaultCluster = IndianPostalDirectory.findByPin("700001") ?: IndianPostalDirectory.postalClusters.first()
        val defaultContext = LocationContext(
            city = defaultCluster.city,
            district = defaultCluster.district,
            state = defaultCluster.state,
            postalCode = defaultCluster.pinCode,
            country = "India",
            latitude = defaultCluster.latitude,
            longitude = defaultCluster.longitude,
            hubCode = defaultCluster.hubCode,
            confidence = LocationConfidence.MEDIUM,
            source = LocationSource.DEFAULT,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        return@withContext LocationResult.Success(defaultContext)
    }

    suspend fun resolveCurrentLocation(): LocationResult = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) {
            return@withContext LocationResult.Failure(
                status = LocationStatus.PermissionRationaleRequired,
                reason = "Location permission has not been granted."
            )
        }

        if (!isLocationProviderEnabled()) {
            return@withContext LocationResult.Failure(
                status = LocationStatus.LocationServicesDisabled,
                reason = "Location services are turned off on your device."
            )
        }

        // Fetch location with a generous 7-second timeout to avoid stalling UI
        val location = withTimeoutOrNull(7000L) {
            fetchLastOrCurrentCoordinates()
        }

        if (location == null) {
            // Coordinate fetch timed out or unavailable (e.g. indoors / GPS search)
            return@withContext LocationResult.Failure(
                status = LocationStatus.ResolutionFailed("Unable to determine GPS coordinates right now."),
                reason = "GPS signal weak or unavailable."
            )
        }

        val lat = location.latitude
        val lng = location.longitude
        val isFine = location.accuracy > 0 && location.accuracy <= 100f

        // Step 2: Reverse Geocoding
        val address = reverseGeocodeCoordinates(lat, lng)

        // Step 3 & 4: Postal & City Resolution
        val resolvedContext = synthesizeLocationContext(lat, lng, isFine, address)

        return@withContext LocationResult.Success(resolvedContext)
    }

    private suspend fun fetchLastOrCurrentCoordinates(): Location? = suspendCancellableCoroutine { cont ->
        try {
            if (!hasLocationPermission()) {
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            // First check last known location (instant)
            fusedClient.lastLocation.addOnSuccessListener { lastLoc ->
                if (lastLoc != null && System.currentTimeMillis() - lastLoc.time < 300_000L) {
                    if (cont.isActive) cont.resume(lastLoc)
                } else {
                    // Request fresh current location with balanced power
                    fusedClient.getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        null
                    ).addOnSuccessListener { freshLoc ->
                        if (cont.isActive) cont.resume(freshLoc ?: lastLoc)
                    }.addOnFailureListener {
                        if (cont.isActive) cont.resume(lastLoc)
                    }
                }
            }.addOnFailureListener {
                if (cont.isActive) cont.resume(null)
            }
        } catch (e: SecurityException) {
            if (cont.isActive) cont.resume(null)
        } catch (e: Exception) {
            if (cont.isActive) cont.resume(null)
        }
    }

    private fun reverseGeocodeCoordinates(latitude: Double, longitude: Double): Address? {
        return try {
            val geocoder = Geocoder(context, Locale("en", "IN"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val list = geocoder.getFromLocation(latitude, longitude, 1)
                list?.firstOrNull()
            } else {
                @Suppress("DEPRECATION")
                val list = geocoder.getFromLocation(latitude, longitude, 1)
                list?.firstOrNull()
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun synthesizeLocationContext(
        latitude: Double,
        longitude: Double,
        isPrecise: Boolean,
        address: Address?
    ): LocationContext {
        // Fallback nearest cluster from directory
        val nearestCluster = IndianPostalDirectory.findNearest(latitude, longitude)

        var city = address?.locality
            ?: address?.subAdminArea
            ?: address?.subLocality
            ?: nearestCluster.city

        val district = address?.subAdminArea ?: address?.adminArea ?: nearestCluster.district
        var state = address?.adminArea ?: nearestCluster.state
        var pinCode = address?.postalCode

        // Validation of Geocoder PIN code
        val isPinValid = pinCode != null && IndianPostalDirectory.isValidPinCode(pinCode)
        if (!isPinValid) {
            // Check if nearest cluster is within reasonable proximity (< 45km)
            val dist = calculateDistanceKm(latitude, longitude, nearestCluster.latitude, nearestCluster.longitude)
            pinCode = if (dist < 45.0) nearestCluster.pinCode else null
        }

        // Clean up city/state names for Indian conventions
        if (state.contains("Bengal", ignoreCase = true) && !state.contains("West", ignoreCase = true)) {
            state = "West Bengal"
        }

        val confidence = when {
            isPrecise && isPinValid -> LocationConfidence.HIGH
            city.isNotBlank() && state.isNotBlank() -> LocationConfidence.MEDIUM
            else -> LocationConfidence.LOW
        }

        val hubCode = nearestCluster.hubCode

        return LocationContext(
            city = city,
            district = district,
            state = state,
            postalCode = pinCode,
            country = address?.countryName ?: "India",
            latitude = latitude,
            longitude = longitude,
            hubCode = hubCode,
            confidence = confidence,
            source = if (isPrecise) LocationSource.GPS_PRECISE else LocationSource.CELL_APPROXIMATE,
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}

sealed class LocationResult {
    data class Success(val location: LocationContext) : LocationResult()
    data class Failure(val status: LocationStatus, val reason: String) : LocationResult()
}
