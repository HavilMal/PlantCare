package com.plantCare.plantcare.ui.screens.listScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.ui.screens.MainScaffold
import com.plantCare.plantcare.viewModel.ListViewModel

@Composable
fun ListScreen(
    viewModel: ListViewModel = hiltViewModel()
) {
    val plantsUiState = viewModel.listState.collectAsState()
    PlantListScaffold { modifier ->
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