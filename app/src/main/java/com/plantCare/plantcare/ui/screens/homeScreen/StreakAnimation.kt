package com.plantCare.plantcare.ui.screens.homeScreen


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import com.plantCare.plantcare.utils.RandomUtil



@Composable
fun CurrentStreakAnimation(
    petalPainter: Painter,
    petalColor: Color = Color.Red,
    centerColor: Color = Color.Yellow,
    petalCount: Int = 20,
    maxPetalsPerLayer: Int = 20,
    maxLayers: Int = 1,
    scale: Float = 1.0F,
    rotatingSpeed: Int = 10000 // higher == slower lol
) {
    val randAngleOffset: Float = RandomUtil.genUniNormFloat() * 360F
    val infiniteTransition = rememberInfiniteTransition()
    val petalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                rotatingSpeed,
                easing = LinearEasing),
            repeatMode = RepeatMode.Restart)
    )
    Box(modifier =
        Modifier.fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center)
    {
        if(petalCount > 0 ) {
            Box(
                modifier = Modifier.graphicsLayer
                {
                    rotationZ = petalRotation
                })
            {
                repeat(petalCount) { index ->
                    val angle = (360f / petalCount * index) + randAngleOffset

                    Image(
                        painter = petalPainter,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .graphicsLayer
                            {
                                rotationZ = angle + 90f
                                translationX =
                                    70f * kotlin.math.cos(Math.toRadians(angle.toDouble()))
                                        .toFloat()
                                translationY =
                                    70f * kotlin.math.sin(Math.toRadians(angle.toDouble()))
                                        .toFloat()
                            }
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(petalCount == 0){
            Text(
                text = "You have no streak yet. Water plants daily to earn streak",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color.Gray
            )}

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(centerColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = petalCount.toString(),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}