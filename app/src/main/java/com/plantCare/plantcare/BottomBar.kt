package com.plantCare.plantcare

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BottomBar() {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(Icons.Filled.Home, "Home")
            },
            label = {Text("Home")}
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(Icons.Filled.Star, "Plants")
            },
            label = {Text("Plants")}
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(Icons.Filled.DateRange, "Calendar")
            },
            label = {Text("Calendar")}
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(Icons.Filled.Search, "Search")
            },
            label = {Text("Search")}
        )
    }

}
