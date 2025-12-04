package com.plantCare.plantcare.ui.screens.noteEditScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.ui.components.TopBar

@Composable
fun NoteEditScaffold(onSave: ()->Unit, content: Content) {
    Scaffold(
        topBar = { TopBar(text = Route.NOTE_EDIT.label) {
            IconButton(
                onClick = {onSave()}
            ) {
                Icon(Icons.Filled.Save, "save")
            }
        } },

    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}