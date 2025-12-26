package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.ui.components.TopBar
import com.plantCare.plantcare.viewModel.PlantScreenUiState

@Composable
fun PlantScaffold(
    state: PlantScreenUiState,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    content: Content,
) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                text = Route.PLANT.label,
                actionButton = {
                    Box {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                leadingIcon = { Icon(Icons.Default.Edit, "Edit") },
                                onClick = { onEdit() }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                leadingIcon = { Icon(Icons.Default.Delete, "Delete") },
                                onClick = { onDelete() }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = { PlantActionButton(state.plant?.id) },
        floatingActionButtonPosition = FabPosition.End
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}

