package com.plantCare.plantcare

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController

@Composable
fun BottomBar(controller: NavHostController) {
    var current by rememberSaveable { mutableStateOf(Route.HOME) }
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        listOf(Route.HOME, Route.PLANT_LIST, Route.CALENDAR, Route.SEARCH).forEach { route ->
            NavigationBarItem(
                selected = current == route,
                onClick = {
                    controller.navigate(route.route)
                    current = route
                },
                icon = {
                    if (route.icon != null) {
                        Icon(route.icon, route.label)
                    } else {
                        Icon(Icons.Filled.Star, route.label)
                    }
                },
                label = { Text(route.label) }
            )
        }

    }

}
