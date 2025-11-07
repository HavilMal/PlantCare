package com.plantCare.plantcare.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.plantCare.plantcare.common.Route

@Composable
fun CalendarScreen() {
    MainScaffold(Route.CALENDAR.label) {
        Text("calendar screen")
    }
}