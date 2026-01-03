package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.ui.components.TextCard

@Composable
fun PlantDescriptionCard(
    modifier: Modifier = Modifier,
    description: String?,
) {
    TextCard(
        modifier = modifier,
        title = "Description",
        content = description,
        noContent = "No description"
    )
}