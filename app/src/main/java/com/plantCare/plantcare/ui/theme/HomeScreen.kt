package com.plantCare.plantcare.ui.theme

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.plantCare.plantcare.TopBar

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopBar() },
        bottomBar = {BottomNavbar(navController)}
    ) {
        contentPadding -> {}
    }
}