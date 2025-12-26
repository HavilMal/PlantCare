package com.plantCare.plantcare

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.plantCare.plantcare.common.AppNavHost
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.viewModel.AppViewModel
import com.plantCare.plantcare.ui.theme.AppTheme


@Composable
fun App(
    viewModel: AppViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.seedDatabase()
//        viewModel.appRepository.setDefaultSettings()
    }

    val navController = rememberNavController()

    AppTheme {
        CompositionLocalProvider(NavigationController provides navController) {
            AppNavHost(navController)
        }
    }
}