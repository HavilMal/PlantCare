package com.plantCare.plantcare.ui.screens.plantEditScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.ui.components.TopBar
import com.plantCare.plantcare.viewModel.PlantEditViewModel

@Composable
fun PlantEditScaffold(
    plantEditViewModel: PlantEditViewModel,
    content: Content,
) {
    val navController = NavigationController.current
    Scaffold(
        topBar = {
            TopBar(
                text = "Edit Plant"
            ) {
                IconButton(
                    onClick = {
                        plantEditViewModel.savePlant()
                        navController?.popBackStack()
                              },
                ) {
                    Icon(
                        Icons.Filled.Save, "Save"
                    )
                }
            }
        }
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}