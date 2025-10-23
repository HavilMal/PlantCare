package com.plantCare.plantcare

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.plantCare.plantcare.screens.CalendarScreen
import com.plantCare.plantcare.screens.HomeScreen
import com.plantCare.plantcare.screens.listScreen.ListScreen
import com.plantCare.plantcare.screens.SearchScreen
import com.plantCare.plantcare.screens.SettingsScreen

enum class Route(
    val route: String,
    val label: String,
    val icon: ImageVector? = null
) {
    // Main routes
    HOME("home", "Home", Icons.Filled.Home),
    PLANT_LIST("list", "List", Icons.Filled.Star),
    CALENDAR("calendar", "Calendar", Icons.Filled.DateRange),
    SEARCH("search", "Search", Icons.Filled.Search),

    // Other routes
    SETTINGS("settings", "Settings"),

    PLANT("plant", "Plant"),
    PLANT_EDIT("plant_edit", "Plant Edit"),

    NOTE("note", "Note"),
    GALLERY("gallery", "Gallery"),
    CAMERA("camera", "Camera")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController,
        startDestination = "main",
        modifier = modifier
    ) {

        navigation(startDestination = Route.HOME.route, route="main") {
            composable(Route.HOME.route) { HomeScreen() }
            composable(Route.SETTINGS.route) { SettingsScreen() }
        }

        navigation(startDestination = Route.PLANT_LIST.route, route = "list_route") {
            composable(Route.PLANT_LIST.route) { ListScreen() }
            composable(Route.PLANT.route) {  }
            composable(Route.PLANT_EDIT.route) { }
            composable(Route.NOTE.route) { }
            composable(Route.GALLERY.route) { }
            composable(Route.CAMERA.route) { }
        }

        composable(Route.CALENDAR.route) { CalendarScreen() }
        composable(Route.SEARCH.route) { SearchScreen() }
    }

}