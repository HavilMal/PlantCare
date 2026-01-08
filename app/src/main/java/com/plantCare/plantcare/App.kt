package com.plantCare.plantcare

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
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
        viewModel.onAppStart()
    }

    val navController = rememberNavController()

    AppTheme {
        CompositionLocalProvider(NavigationController provides navController) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                AppNavHost(navController)
            }
        }
    }
}