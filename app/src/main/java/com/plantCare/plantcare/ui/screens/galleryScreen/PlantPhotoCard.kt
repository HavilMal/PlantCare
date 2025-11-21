package com.plantCare.plantcare.ui.screens.galleryScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plantCare.plantcare.R

@Preview
@Composable
fun PlantPhotoCard(image: ImageBitmap) {
    Box(
        modifier = Modifier.clip(
            RoundedCornerShape(16.dp)
        )
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            contentDescription = "photo",
            contentScale = ContentScale.Fit,
            bitmap = image
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd,
        ) {
            IconButton(
                onClick = {/*todo*/},
            ) {
                Icon(Icons.Filled.Delete, "Save")
            }
        }
    }
}
