package com.plantCare.plantcare.utils

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import coil3.Bitmap
import java.io.File

object MediaUtil {
    fun getImageRepresentationOfVideo(video: File) : Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(video.absolutePath)
            retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } finally {
            retriever.release()
        }
    }

    fun getVideoAspectRatio(file: File): Float{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toFloat() ?: 1f
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toFloat() ?: 1f
        retriever.release()
        return width / height
    }
    fun getMediaAspectRatio(file: File, isVideo: Boolean): Float {
        return try {
            if (isVideo) {
                getVideoAspectRatio(file)
            } else {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(file.absolutePath, options)
                options.outWidth.toFloat() / options.outHeight.toFloat()
            }
        } catch (_: Exception) {
            9f/16f
        }
    }
}