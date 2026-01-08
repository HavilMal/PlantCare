package com.plantCare.plantcare.ui.screens.galleryScreen

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.plantCare.plantcare.ui.components.MediaThumbnail
import com.plantCare.plantcare.utils.FileUtil
import com.plantCare.plantcare.utils.MediaUtil
import kotlinx.coroutines.launch
import java.io.File


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    file: File,
            modifier: Modifier
) {
    val context = LocalContext.current
    val uri = remember(file) { file.toUri() }

    val exoPlayer = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        }
    )
}

@Composable
fun PlantMediaCard(
    media: File,
    onDelete: suspend () -> Unit)
{
    val scope = rememberCoroutineScope()
    var playVideo by remember { mutableStateOf(false) }
    val isVideo = FileUtil.isVideo(media)
    val modifier = remember { if (isVideo) {
        Modifier.aspectRatio(MediaUtil.getVideoAspectRatio(media))
    } else {
        Modifier
    }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = isVideo) {
                playVideo = true
            }
    ) {
        if (playVideo) {
            VideoPlayer(
                file = media,
                modifier = Modifier.matchParentSize()
            )
        } else {
            MediaThumbnail(
                modifier = Modifier
                    .fillMaxWidth(),
                file = media,
                contentScale = ContentScale.Crop
            )
        }

        if (isVideo && !playVideo) {
            Box(
                modifier = Modifier
                    .matchParentSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play video",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
                onClick = {
                    scope.launch {
                        onDelete()
                    }
                }
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}
