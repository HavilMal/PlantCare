package com.plantCare.plantcare.utils

import android.media.MediaMetadataRetriever
import android.util.Log
import coil3.Bitmap
import java.io.File

object MediaUtil {
    fun getImageRepresentationOfVideo(video: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(video.absolutePath)
            retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } finally {
            retriever.release()
        }
    }

    fun getVideoAspectRatio(file: File): Float {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val width =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toFloat()
                ?: 1f
        val height =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toFloat()
                ?: 1f
        val rotation =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt()
                ?: 0

        retriever.release()

        if (rotation == 90 || rotation == 270) {
            return height / width
        } else {
            return width / height
        }
    }

}