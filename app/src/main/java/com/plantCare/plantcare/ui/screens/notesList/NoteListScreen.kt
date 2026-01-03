package com.plantCare.plantcare.ui.screens.notesList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.ui.components.InlinedText
import com.plantCare.plantcare.ui.theme.spacing
import com.plantCare.plantcare.viewModel.NoteListUiState
import com.plantCare.plantcare.viewModel.NoteListViewModel

@Preview
@Composable
fun NoteListScreen(
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val state = viewModel.noteListState.collectAsState()

    NoteListScaffold { modifier ->
        NoteList(state.value, viewModel, modifier)
    }
}


@Composable
fun NoteList(state: NoteListUiState, viewModel: NoteListViewModel, modifier: Modifier) {
    val isEmpty = state.notes.isEmpty()
    val isLoading = state.isLoading
    if (!isLoading && isEmpty) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.size(200.dp),
                imageVector = Icons.AutoMirrored.Default.NoteAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
            )
            Text(
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.outlineVariant,
                text = "You don't have any plants.",
            )
            InlinedText(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outlineVariant,
                annotatedText = buildAnnotatedString {
                    append("To add a plant press ")
                    appendInlineContent("add_icon")
                    append(" button.")
                },
                annotationDictionary = mapOf("add_icon" to { tint ->
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = tint,
                    )
                }),
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.padding(MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        items(state.notes) { note ->
            NoteCard(
                onDelete = { viewModel.deleteNote(note) },
                note = note,
            )
        }
    }

}