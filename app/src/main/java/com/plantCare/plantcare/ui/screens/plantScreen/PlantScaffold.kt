package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.common.route
import com.plantCare.plantcare.ui.components.TopBar
import com.plantCare.plantcare.viewModel.EditMode

@Composable
fun PlantScaffold(content: Content) {
    val navController = NavigationController.current

    Scaffold(
        topBar = {
            TopBar(
                text = Route.PLANT.label,
                actionButton = {
                    IconButton(
                        onClick = {
                            navController?.navigate(route(Route.PLANT_EDIT.route, EditMode.EDIT, 0))
                        }
                    ) {
                        Icon(
                            Icons.Filled.Edit, "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = { PlantActionButton() },
        floatingActionButtonPosition = FabPosition.End
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }

}