package com.plantCare.plantcare.ui.screens.plantScreen

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.plantCare.plantcare.common.WithPermission
import com.plantCare.plantcare.utils.CameraCapture
import com.plantCare.plantcare.viewModel.PlantCameraCaptureViewModel

@Composable
fun PlantCameraCaptureView(
    viewModel: PlantCameraCaptureViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Instance of the camera helper class
    val cameraCapture = remember {
        CameraCapture(context, lifecycleOwner)
    }

    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    LaunchedEffect(previewView) {
        previewView?.let { view ->
            cameraCapture.startCamera(view)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).also { previewView = it }
            }
        )

        Button(
            onClick = {
                cameraCapture.takePhoto { file ->
                    null // i need to know what plant i am making photo for
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("Take Photo")
        }
    }
}

@Composable
fun PlantCameraCaptureScreen() {
    WithPermission(permission = Manifest.permission.CAMERA) {
       PlantCameraCaptureView()
    }
}