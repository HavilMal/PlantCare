package com.plantCare.plantcare.ui.screens.plantEditScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.plantCare.plantcare.common.WithPermission
import com.plantCare.plantcare.ui.components.SquareButton
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
    bluetoothOn: Boolean,
    state: SensorButtonState,
    onScanForSensor: () -> Unit,
    onRemoveSensor: () -> Unit,
) {
    if (!bluetoothOn) {
        SquareButton(
            onClick = {},
            enabled = false,
        ) {
            Icon(Icons.Default.BluetoothDisabled, null)
            Text("Turn on Bluetooth")
        }
        return
    }

    WithPermission(
        requestedPermissions = listOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) { permissionsState ->
        SquareButton(
            onClick = {
                when (state) {
                    SensorButtonState.ADD_SENSOR -> {
                        if (!permissionsState.allPermissionsGranted) {
                            permissionsState.launchMultiplePermissionRequest()
                        } else {
                            onScanForSensor()
                        }
                    }

                    SensorButtonState.SCANNING -> {}
                    SensorButtonState.REMOVE_SENSOR -> {
                        onRemoveSensor()
                    }
                }
            },
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (state) {
                    SensorButtonState.ADD_SENSOR -> {
                        Icon(
                            modifier = Modifier.size(MaterialTheme.size.small),
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                        )
                        Text("Add Sensor")
                    }

                    SensorButtonState.SCANNING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(MaterialTheme.size.smallInner),
                            color = MaterialTheme.colorScheme.secondary,
                            strokeWidth = 2.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Text("Scanning")
                    }

                    SensorButtonState.REMOVE_SENSOR -> {
                        Icon(
                            modifier = Modifier.size(MaterialTheme.size.small),
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                        )
                        Text("Remove Sensor")
                    }
                }
            }
        }
    }
}