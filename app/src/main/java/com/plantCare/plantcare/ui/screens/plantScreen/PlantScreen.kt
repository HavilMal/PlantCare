package com.plantCare.plantcare.ui.screens.plantScreen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
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

    val items = uiState.images.mapIndexed { index, file ->
        CarouselItem(
            id = index,
            imageFile = file,
            contentDescription = "Plant photo no. $index"
        )
    }

    val navController = NavigationController.current
    val carouselState = rememberCarouselState { items.count() }

    PlantScaffold(uiState) { modifier ->
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
                    val bitmap = remember(item.imageFile) {
                        BitmapFactory.decodeFile(item.imageFile.absolutePath)
                    }
                    Image(
                        modifier = Modifier
                            .height(205.dp)
                            .maskClip(MaterialTheme.shapes.extraLarge)
                            .clickable {
                                navController?.navigate(Route.GALLERY.routeWithArgs(uiState.plant?.id))
                            },
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = item.contentDescription,
                        contentScale = ContentScale.Crop
                    )
                }
            }


            item {
                SensorCard()
            }

            item {
                PlantNotesCard()
            }

            item {
                PlantTipsCard()
            }
        }
    }
}