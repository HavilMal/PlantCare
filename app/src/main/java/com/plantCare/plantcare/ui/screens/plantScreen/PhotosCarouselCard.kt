package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.plantCare.plantcare.ui.theme.spacing
import com.plantCare.plantcare.utils.FileUtil
import com.plantCare.plantcare.utils.MediaUtil
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosCarouselCard(
    onImageClick: () -> Unit,
    onTakePhoto: () -> Unit,
    media: List<File>,
) {
    if (media.isEmpty()) {
        Card(
            modifier = Modifier
                .height(205.dp)
                .fillMaxWidth()
                .clickable {
                onTakePhoto()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
               Icon(Icons.Default.CameraAlt, null)
                Text("Take photo")
            }
        }

        return
    }

    val carouselState = rememberCarouselState { media.size }

    HorizontalMultiBrowseCarousel(
        state = carouselState,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val file = media[i]

        AsyncImage(
            modifier = Modifier
                .height(205.dp)
                .maskClip(MaterialTheme.shapes.extraLarge)
                .clickable {
                    onImageClick()
                },
            contentDescription = null,
            model = if (FileUtil.isVideo(file)) {
                MediaUtil.getImageRepresentationOfVideo(file)
            } else {
                file
            },
            contentScale = ContentScale.Crop
        )
    }
}