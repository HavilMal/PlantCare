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
import org.osmdroid.views.overlay.Marker


fun map(context: Context, onLocationConfirmed: (lat: Double, lon: Double) -> Unit) {
    val ctx = context.applicationContext
    org.osmdroid.config.Configuration.getInstance()
        .load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx))

    val dialog = Dialog(context)

    val mapView = org.osmdroid.views.MapView(context).apply {
        setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        setBuiltInZoomControls(true)
        setMultiTouchControls(true)
        isHorizontalMapRepetitionEnabled = false
        isVerticalMapRepetitionEnabled = false
    }

    val mapController = mapView.controller
    mapController.setZoom(2.0)
    mapController.setCenter(org.osmdroid.util.GeoPoint(20.0, 0.0))

    var currentMarker: org.osmdroid.views.overlay.Marker? = null
    var selectedPoint: org.osmdroid.util.GeoPoint? = null

    mapView.setOnTouchListener { _, event ->
        if (event.action == android.view.MotionEvent.ACTION_UP) {
            val geoPoint = mapView.projection.fromPixels(event.x.toInt(), event.y.toInt()) as org.osmdroid.util.GeoPoint

            currentMarker?.let { mapView.overlays.remove(it) }

            val marker = org.osmdroid.views.overlay.Marker(mapView).apply {
                position = geoPoint
                title = "Selected location"
                setAnchor(
                    org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                    org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
                )
            }

            mapView.overlays.add(marker)
            mapView.invalidate()
            currentMarker = marker
            selectedPoint = geoPoint
        }
        false
    }

    val container = FrameLayout(context).apply {
        addView(mapView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
    }

    val button = Button(context).apply {
        text = "Confirm"
        setOnClickListener {
            selectedPoint?.let {
                onLocationConfirmed(it.latitude, it.longitude)
                dialog.dismiss()
            } ?: run {
                Toast.makeText(context, "Please select a location first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val buttonParams = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        bottomMargin = 32
    }

    container.addView(button, buttonParams)

    dialog.setContentView(container)
    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    dialog.show()
}






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

            map(context) { lat, lon ->
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