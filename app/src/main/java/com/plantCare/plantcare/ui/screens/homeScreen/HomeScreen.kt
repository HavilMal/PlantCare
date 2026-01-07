package com.plantCare.plantcare.ui.screens.homeScreen

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.R
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantWateringInfo
import com.plantCare.plantcare.logic.PlantWateringStatus
import com.plantCare.plantcare.logic.WateringStatus
import com.plantCare.plantcare.ui.screens.homeScreen.HomeScaffold
import com.plantCare.plantcare.ui.theme.spacing
import com.plantCare.plantcare.viewModel.HomeViewModel
import com.plantCare.plantcare.viewModel.PlantWateringCardInfo
import com.plantCare.plantcare.viewModel.PlantWateringSection
import kotlinx.coroutines.launch

@Composable
fun PlantItem(
    context : Context,
    plantWateringCardInfo: PlantWateringCardInfo,
    onWater: (Plant) -> Unit
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${plantWateringCardInfo.plant.name}",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(align = Alignment.CenterVertically),
        )

        when(plantWateringCardInfo.wateringStatus){
            WateringStatus.NEEDS_WATERING ->
                Button(onClick = {

                    MediaPlayer.create(context, R.raw.sound_watering).apply {
                        setOnCompletionListener { it.release() }
                        start()
                    }

                    onWater(plantWateringCardInfo.plant)
                }) {
                    Text("Water")
                }
            else -> {}

        }

    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navController = NavigationController.current
    val context  = LocalContext.current
    val uiState = viewModel.homeState.collectAsState()
    val scope = rememberCoroutineScope()

    val petal =  painterResource(R.drawable.petal_long)

    LaunchedEffect(Unit) {
        viewModel.logWeatherData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeScaffold(
            actionButton = {
                IconButton(
                    onClick = {
                        navController?.navigate(Route.SETTINGS.route)
                    }
                ) {
                    Icon(Icons.Default.Settings, "Settings")
                }
            }
        ){ modifier ->

            LazyColumn(
                contentPadding = PaddingValues(MaterialTheme.spacing.medium),
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    CurrentStreakAnimation(petal, androidx.compose.ui.graphics.Color.Red, androidx.compose.ui.graphics.Color.Yellow,20,1,1,1.0F,10000)
//                    CurrentStreakAnimation(petal, androidx.compose.ui.graphics.Color.Red, androidx.compose.ui.graphics.Color.Yellow,uiState.value.userCurrentStreak,1,1,1.0F,10000)
                }

                PlantWateringSection.entries.forEach { section ->
                    val filtered = uiState.value.plantWateringStatuses
                        .filter {
                            section.acceptedStatuses.contains(it.wateringStatus)
                        }
                    if(!filtered.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = 2.dp
                            ) {
                                Column {

                                    Text(
                                        text = section.sectionName,
                                        modifier = Modifier
                                            .padding(start = 16.dp, top = 12.dp, end = 16.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Divider(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        thickness = 1.5.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )

                                    filtered.forEach { status ->
                                        PlantItem(
                                            context,
                                            status,
                                            {
                                                scope.launch {
                                                    viewModel.waterPlant(status.plant.id)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}