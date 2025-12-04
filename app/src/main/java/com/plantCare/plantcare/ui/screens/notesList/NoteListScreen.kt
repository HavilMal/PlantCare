package com.plantCare.plantcare.ui.screens.notesList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.ui.theme.spacing
import com.plantCare.plantcare.viewModel.NoteListViewModel

@Preview
@Composable
fun NoteListScreen(
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val state = viewModel.noteListState.collectAsState()

    NoteListScaffold { modifier ->
        NoteList(state.value.notes, viewModel, modifier)
    }
}

@Composable
fun NoteList(notes: List<Note>, viewModel: NoteListViewModel, modifier: Modifier) {
    if (!notes.isEmpty()) {
        LazyColumn(
            modifier = modifier.padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            items(notes) { note ->
                NoteCard(
                    onDelete = { viewModel.deleteNote(note) },
                    note = note,
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("No notes")
        }
    }
}