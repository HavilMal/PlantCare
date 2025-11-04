package com.plantCare.plantcare.ui.screens.listScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.ui.screens.MainScaffold

@Preview
@Composable
fun ListScreen() {
    MainScaffold(Route.PLANT_LIST.label) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(100) { num ->
                PlantCard()
            }
        }
    }
}