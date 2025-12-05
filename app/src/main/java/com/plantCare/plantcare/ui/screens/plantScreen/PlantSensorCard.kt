package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import com.plantCare.plantcare.R
import com.plantCare.plantcare.ui.components.FillableSVG
import com.plantCare.plantcare.ui.theme.size
import com.plantCare.plantcare.ui.theme.spacing
import kotlin.math.PI
import kotlin.math.cos

@Preview
@Composable
fun SensorCard(
    sensorData: Boolean = true,
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (sensorData) {
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
                        fill = 0.5F,
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
                        fill = 0.5F,
                        fillColor = Color.Yellow,
                        backgroundColor = Color.LightGray
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.medium),
                contentAlignment = Alignment.Center,
            ) {
               Text("No sensor connected")
            }
        }
    }
}