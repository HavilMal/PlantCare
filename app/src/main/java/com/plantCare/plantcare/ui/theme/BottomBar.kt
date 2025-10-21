package com.plantCare.plantcare.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController


enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val description: String
) {
    HOME("home", "Home", Icons.Default.Home, "Home screen"),
    PLANTS("plants", "Plants", Icons.Default.Star, "Plants list screen"),
    CALENDAR("calendar", "Calendar", Icons.Default.DateRange, "Watering calendar"),
    SEARCH("search", "Search", Icons.Default.Search, "Search using image recognition")
}


@Composable
fun BottomNavbar(navController: NavController) {
    var selectedDestination by rememberSaveable { mutableIntStateOf(Destination.HOME.ordinal ) }
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        Destination.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = index == selectedDestination,
                onClick = {
                    navController.navigate(route=destination.route)
                    selectedDestination = index
                },
                icon = {},
                label = {Text(destination.label)}
            )
        }
    }
}