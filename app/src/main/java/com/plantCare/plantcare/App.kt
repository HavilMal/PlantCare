package com.plantCare.plantcare

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.plantCare.plantcare.common.AppNavHost
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.model.AppDatabase
import com.plantCare.plantcare.model.AppRepository
import com.plantCare.plantcare.model.AppViewModel
import com.plantCare.plantcare.model.PlantEditViewModel
import com.plantCare.plantcare.model.PlantRepository
import com.plantCare.plantcare.model.PlantViewModel
import com.plantCare.plantcare.ui.theme.AppTheme


@Composable
fun App(
    viewModel: AppViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.seedDatabase()
    }

    val navController = rememberNavController()

    AppTheme {
        CompositionLocalProvider(NavigationController provides navController) {
            AppNavHost(navController)
        }
    }
}