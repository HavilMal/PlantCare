package com.plantCare.plantcare

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.plantCare.plantcare.common.AppNavHost
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.model.AppDatabase


@Composable
fun App() {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context.applicationContext)

    val navController = rememberNavController()
    CompositionLocalProvider(NavigationController provides navController) {
        AppNavHost(navController)
    }
}