package com.plantCare.plantcare.ui.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import coil3.video.videoFrameIndex
import com.plantCare.plantcare.utils.FileUtil
import java.io.File


@Composable
fun MediaThumbnail(
    modifier: Modifier = Modifier,
    file: File,
) {
    val context = LocalContext.current

    if (FileUtil.isVideo(file)) {
        val loader = ImageLoader.Builder(context).components {
            add(VideoFrameDecoder.Factory())
        }.build()

        val request = ImageRequest.Builder(context)
            .data(file)
            .videoFrameIndex(0)
            .build()


        AsyncImage(
            modifier = modifier,
            model = request,
            imageLoader = loader,
            contentDescription = "video preview",
            contentScale = ContentScale.Crop
        )
    } else {
        AsyncImage(
            modifier = modifier,
            model = file,
            contentDescription = "plant visual",
            contentScale = ContentScale.Crop
        )
    }
}
