package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.plantCare.plantcare.common.WithPermission
import com.plantCare.plantcare.service.SensorData
import com.plantCare.plantcare.ui.components.ContentCompositionRow
import com.plantCare.plantcare.ui.components.FillableSun
import com.plantCare.plantcare.ui.components.FillableWaterDrop
import com.plantCare.plantcare.ui.components.TextCard
import com.plantCare.plantcare.ui.theme.size
import com.plantCare.plantcare.ui.theme.spacing


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SensorCard(
    modifier: Modifier = Modifier,
    hasSensor: Boolean,
    bluetoothOn: Boolean,
    sensorData: SensorData?,
    onGetSensorData: () -> Unit,
) {
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    if (!hasSensor) {
        TextCard(
            modifier = modifier,
            title = "Sensor",
            hasContent = false,
        ) {
            Icon(Icons.Default.SensorsOff, "No sensor")
            Text("No sensor connected")
        }
        return
    }

    if (!bluetoothOn) {
        TextCard(
            modifier = modifier,
            title = "Sensor",
            hasContent = false,
        ) {
            Icon(Icons.Default.BluetoothDisabled, "No Bluetooth")
            Text("Bluetooth is off")
        }
        return
    }

    TextCard(
        modifier = modifier.fillMaxWidth(),
        title = "Sensor",
        hasContent = sensorData != null
    ) {
        WithPermission(
            requestedPermissions = listOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            LaunchedEffect(permissionState.allPermissionsGranted) {
                onGetSensorData()
            }

            if (sensorData == null) {
                ContentCompositionRow(false) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(MaterialTheme.size.small)
                    )
                    Text("Loading sensor data")
                }

                return@WithPermission
            }

            Row(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.medium)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Humidity")
                    FillableWaterDrop(
                        modifier = Modifier.size(MaterialTheme.size.large),
                        fill = sensorData.humidity,
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Light")
                    FillableSun(
                        modifier = Modifier.size(MaterialTheme.size.large),
                        fill = sensorData.light,
                    )
                }
            }
        }
    }
}