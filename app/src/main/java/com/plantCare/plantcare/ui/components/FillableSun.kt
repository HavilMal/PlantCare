package com.plantCare.plantcare.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.plantCare.plantcare.R

@Composable
fun FillableSun(
    modifier: Modifier = Modifier,
    @DrawableRes svg: Int = R.drawable.sun,
    fill: Float,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
) {
    val painter = painterResource(svg)
    val infiniteTransition = rememberInfiniteTransition(label = "bobTransition")

    val bobOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = "wave1"
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
                with(painter) {
                    draw(
                        size = size,
                        colorFilter = ColorFilter.tint(backgroundColor),
                    )
                }

                drawCircle(
                    color = Color(0xFFFF8F41),
                    radius = size.height / 2,
                    blendMode = BlendMode.SrcIn,
                    center = Offset(size.width / 2, (size.height * 1.5f) - (size.height * animatedFill) + bobOffset),
                )
            }
    )
}