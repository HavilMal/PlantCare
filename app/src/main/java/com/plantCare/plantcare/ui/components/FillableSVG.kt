package com.plantCare.plantcare.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage

@Composable
fun FillableSVG(
    modifier: Modifier = Modifier,
    @DrawableRes svg: Int,
    fill: Float,
    fillColor: Color = Color.Black,
    backgroundColor: Color = Color.Gray,
    fillShape: (size: Size, height: Float) -> Path = { size, height ->
        Path().apply {
            addRect(Rect(0f, size.height - height, size.width, size.height))
        }
    },
) {
    Box(
        modifier = modifier,
    ) {
        AsyncImage(
            model = svg,
            contentDescription = null,
            colorFilter = ColorFilter.tint(backgroundColor),
        )
        AsyncImage(
            model = svg,
            contentDescription = null,
            modifier = Modifier
                .clip(RectangleShape)
                .graphicsLayer {
                    clip = true
                }
                .drawWithContent {
                    val height = size.height * fill
                    clipPath(fillShape(size, height)) {
                        this@drawWithContent.drawContent()
                    }
                },
            colorFilter = ColorFilter.tint(fillColor)
        )
    }
}