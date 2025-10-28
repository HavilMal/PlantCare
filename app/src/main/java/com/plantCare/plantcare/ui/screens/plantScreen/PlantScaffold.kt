package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.layout.Box
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
import com.plantCare.plantcare.ui.components.TopBar

@Composable
fun PlantScaffold(content: Content) {
    val navController = NavigationController.current

    Scaffold(
        topBar = {
            TopBar(
                actionButton = {
                    IconButton(
                        onClick = {
                            navController?.popBackStack()
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