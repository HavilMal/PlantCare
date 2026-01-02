package com.plantCare.plantcare.utils

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import java.io.File

fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onResult: (File?) -> Unit
) {

    val outputDir = context.cacheDir
    val photoFile = File(
        outputDir,
        "photo_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    controller.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                onResult(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onResult(null)
            }
        }
    )
}