package com.plantCare.plantcare.ui.screens.noteScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.ui.components.TopBar

@Composable
fun NoteScaffold(content: Content) {
    Scaffold(
        topBar = { TopBar(text = Route.NOTE.label) },
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}