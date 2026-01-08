package com.plantCare.plantcare.ui.screens.homeScreen

import com.plantCare.plantcare.R
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plantCare.plantcare.utils.RandomUtil
import kotlin.math.pow
import kotlin.random.Random


@Composable
fun Flower(
    painter: Painter,
    x: Float,
    y: Float,
    size: Dp = 32.dp
) {
    val rotation by rememberInfiniteTransition()
        .animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 5000,
                    easing = LinearEasing
                )
            )
        )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                translationX = x
                translationY = y
                rotationZ = rotation
            }
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

        val positions = remember {
            List(itemCount) {
                var x: Float
                var y: Float
                do {
                    x = RandomUtil.genUniNormFloat() * (widthPx - sizePx)
                    y = RandomUtil.genUniNormFloat() * (heightPx - sizePx)
                } while (itemCount > 0 &&
                    (x + sizePx / 2 - circleCenterX).pow(2) +
                    (y + sizePx / 2 - circleCenterY).pow(2) < (circleRadius + sizePx / 2).pow(2)
                )
                x to y
            }
        }

        positions.forEach { (x, y) ->
            Flower(
                painter = painterResource(R.drawable.flower),
                x = x,
                y = y,
                size = 32.dp
            )
        }

        if (itemCount > 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Canvas(
                    modifier = Modifier.size(circleSizeDp)
                ) {
                    drawCircle(
                        color = Color.White,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }

                Text(
                    text = "$itemCount day${if (itemCount == 1) "" else "s"}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (itemCount == 0) {
            Text(
                text = "You have no streak yet. Water plants daily to earn streak",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 0.dp),
                color = Color.Gray
            )
        }
    }
}
