package com.plantCare.plantcare

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.plantCare.plantcare.screens.HomeScreen
import com.plantCare.plantcare.screens.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable object Home

enum class Route(
    val route: String,
    val label: String
) {
    // Main routes
    HOME("home", "Home"),
    LIST("list", "List"),
    CALENDAR("calendar", "Calendar"),
    SEARCH("search", "Search"),

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

        navigation(startDestination = Route.LIST.route, route = "list_route") {
            composable(Route.LIST.route) { }
            composable(Route.PLANT.route) { }
            composable(Route.PLANT_EDIT.route) { }
            composable(Route.NOTE.route) { }
            composable(Route.GALLERY.route) { }
            composable(Route.CAMERA.route) { }
        }

        composable(Route.CALENDAR.route) { }
        composable(Route.SEARCH.route) { }
    }

}