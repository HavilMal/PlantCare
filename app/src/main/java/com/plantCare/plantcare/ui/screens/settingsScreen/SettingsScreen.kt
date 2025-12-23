package com.plantCare.plantcare.ui.screens.settingsScreen

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.viewModel.SettingsViewModel
import com.plantCare.plantcare.utils.MapUtil.mapPinSelection
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    data class Setting(
        val name: String,
        val description: String,
        val onClick: () -> Unit
    )

    val settings = listOf(
        Setting("Location", uiState.location.toString()) {

            mapPinSelection(context) { lat, lon ->
                Log.d("lukas", "LAT = $lat, LON = $lon")
                viewModel.setLocation(lat,lon)
            }
        },
        Setting("Date format", uiState.dateFormat,{}),
        Setting("Notifications mode", uiState.notificationMode.toString(),{}),
        Setting("Unit", "Set your units",{}),
    )


    SettingsScaffold { modifier ->
        LazyColumn(
            modifier = modifier,
        ) {
            settings.forEach { it  ->
                item {
                    SettingsItem(it.name, it.description, it.onClick)
                }
             }
        }
    }
}