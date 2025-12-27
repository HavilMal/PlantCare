package com.plantCare.plantcare.ui.screens.plantEditScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.plantCare.plantcare.ui.theme.size
import com.plantCare.plantcare.ui.theme.spacing


enum class SensorButtonState {
    ADD_SENSOR,
    SCANNING,
    REMOVE_SENSOR,
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@ExperimentalPermissionsApi
fun SensorButton(
    state: SensorButtonState,
    onScanForSensor: () -> Unit,
    onRemoveSensor: () -> Unit,
) {
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    Button(
        onClick = {
            when (state) {
                SensorButtonState.ADD_SENSOR -> {
                    permissionState.permissions.filter { !it.status.isGranted }.forEach {
                        it.launchPermissionRequest()
                    }

                    if (permissionState.permissions.all { it.status.isGranted }) {
                        onScanForSensor()
                    }
                }

                SensorButtonState.SCANNING -> {}
                SensorButtonState.REMOVE_SENSOR -> {
                    onRemoveSensor()
                }
            }

        },
        modifier = Modifier.fillMaxWidth(),
        shape = ButtonDefaults.squareShape
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            when (state) {
                SensorButtonState.ADD_SENSOR -> {
                    Icon(Icons.Filled.Add, "Add Sensor")
                    Text("Add Sensor")
                }

                SensorButtonState.SCANNING -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(MaterialTheme.size.small)
                    )
                    Text("Scanning")
                }

                SensorButtonState.REMOVE_SENSOR -> {
                    Icon(Icons.Default.Cancel, "Remove Sensor")
                    Text("Remove Sensor")
                }
            }
        }
    }
}