package com.plantCare.plantcare.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "app_settings")


enum class AppSettingNotification {
    ALWAYS_NOTIFY,
    NEVER
}


private object PreferencesKeys {
    val DATE_FORMAT = stringPreferencesKey("app_setting_date_format")
    val LOCATION = stringPreferencesKey("app_setting_location")
    val NOTIFICATION = stringPreferencesKey("app_setting_notification")
}