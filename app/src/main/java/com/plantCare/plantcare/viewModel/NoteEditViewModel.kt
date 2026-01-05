package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteEditUiState(
    val title: String,
    val content: String,
)

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    val notesRepository: NotesRepository,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val mode: EditMode = savedStateHandle["mode"]!!

    val plantId: Long = savedStateHandle["plantId"]!!
    val noteId: Long = savedStateHandle["noteId"]!!

    private val noteEditFlow = MutableStateFlow(NoteEditUiState("", ""))
    val noteEditState = noteEditFlow.asStateFlow()

    init {
        if (mode == EditMode.EDIT) {
            viewModelScope.launch {
                notesRepository.getPlantNoteFlow(noteId).collect { note ->
                    noteEditFlow.update {
                        it.copy(title = note.title, content = note.note)
                    }
                }
            }
        }
    }

    fun saveNote() {
        when (mode) {
            EditMode.EDIT -> {
                viewModelScope.launch {
                    notesRepository.editNoteContent(
                        noteId,
                        plantId,
                        noteEditState.value.title,
                        noteEditState.value.content
                    )
                }
            }

            EditMode.ADD -> {
                viewModelScope.launch {
                    notesRepository.createNote(
                        plantId,
                        noteEditState.value.title,
                        noteEditState.value.content,
                    )
                }
            }
        }
    }

    fun setNoteTitle(title: String) {
        noteEditFlow.update {
            it.copy(title = title)
        }
    }

    fun setNoteContent(content: String) {
        noteEditFlow.update {
            it.copy(content = content)
        }
    }
}