package com.plantCare.plantcare.ui.screens.homeScreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plantCare.plantcare.R
import com.plantCare.plantcare.utils.RandomUtil
import kotlinx.coroutines.delay
import kotlin.math.pow


@Composable
fun Flower(
    painter: Painter,
    x: Float,
    y: Float,
    size: Dp = 32.dp,
    delayMillis: Int = 0
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
    }

    val colorFilter = ColorFilter.colorMatrix(
        ColorMatrix().apply {
            setToScale(
                RandomUtil.genUniNormFloat(),
                RandomUtil.genUniNormFloat(),
                RandomUtil.genUniNormFloat(),
                1f
            )
        }
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                translationX = x
                translationY = y
                this.alpha = alpha.value
            },
        colorFilter = colorFilter
    )
}
@Composable
fun RandomFlowersAnimation(
    itemCount: Int,
    modifier: Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val sizePx = with(LocalDensity.current) { 32.dp.toPx() }

        val circleSizeDp = 100.dp
        val circleSizePx = with(LocalDensity.current) { circleSizeDp.toPx() }
        val circleCenterX = widthPx / 2
        val circleCenterY = heightPx / 2
        val circleRadius = circleSizePx / 2

        val positions = remember(widthPx, heightPx, itemCount) {
            List(itemCount) {
                var x: Float
                var y: Float
                do {
                    x = RandomUtil.genUniNormFloat() * (widthPx - sizePx)
                    y = RandomUtil.genUniNormFloat() * (heightPx - sizePx)
                } while (
                    itemCount > 0 &&
                    (x + sizePx / 2 - circleCenterX).pow(2) +
                    (y + sizePx / 2 - circleCenterY).pow(2) <
                    (circleRadius + sizePx / 2).pow(2)
                )
                x to y
            }
        }

        val flowerSize = (64.dp - itemCount.dp.coerceIn(0.dp, 32.dp)).coerceIn(16.dp, 64.dp)
        positions.forEachIndexed { index, (x, y) ->
            Flower(
                painter = painterResource(R.drawable.flower),
                x = x,
                y = y,
                size = flowerSize,
                delayMillis = index * 10
            )
        }

        val circleColor = MaterialTheme.colorScheme.inverseSurface
        if (itemCount > 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Canvas(
                    modifier = Modifier.size(circleSizeDp)
                ) {
                    drawCircle(
                        color = circleColor,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = itemCount.toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = circleColor,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (itemCount == 1) "Day" else "Days",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = circleColor,
                        textAlign = TextAlign.Center
                    )
                }

            }
        }

        if (itemCount == 0) {
            Text(
                text = "You have no streak yet. Water plants daily to earn streak",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 0.dp),
            )
        }
    }
}

