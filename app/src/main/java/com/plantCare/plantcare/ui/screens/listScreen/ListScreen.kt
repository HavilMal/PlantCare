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
import com.plantCare.plantcare.model.PlantViewModel
import com.plantCare.plantcare.ui.screens.MainScaffold

@Composable
fun ListScreen(
    plantVM: PlantViewModel = hiltViewModel()
) {
    val plantsUiState = plantVM.uiState.collectAsState()
    MainScaffold(Route.PLANT_LIST.label) { modifier -> 
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