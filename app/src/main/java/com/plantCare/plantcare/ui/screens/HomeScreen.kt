package com.plantCare.plantcare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.model.Plant
import com.plantCare.plantcare.model.PlantViewModel
import com.plantCare.plantcare.ui.screens.listScreen.PlantCard


@Composable
fun PlantItem(plant: Plant) {
    Row(
        modifier = Modifier
//            .padding(16.dp)
            .height(48.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = plant.name,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(align = Alignment.CenterVertically),
        )
        Button(onClick = {}) {
            Text("Water")
        }
    }
}

// todo

@Composable
fun HomeScreen(plantVM: PlantViewModel) {
    val navController = NavigationController.current
    val plants by plantVM.getAllPlants().collectAsState(initial = emptyList())
    MainScaffold(
        label = Route.HOME.label,
        actionButton = {
            IconButton(
                onClick = {
                    navController?.navigate(Route.SETTINGS.route)
                }
            ) {
                Icon(Icons.Default.Settings, "Settings")
            }
        }
    ) { modifier ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1F)
                ) { }
            }
            item {
                Text(
                    text = "Water today",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically),
                )
            }
            items(plants) { plant ->
                PlantItem(plant)
            }
        }
    }
}