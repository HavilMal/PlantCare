package com.plantCare.plantcare.utils

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
}