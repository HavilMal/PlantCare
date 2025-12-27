package com.plantCare.plantcare.ui.screens.galleryScreen

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.viewModel.PlantScreenViewModel

@Preview
@Composable
fun GalleryScreen(
    viewModel: PlantScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GalleryScaffold { modifier ->
        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(uiState.media) { item ->
                PlantMediaCard(item)
                {
                    viewModel.deletePlantMedia(item)
                }
            }
        }
    }
}
