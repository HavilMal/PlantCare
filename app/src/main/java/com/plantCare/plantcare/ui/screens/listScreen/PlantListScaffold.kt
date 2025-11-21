package com.plantCare.plantcare.ui.screens.listScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.common.route
import com.plantCare.plantcare.ui.components.BottomBar
import com.plantCare.plantcare.ui.components.TopBar
import com.plantCare.plantcare.viewModel.EditMode

@Composable
fun PlantListScaffold(content: Content) {
    val navController = NavigationController.current

    Scaffold(
        topBar = { TopBar("List") },
        bottomBar = { BottomBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController?.navigate(route(Route.PLANT_EDIT.route, EditMode.ADD))
                }
            ) {
                Icon(Icons.Default.Add, "Add plant")
            }
        }

    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}