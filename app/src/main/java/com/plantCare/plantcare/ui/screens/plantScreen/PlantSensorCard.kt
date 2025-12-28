package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.plantCare.plantcare.R
import com.plantCare.plantcare.service.SensorData
import com.plantCare.plantcare.ui.components.FillableSVG
import com.plantCare.plantcare.ui.theme.size
import com.plantCare.plantcare.ui.theme.spacing
import kotlin.math.PI
import kotlin.math.cos


@Composable
fun NoData(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = MaterialTheme.spacing.extraSmall,
                alignment = Alignment.CenterHorizontally,
            )
        ) {
            content()
        }
    }
}

@Composable
fun SensorCard(
    hasSensor: Boolean,
    bluetoothOn: Boolean,
    sensorData: SensorData?,
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!hasSensor) {
            NoData {
                Icon(Icons.Default.SensorsOff, "No sensor")
                Text("No sensor connected")
            }
            return@Card
        }

        if (!bluetoothOn) {
            NoData {
                Icon(Icons.Default.BluetoothDisabled, "No Bluetooth")
                Text("Bluetooth is off")
            }
            return@Card
        }

        if (sensorData == null) {
            NoData {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(MaterialTheme.size.small)
                )
                Text("Loading sensor data")
            }
            return@Card
        }

        Row(
            modifier = Modifier
                .padding(MaterialTheme.spacing.medium)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // todo does not fill fully at 100%
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Humidity")
                FillableSVG(
                    modifier = Modifier.size(MaterialTheme.size.large),
                    svg = R.drawable.water_drop,
                    fill = sensorData.humidity,
                    fillColor = Color.Blue,
                    backgroundColor = Color.LightGray,
                    fillShape = { size, height ->
                        val waveLength = size.width / 2.5f
                        val waveHeight = 10f
                        val offset = 1f
                        Path().apply {
                            moveTo(0f, size.height)

                            for (x in 0..size.width.toInt()) {
                                val y =
                                    (size.height - height) + (cos((x / waveLength) * (2 * PI)).toFloat() * waveHeight + offset)
                                lineTo(x.toFloat(), y)
                            }

                            lineTo(size.width, size.height)
                            close()
                        }
                    }
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Light")
                FillableSVG(
                    modifier = Modifier.size(MaterialTheme.size.large),
                    svg = R.drawable.sun,
                    fill = sensorData.light,
                    fillColor = Color.Yellow,
                    backgroundColor = Color.LightGray
                )
            }
        }
    }
}