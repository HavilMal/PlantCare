package com.plantCare.plantcare

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.plantCare.plantcare.common.AppNavHost
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.ui.theme.AppTheme


@Composable
fun App() {
    val navController = rememberNavController()

    AppTheme {
        CompositionLocalProvider(NavigationController provides navController) {
            AppNavHost(navController)
        }
    }
}