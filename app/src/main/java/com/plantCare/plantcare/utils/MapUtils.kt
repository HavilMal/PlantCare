package com.plantCare.plantcare.utils

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.preference.PreferenceManager


object MapUtil {
    object MapUtils {

        fun locationSelectionMap(
            context: Context,
            initialLat: Double,
            initialLon: Double,
            onLocationConfirmed: (lat: Double, lon: Double) -> Unit
        ) {
            org.osmdroid.config.Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
            val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

            val mapView = org.osmdroid.views.MapView(context).apply {
                setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                setBuiltInZoomControls(true)
                setMultiTouchControls(true)
                isHorizontalMapRepetitionEnabled = false
                isVerticalMapRepetitionEnabled = false
            }

            val mapController = mapView.controller
            val initialPoint = org.osmdroid.util.GeoPoint(initialLat, initialLon)
            mapController.setZoom(5.0)
            mapController.setCenter(initialPoint)

            var currentMarker: org.osmdroid.views.overlay.Marker? = null
            var selectedPoint: org.osmdroid.util.GeoPoint? = initialPoint
            val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
            var wasMultiTouch = false
            var downX = 0f
            var downY = 0f

            currentMarker = org.osmdroid.views.overlay.Marker(mapView).apply {
                position = initialPoint
                title = "Selected location"
                setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
            }
            mapView.overlays.add(currentMarker)

            mapView.setOnTouchListener { _, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        wasMultiTouch = false
                        downX = event.x
                        downY = event.y
                    }
                    MotionEvent.ACTION_POINTER_DOWN -> wasMultiTouch = true
                    MotionEvent.ACTION_UP -> {
                        if (!wasMultiTouch) {
                            val dx = event.x - downX
                            val dy = event.y - downY
                            val distance = kotlin.math.sqrt(dx * dx + dy * dy)
                            if (distance <= touchSlop) {
                                val geoPoint = mapView.projection.fromPixels(event.x.toInt(), event.y.toInt()) as org.osmdroid.util.GeoPoint
                                currentMarker?.let { mapView.overlays.remove(it) }
                                val marker = org.osmdroid.views.overlay.Marker(mapView).apply {
                                    position = geoPoint
                                    title = "Selected location"
                                    setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
                                }
                                mapView.overlays.add(marker)
                                mapView.invalidate()
                                currentMarker = marker
                                selectedPoint = geoPoint
                            }
                        }
                    }
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
            dialog.show()
        }
    }

}
