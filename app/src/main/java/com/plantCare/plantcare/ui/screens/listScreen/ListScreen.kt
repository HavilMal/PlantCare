package com.plantCare.plantcare.ui.screens.listScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.plantCare.plantcare.R
import com.plantCare.plantcare.ui.components.InlinedText
import com.plantCare.plantcare.viewModel.ListViewModel


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListScreen(
    viewModel: ListViewModel = hiltViewModel()
) {
    val plantsUiState = viewModel.listState.collectAsState()
    val isEmpty = plantsUiState.value.plants.isEmpty()
    val isLoading = plantsUiState.value.isLoading
    PlantListScaffold { modifier ->
        if (!isLoading && isEmpty) {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AsyncImage(
                    modifier = Modifier.size(200.dp),
                    model = R.drawable.grass,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outlineVariant),
                )
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    text = "You don't have any plants.",
                )
                InlinedText(
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    annotatedText = buildAnnotatedString {
                        append("To add a plant press ")
                        appendInlineContent("add_icon")
                        append(" button.")
                    },
                    annotationDictionary = mapOf("add_icon" to { Icon(Icons.Default.Add, null) }),
                )
            }
            return@PlantListScaffold
        }

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(plantsUiState.value.plants) { plant ->
                PlantCard(plant)
            }
        }
    }
}