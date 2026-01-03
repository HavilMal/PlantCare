package com.plantCare.plantcare.ui.screens.noteEditScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.viewModel.NoteEditViewModel

@Composable
fun NoteEditScreen(
    viewModel: NoteEditViewModel = hiltViewModel()
) {
    val state = viewModel.noteEditState.collectAsState()
    val navController = NavigationController.current
    NoteEditScaffold(
        onSave = {
            if (viewModel.saveNote()) {
                navController?.popBackStack()
            }
        }
    ) { modifier ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") },
                value = state.value.title,
                onValueChange = viewModel::setNoteTitle,
                singleLine = true,
                isError = !state.value.titleError.isEmpty(),
                supportingText = {
                    if (!state.value.titleError.isEmpty()) {
                        Text(state.value.titleError)
                    }
                }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxSize(),
                label = {},
                value = state.value.content,
                onValueChange = viewModel::setNoteContent,
                isError = !state.value.contentError.isEmpty(),
                supportingText = {
                    if (!state.value.contentError.isEmpty()) {
                        Text(state.value.contentError)
                    }
                }
            )
        }

    }
}