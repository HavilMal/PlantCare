package com.plantCare.plantcare.database

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "app_settings")


enum class AppSettingNotificationMode {
    ALWAYS_NOTIFY,
    NEVER
}

private object PreferencesKeys {
    val DATE_FORMAT = stringPreferencesKey("app_setting_date_format")
    val LOCATION_LAT = doublePreferencesKey("app_setting_location_lat")
    val LOCATION_LON = doublePreferencesKey("app_setting_location_lon")
    val NOTIFICATION_MODE = stringPreferencesKey("app_setting_notification")
}

class SettingsRepository(private val context: Context) {

    val dateFormat: Flow<String> =
        context.dataStore.data.map { prefs ->
            prefs[PreferencesKeys.DATE_FORMAT] ?: "dd/mm/yyyy"
        }

    val location: Flow<Pair<Double, Double>> =
        context.dataStore.data.map { prefs ->
            val lat = prefs[PreferencesKeys.LOCATION_LAT] ?: 0.0
            val lon = prefs[PreferencesKeys.LOCATION_LON] ?: 0.0
            lat to lon
        }

    val notificationMode: Flow<AppSettingNotificationMode> =
        context.dataStore.data.map { prefs ->
            prefs[PreferencesKeys.NOTIFICATION_MODE]
                ?.let {AppSettingNotificationMode.valueOf(it) }
                ?: AppSettingNotificationMode.ALWAYS_NOTIFY
        }

    suspend fun setDateFormat(format: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.DATE_FORMAT] = format
        }
    }

    suspend fun setLocation(lat: Double, lon: Double) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.LOCATION_LAT] = lat
            prefs[PreferencesKeys.LOCATION_LON] = lon
        }
    }
    suspend fun getLocation() : Pair<Double, Double> {
        return location.first()
    }

    suspend fun setNotificationMode(setting: AppSettingNotificationMode) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.NOTIFICATION_MODE] = setting.name
        }
    }
    suspend fun setDefault(){
        setDateFormat("dd/mm/yyyy")
        setLocation(0.0,0.0)
        setNotificationMode(AppSettingNotificationMode.ALWAYS_NOTIFY)
    }
}
