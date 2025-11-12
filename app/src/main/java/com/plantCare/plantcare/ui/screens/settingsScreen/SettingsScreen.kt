package com.plantCare.plantcare.ui.screens.settingsScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun SettingsScreen() {
    data class Setting(
        val name: String,
        val description: String,
    )

    val settings = listOf(
        Setting("Location", "Change your location"),
        Setting("Date", "Change your date format"),
        Setting("Unit", "Set your units"),
    )


    SettingsScaffold { modifier ->
        LazyColumn(
            modifier = modifier,
        ) {
            settings.forEach { it  ->
                item {
                    SettingsItem(it.name, it.description)
                }
             }
        }
    }
}