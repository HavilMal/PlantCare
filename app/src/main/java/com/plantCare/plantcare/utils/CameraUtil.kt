package com.plantCare.plantcare.utils

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File

class CameraCapture(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    suspend fun startCamera(previewView: PreviewView) {
        cameraProvider = ProcessCameraProvider.awaitInstance(context)

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider?.unbindAll()
        cameraProvider?.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }

    fun takePhoto(onResult: (File?) -> Unit) {
        val capture = imageCapture ?: return onResult(null)

        val outputDir = context.cacheDir
        val photoFile = File(
            outputDir,
            "photo_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
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

    fun stopCamera() {
        cameraProvider?.unbindAll()
    }
}