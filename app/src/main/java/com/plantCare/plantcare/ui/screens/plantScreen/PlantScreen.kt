package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.common.addQuery
import com.plantCare.plantcare.viewModel.EditMode
import com.plantCare.plantcare.viewModel.PlantScreenViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PlantScreen(
    viewModel: PlantScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = NavigationController.current

    PlantScaffold(
        state = uiState,
        onEdit = {
            if (uiState.plant != null) {
                navController?.navigate(
                    Route.PLANT_EDIT.routeWithArgs(EditMode.EDIT)
                        .addQuery("plantId", uiState.plant!!.id)
                )
            }
        },
        onDelete = { viewModel.setDialogState(true) },
        onBack = { navController?.popBackStack() }
    ) { modifier ->
        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                PhotosCarouselCard(
                    onImageClick = {
                        navController?.navigate(
                            Route.GALLERY.routeWithArgs(uiState.plant?.id)
                        )
                    },
                    onTakePhoto = {
                        navController?.navigate(
                            Route.CAMERA.routeWithArgs(uiState.plant?.id)
                        )
                    },
                    media = uiState.media
                )
            }
            item {
                SensorCard(
                    hasSensor = uiState.plant?.sensorAddress != null,
                    bluetoothOn = uiState.bluetoothOn,
                    sensorData = uiState.sensorData,
                    onGetSensorData = {
                        viewModel.getSensorData()
                    },
                )
            }

            item {
               PlantDescriptionCard(
                   description = uiState.plant?.description,
               )
            }

            item {
                PlantNotesCard(notes = uiState.notes, onClick = {
                    navController?.navigate(Route.NOTE_LIST.routeWithArgs(uiState.plant?.id))
                })
            }

            item {
                PlantTipsCard(
                    details = uiState.plantDetails,
                )
            }
        }

        when {
            uiState.dialogOpen -> {
                ConfirmationDialog(
                    onDismissRequest = { viewModel.setDialogState(false) },
                    onConfirmation = {
                        viewModel.setDialogState(false)
                        navController?.popBackStack()
                        viewModel.deleteCurrentPlant()
                    },
                    dialogTitle = "Confirm deletion",
                    dialogText = "Are you sure that you want to delete this plant?",
                    icon = Icons.Default.Delete
                )
            }
        }
    }
}