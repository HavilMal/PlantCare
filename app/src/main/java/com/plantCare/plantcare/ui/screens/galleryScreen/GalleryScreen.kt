package com.plantCare.plantcare.ui.screens.galleryScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.ui.components.InlinedText
import com.plantCare.plantcare.viewModel.GalleryViewModel

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isEmpty = state.media.isEmpty()
    val isLoading = state.isLoading

    GalleryScaffold { modifier ->
        if (!isLoading && isEmpty) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    modifier = Modifier.size(200.dp),
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                )
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    text = "This plant doesn't have any photos.",
                )
                InlinedText(
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    annotatedText = buildAnnotatedString {
                        append("To add photos to gallery use ")
                        appendInlineContent("add_icon")
                        append(" button while viewing plant.")
                    },
                    annotationDictionary = mapOf("add_icon" to { tint ->
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = tint
                        )
                    }),
                )

                return@GalleryScaffold
            }
        }

        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.media) { item ->
                PlantMediaCard(item)
                {
                    viewModel.deletePlantMedia(item)
                }
            }
        }
    }
}
