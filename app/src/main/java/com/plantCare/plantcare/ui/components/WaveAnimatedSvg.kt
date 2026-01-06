package com.plantCare.plantcare.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.plantCare.plantcare.R
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun FillableWaterDrop(
    modifier: Modifier = Modifier,
    @DrawableRes svg: Int = R.drawable.water_drop,
    fill: Float,
    color1: Color = Color(0xFF92B7D1),
    color2: Color = Color(0xFF6FA4C6),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveTransition")
    val painter = painterResource(svg)

    val waveOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "wave1"
    )


    val waveOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (-2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "wave3"
    )

    val animatedFill by animateFloatAsState(
        targetValue = fill,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "fillLevel"
    )

    Box(
        modifier = modifier
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                val fillHeight = size.height * animatedFill

                with(painter) {
                    draw(
                        size = size,
                        colorFilter = ColorFilter.tint(backgroundColor),
                    )
                }

                drawPath(
                    path = createWavePath(size, fillHeight, waveOffset1, 12f, size.width),
                    color = color1,
                    blendMode = BlendMode.SrcIn,
                )

                drawPath(
                    path = createWavePath(size, fillHeight, waveOffset2, 18f, size.width * 1.2f),
                    color = color2,
                    blendMode = BlendMode.SrcIn,
                )

            }
    )
}

private fun createWavePath(
    size: Size,
    fillHeight: Float,
    offset: Float,
    waveHeight: Float,
    wavelength: Float
): Path {
    return Path().apply {
        val baseLine = size.height - fillHeight
        moveTo(0f, size.height)
        lineTo(0f, baseLine)

        for (x in 0..size.width.toInt() step 5) {
            val relativeX = x / wavelength
            val y = baseLine + (sin(relativeX * 2 * PI.toFloat() + offset) * waveHeight)
            lineTo(x.toFloat(), y)
        }

        lineTo(size.width, baseLine)
        lineTo(size.width, size.height)
        close()
    }
}


