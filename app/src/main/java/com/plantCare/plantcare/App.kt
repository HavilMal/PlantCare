package com.plantCare.plantcare

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.plantCare.plantcare.common.AppNavHost
import com.plantCare.plantcare.ui.components.BottomBar
import com.plantCare.plantcare.ui.components.TopBar


@Composable
fun App() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController) }
    ) {
        contentPadding ->
        AppNavHost(navController, Modifier.padding(contentPadding))
    }

}