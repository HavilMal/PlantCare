package com.plantCare.plantcare.ui.screens.noteScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun NoteScreen() {
    val note = rememberTextFieldState()
    NoteScaffold { modifier ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxSize(),
                state = note,
            )
        }

    }
}