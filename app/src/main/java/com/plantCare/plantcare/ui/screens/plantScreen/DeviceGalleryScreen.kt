package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.viewModel.DeviceGalleryScreenViewModel
import kotlinx.coroutines.launch


@Composable
fun DeviceGalleryScreen(
    viewModel: DeviceGalleryScreenViewModel = hiltViewModel()
) {
    val navController = NavigationController.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 50)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.saveMedia(uris)
            navController?.popBackStack()
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