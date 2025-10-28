package com.plantCare.plantcare.ui.screens

import android.Manifest
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.plantCare.plantcare.common.WithPermission


@Composable
fun CameraView() {
    val previewUseCase = remember { Preview.Builder().build() }
    val localContext = LocalContext.current
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

    fun rebindCameraProvider() {
        cameraProvider?.let { cameraProvider ->
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(LENS_FACING_BACK)
                .build()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                localContext as LifecycleOwner,
                cameraSelector,
                previewUseCase,
            )
            cameraControl = camera.cameraControl
        }
    }


    LaunchedEffect(Unit) {
        cameraProvider = ProcessCameraProvider.awaitInstance(localContext)
        rebindCameraProvider()
    }

    MainScaffold {
        AndroidView(
            factory = { context ->
                PreviewView(context).also {
                    previewUseCase.surfaceProvider = it.surfaceProvider
                }
            }
        )
    }
}

@Composable
fun SearchScreen() {
    WithPermission(permission = Manifest.permission.CAMERA) {
        CameraView()
    }
}