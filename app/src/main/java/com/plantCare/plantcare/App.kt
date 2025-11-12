package com.plantCare.plantcare

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.plantCare.plantcare.common.AppNavHost
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.model.AppDatabase
import com.plantCare.plantcare.model.AppRepository
import com.plantCare.plantcare.model.PlantRepository
import com.plantCare.plantcare.model.PlantViewModel
import com.plantCare.plantcare.ui.theme.AppTheme


val localAppRepository = staticCompositionLocalOf<AppRepository> {
    error("No AppRepository provided")
}
@Composable
fun App() {

    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context.applicationContext) }
    val appRepo = remember { AppRepository(db, context.applicationContext) }
    LaunchedEffect(Unit) {
        appRepo.seedDatabase()
    }

    val navController = rememberNavController()

    CompositionLocalProvider(localAppRepository provides appRepo) {
        AppTheme {
            CompositionLocalProvider(NavigationController provides navController) {
                AppNavHost(navController)
            }
        }
    }
}