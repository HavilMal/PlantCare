package com.plantCare.plantcare.ui.screens.plantCameraCaptureScreen

import android.Manifest
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.WithPermission
import com.plantCare.plantcare.utils.takePhoto
import com.plantCare.plantcare.viewModel.PlantCameraCaptureViewModel


@Composable
fun PlantCameraCaptureView(
    viewModel: PlantCameraCaptureViewModel,
    navController: NavHostController?,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraCapture = remember {
//        CameraCapture(context, lifecycleOwner)
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        cameraCapture.bindToLifecycle(lifecycleOwner)
        onDispose {
            cameraCapture.unbind()
        }
    }

//
//    var previewView by remember { mutableStateOf<PreviewView?>(null) }
//
//    LaunchedEffect(previewView) {
//        previewView?.let { view ->
//            cameraCapture.startCamera(view)
//        }
//    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    controller = cameraCapture
                }
            },
            onRelease = {
                cameraCapture.unbind()
            }
        )
        Button(
            onClick = {
                takePhoto(context, cameraCapture) { file ->
                    viewModel.savePhoto(file)
                    navController?.popBackStack()
                }
//                cameraCapture.takePhoto { file ->
//                    viewModel.savePhoto(file)
//                    cameraCapture.stopCamera()
//                    navController?.popBackStack()
//                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("Take Photo")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PlantCameraCaptureScreen(viewModel: PlantCameraCaptureViewModel = hiltViewModel()) {
    val navController = NavigationController.current
    PlantCameraScaffold(
        navController = navController,
    ) { modifier ->
        WithPermission(
            requestedPermissions = listOf(Manifest.permission.CAMERA),
        ) {
            PlantCameraCaptureView(
                navController = navController,
                viewModel = viewModel,
            )
        }
    }
}