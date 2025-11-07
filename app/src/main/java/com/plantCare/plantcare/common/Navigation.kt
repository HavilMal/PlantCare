package com.plantCare.plantcare.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.plantCare.plantcare.model.PlantViewModel
import com.plantCare.plantcare.ui.screens.CalendarScreen
import com.plantCare.plantcare.ui.screens.HomeScreen
import com.plantCare.plantcare.ui.screens.plantScreen.PlantScreen
import com.plantCare.plantcare.ui.screens.listScreen.ListScreen
import com.plantCare.plantcare.ui.screens.SearchScreen
import com.plantCare.plantcare.ui.screens.settingsScreen.SettingsScreen
import com.plantCare.plantcare.ui.screens.galleryScreen.GalleryScreen
import com.plantCare.plantcare.ui.screens.noteScreen.NoteScreen
import com.plantCare.plantcare.ui.screens.plantEditScreen.PlantEditScreen

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

val NavigationController = staticCompositionLocalOf<NavHostController?> { null }

@Composable
fun AppNavHost(
    navController: NavHostController,
    plantVM: PlantViewModel
) {
    NavHost(
        navController,
        startDestination = "main",
    ) {

        navigation(startDestination = Route.HOME.route, route="main") {
            composable(Route.HOME.route) { HomeScreen(plantVM) }
            composable(Route.SETTINGS.route) { SettingsScreen() }
        }

        navigation(startDestination = Route.PLANT_LIST.route, route = "list_route") {
            composable(Route.PLANT_LIST.route) { ListScreen() }
            composable(Route.PLANT.route) { PlantScreen() }
            composable(Route.GALLERY.route) { GalleryScreen() }
            composable(Route.PLANT_EDIT.route) { PlantEditScreen() }
            composable(Route.NOTE.route) { NoteScreen() }
            composable(Route.CAMERA.route) { }
        }


        composable(Route.CALENDAR.route) { CalendarScreen() }
        composable(Route.SEARCH.route) { SearchScreen() }
    }

}