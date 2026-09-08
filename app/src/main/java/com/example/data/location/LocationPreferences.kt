package com.example.data.location

import android.content.Context
import android.content.SharedPreferences

class LocationPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("gaonova_location_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FIRST_LAUNCH_COMPLETED = "key_first_launch_completed"
        private const val KEY_CITY = "key_city"
        private const val KEY_DISTRICT = "key_district"
        private const val KEY_STATE = "key_state"
        private const val KEY_PIN = "key_pin"
        private const val KEY_HUB = "key_hub"
        private const val KEY_LAT = "key_lat"
        private const val KEY_LNG = "key_lng"
        private const val KEY_CONFIDENCE = "key_confidence"
        private const val KEY_SOURCE = "key_source"
        private const val KEY_LAST_UPDATED = "key_last_updated"
        private const val KEY_LAST_DAILY_WELCOME_DATE = "key_last_daily_welcome_date"
    }

    var isFirstLaunchCompleted: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_LAUNCH_COMPLETED, value).apply()

    var lastDailyWelcomeDate: String?
        get() = prefs.getString(KEY_LAST_DAILY_WELCOME_DATE, null)
        set(value) = prefs.edit().putString(KEY_LAST_DAILY_WELCOME_DATE, value).apply()

    fun saveLocation(location: LocationContext) {
        prefs.edit().apply {
            putString(KEY_CITY, location.city)
            putString(KEY_DISTRICT, location.district)
            putString(KEY_STATE, location.state)
            putString(KEY_PIN, location.postalCode)
            putString(KEY_HUB, location.hubCode)
            if (location.latitude != null) putFloat(KEY_LAT, location.latitude.toFloat())
            if (location.longitude != null) putFloat(KEY_LNG, location.longitude.toFloat())
            putString(KEY_CONFIDENCE, location.confidence.name)
            putString(KEY_SOURCE, location.source.name)
            putLong(KEY_LAST_UPDATED, location.lastUpdatedMillis)
        }.apply()
    }

    fun loadCachedLocation(): LocationContext {
        val city = prefs.getString(KEY_CITY, "Kolkata") ?: "Kolkata"
        val district = prefs.getString(KEY_DISTRICT, "Kolkata")
        val state = prefs.getString(KEY_STATE, "West Bengal") ?: "West Bengal"
        val pin = prefs.getString(KEY_PIN, "700001")
        val hub = prefs.getString(KEY_HUB, "KOL") ?: "KOL"
        val lat = if (prefs.contains(KEY_LAT)) prefs.getFloat(KEY_LAT, 22.5726f).toDouble() else 22.5726
        val lng = if (prefs.contains(KEY_LNG)) prefs.getFloat(KEY_LNG, 88.3639f).toDouble() else 88.3639
        val confidence = try {
            LocationConfidence.valueOf(prefs.getString(KEY_CONFIDENCE, LocationConfidence.HIGH.name) ?: LocationConfidence.HIGH.name)
        } catch (e: Exception) {
            LocationConfidence.HIGH
        }
        val source = try {
            LocationSource.valueOf(prefs.getString(KEY_SOURCE, LocationSource.DEFAULT.name) ?: LocationSource.DEFAULT.name)
        } catch (e: Exception) {
            LocationSource.DEFAULT
        }
        val lastUpdated = prefs.getLong(KEY_LAST_UPDATED, System.currentTimeMillis())

        return LocationContext(
            city = city,
            district = district,
            state = state,
            postalCode = pin,
            country = "India",
            latitude = lat,
            longitude = lng,
            hubCode = hub,
            confidence = confidence,
            source = source,
            lastUpdatedMillis = lastUpdated
        )
    }
}
