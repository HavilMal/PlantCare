//package com.plantCare.plantcare.ui.screens.galleryScreen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.net.toUri
//import androidx.media3.common.MediaItem
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.ui.PlayerView
//import coil3.ImageLoader
//import coil3.compose.AsyncImage
//import coil3.request.ImageRequest
//import coil3.video.VideoFrameDecoder
//import coil3.video.videoFrameIndex
//import com.plantCare.plantcare.ui.components.MediaThumbnail
//import com.plantCare.plantcare.utils.FileUtil
//import kotlinx.coroutines.launch
//import java.io.File
//
//
//@Composable
//fun VideoPlayer(file: File) {
//    val context = LocalContext.current
//    val uri = remember(file) { file.toUri() }
//
//    val exoPlayer = remember(uri) {
//        ExoPlayer.Builder(context).build().apply {
//            setMediaItem(MediaItem.fromUri(uri))
//            prepare()
//            playWhenReady = true
//        }
//    }
//
//    DisposableEffect(Unit) {
//        onDispose { exoPlayer.release() }
//    }
//
//    AndroidView(
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio( 9f / 16f),
//        factory = {
//            PlayerView(it).apply {
//                player = exoPlayer
//                useController = true
//            }
//        }
//    )
//}
//
//@Composable
//fun PlantMediaCard(media: File, onDelete: suspend () -> Unit) {
//    val scope = rememberCoroutineScope()
//    var playVideo by remember { mutableStateOf(false) }
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(16.dp))
//            .clickable(enabled = FileUtil.isVideo(media)) {
//                playVideo = true
//            }
//    ) {
//        if (playVideo) {
//            VideoPlayer(file = media)
//        } else {
//            MediaThumbnail(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(9f / 16f),
//                file = media
//            )
//        }
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(4.dp),
//            contentAlignment = Alignment.TopEnd
//        ) {
//            IconButton(
//                modifier = Modifier.background(
//                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
//                    shape = CircleShape
//                ),
//                onClick = {
//                    scope.launch {
//                        onDelete()
//                    }
//                }
//            ) {
//                Icon(Icons.Filled.Delete, contentDescription = "Delete")
//            }
//        }
//    }
//}
