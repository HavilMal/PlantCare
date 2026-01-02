package com.plantCare.plantcare.ui.screens.notesList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.common.addQuery
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.ui.theme.spacing
import com.plantCare.plantcare.viewModel.EditMode

@Composable
fun NoteCard(onDelete: () -> Unit, note: Note) {
    val navController = NavigationController.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.medium)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(note.title)
                Row {
                    IconButton(
                        onClick = {
                            navController?.navigate(
                                Route.NOTE_EDIT.routeWithArgs(EditMode.EDIT, note.plant).addQuery("noteId", note.id)
                            )
                        }
                    ) {
                        Icon(Icons.Filled.Edit, "Edit")
                    }
                    IconButton(
                        onClick = { onDelete() }
                    ) {
                        Icon(Icons.Filled.Delete, "Delete")
                    }
                }
            }


            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.medium)
                    .padding(bottom = MaterialTheme.spacing.medium),
                text = note.note,
            )
        }
    }
}
