package com.plantCare.plantcare.ui.screens.calendarScreen

import androidx.compose.runtime.Composable

@Composable
fun CalendarScreen() {
    CalendarScaffold { contentPadding ->
        Calendar(contentPadding)
    }
}