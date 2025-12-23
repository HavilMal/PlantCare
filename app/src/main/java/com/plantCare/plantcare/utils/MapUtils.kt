package com.plantCare.plantcare.utils

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast


object MapUtil {
    fun mapPinSelection(context: Context, onLocationConfirmed: (lat: Double, lon: Double) -> Unit) {
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
}
