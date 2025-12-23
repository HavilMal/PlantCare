package com.plantCare.plantcare.ui.screens.plantScreen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.utils.FileUtil
import com.plantCare.plantcare.viewModel.DeviceGalleryScreenViewModel
import com.plantCare.plantcare.viewModel.PlantScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun DeviceGalleryScreen(
    viewModel: DeviceGalleryScreenViewModel = hiltViewModel()
) {
    val navController = NavigationController.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 50)
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch {
                viewModel.saveMedia(uris)
                navController?.popBackStack()
            }
        } else {
            navController?.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageAndVideo
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Opening gallery…")
    }
}