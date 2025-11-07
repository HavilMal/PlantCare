package com.plantCare.plantcare

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.plantCare.plantcare.common.AppNavHost
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.model.AppDatabase
import com.plantCare.plantcare.model.PlantRepository
import com.plantCare.plantcare.model.PlantViewModel
import com.plantCare.plantcare.model.PlantViewModelFactory
import com.plantCare.plantcare.ui.theme.AppTheme


@Composable
fun App() {

    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context.applicationContext) }
    val plantRepo = remember { PlantRepository(context, db.plantDao()) }
    val plantVM: PlantViewModel = viewModel(factory = PlantViewModelFactory(plantRepo))

    LaunchedEffect(Unit) {
        plantVM.deleteAllPlants()
        plantVM.insertPlant("Kaktus Maksiu","Kaktus Kaktus")
    }

    val navController = rememberNavController()

    AppTheme {
        CompositionLocalProvider(NavigationController provides navController) {
            AppNavHost(navController, plantVM)
        }
    }
}