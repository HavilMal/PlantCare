package com.plantCare.plantcare.ui.screens.plantScreen

import android.R.attr.bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plantCare.plantcare.R
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.viewModel.HomeViewModel
import com.plantCare.plantcare.viewModel.PlantScreenViewModel
import java.io.File
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment

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

    PlantScaffold { modifier ->
        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val test = rememberCarouselState { items.count() }
            HorizontalMultiBrowseCarousel(
                state = test,
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

            SensorCard()
            PlantNotesCard()
            PlantTipsCard()
        }
    }
}

