package com.plantCare.plantcare.common

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.plantCare.plantcare.ui.screens.calendarScreen.CalendarScreen
import com.plantCare.plantcare.ui.screens.homeScreen.HomeScreen
import com.plantCare.plantcare.ui.screens.SearchScreen
import com.plantCare.plantcare.ui.screens.galleryScreen.GalleryScreen
import com.plantCare.plantcare.ui.screens.listScreen.ListScreen
import com.plantCare.plantcare.ui.screens.noteEditScreen.NoteEditScreen
import com.plantCare.plantcare.ui.screens.notesList.NoteListScreen
import com.plantCare.plantcare.ui.screens.plantScreen.PlantCameraCaptureScreen
import com.plantCare.plantcare.ui.screens.plantEditScreen.PlantEditScreen
import com.plantCare.plantcare.ui.screens.plantScreen.PlantScreen
import com.plantCare.plantcare.ui.screens.settingsScreen.SettingsScreen
import com.plantCare.plantcare.viewModel.EditMode

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

    NOTE_EDIT("note_edit", "Note"),
    NOTE_LIST("note_list", "Note"),
    GALLERY("gallery", "Gallery"),
    CAMERA("camera", "Camera");


    fun routeWithArgs(vararg args: Any?): String {
        return buildString {
            append(route)
            args.forEach { arg -> append("/$arg") }
        }
    }

    fun routeWithArgNames(vararg names: String): String {
        return buildString {
            append(route)
            names.forEach { name -> append("/{$name}") }
        }
    }
}

// DO NOT CHAIN
fun String.addQuery(argument: String, value: Any): String {
    return this + "?${argument}=$value"
}

fun String.chainQuerry(argument: String, value: Any): String {
    return this + "&${argument}=$value"
}

val NavigationController = staticCompositionLocalOf<NavHostController?> { null }

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        navController,
        startDestination = "main",
    ) {

        navigation(startDestination = Route.HOME.route, route = "main") {
            composable(Route.HOME.route) { HomeScreen() }
            composable(Route.SETTINGS.route) { SettingsScreen() }
        }

        navigation(startDestination = Route.PLANT_LIST.route, route = "list_route") {
            composable(Route.PLANT_LIST.route) { ListScreen() }

            composable(
                route = Route.PLANT.routeWithArgNames("plantId"),
                arguments = listOf(
                    navArgument("plantId") { type = NavType.LongType }
                )
            ) {
                PlantScreen()
            }
            composable(
                route = Route.GALLERY.routeWithArgNames("plantId"),
                arguments = listOf(
                    navArgument("plantId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                GalleryScreen()
            }

            composable(
                route = Route.PLANT_EDIT.routeWithArgNames("mode").addQuery("plantId", "{plantId}").chainQuerry("noteId", "{noteId}"),
                arguments = listOf(
                    navArgument("mode") { type = NavType.EnumType(EditMode::class.java) },
                    navArgument("plantId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                )
            ) { PlantEditScreen() }
            composable(
                route = Route.NOTE_LIST.routeWithArgNames("plantId"),
                arguments = listOf(
                    navArgument("plantId") {
                        type = NavType.LongType
                    }
                )
            ) { NoteListScreen() }
            composable(
                route = Route.NOTE_EDIT.routeWithArgNames("mode", "plantId").addQuery("noteId", "{noteId}"),
                arguments = listOf(
                    navArgument("mode") {
                        type = NavType.EnumType(EditMode::class.java)
                    },
                    navArgument("plantId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                    navArgument("noteId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                )
            ) { NoteEditScreen() }

            composable(
                route = Route.CAMERA.routeWithArgNames("plantId"),
                arguments = listOf(
                    navArgument("plantId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                backStackEntry ->
                PlantCameraCaptureScreen()
            }
        }

        composable(Route.CALENDAR.route) { CalendarScreen() }
        composable(Route.SEARCH.route) { SearchScreen() }
    }

}