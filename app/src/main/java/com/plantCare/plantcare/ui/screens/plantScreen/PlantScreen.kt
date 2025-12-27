package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.common.addQuery
import com.plantCare.plantcare.utils.FileUtil
import com.plantCare.plantcare.utils.MediaUtil
import com.plantCare.plantcare.viewModel.EditMode
import com.plantCare.plantcare.viewModel.PlantScreenViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantScreen(
    viewModel: PlantScreenViewModel = hiltViewModel()
) {
    data class CarouselItem(
        val id: Int,
        val imageFile: File,
        val contentDescription: String
    )

    val uiState by viewModel.uiState.collectAsState()

    val items = uiState.media.mapIndexed { index, file ->
        CarouselItem(
            id = index,
            imageFile = file,
            contentDescription = "Plant photo no. $index"
        )
    }

    val navController = NavigationController.current
    val carouselState = rememberCarouselState { items.count() }

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
    ) { modifier ->
        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(uiState.plant?.name ?: "Loading")
            }
            item {
                Text(uiState.plant?.species ?: "Loading")
            }
            item {
                HorizontalMultiBrowseCarousel(
                    state = carouselState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 16.dp, bottom = 16.dp),
                    preferredItemWidth = 186.dp,
                    itemSpacing = 8.dp,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) { i ->
                    val item = items[i]
                    val file = item.imageFile

                    AsyncImage(
                        modifier = Modifier
                            .height(205.dp)
                            .maskClip(MaterialTheme.shapes.extraLarge)
                            .clickable {
                                navController?.navigate(
                                    Route.GALLERY.routeWithArgs(uiState.plant?.id)
                                )
                            },
                        contentDescription = item.contentDescription,
                        model = if (FileUtil.isVideo(file)) {
                            MediaUtil.getImageRepresentationOfVideo(file)
                        } else {
                            file
                        },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item {
                SensorCard()
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