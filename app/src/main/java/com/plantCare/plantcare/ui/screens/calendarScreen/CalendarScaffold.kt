package com.plantCare.plantcare.ui.screens.calendarScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.ui.components.BottomBar
import com.plantCare.plantcare.ui.components.TopBar

@Composable
fun CalendarScaffold(content: Content) {
    Scaffold(
        topBar = {
            TopBar( text = "Calendar" )
        },
        bottomBar = { BottomBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {}
            ) {
                Icon(Icons.Default.ArrowUpward, "Up arrow")
            }
        }
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}
