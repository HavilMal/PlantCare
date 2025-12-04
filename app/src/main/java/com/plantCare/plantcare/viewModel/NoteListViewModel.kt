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

data class NoteListUiState(
    val notes: List<Note>,
)

@HiltViewModel
class NoteListViewModel @Inject constructor(
    val notesRepository: NotesRepository,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val plantId: Long = savedStateHandle["plantId"]!!
    private val noteListFlow = MutableStateFlow(NoteListUiState(listOf()))
    val noteListState = noteListFlow.asStateFlow()

    init {
        viewModelScope.launch {
            notesRepository.getPlantNotesFlow(plantId).collect { notes ->
                noteListFlow.update {
                    it.copy(notes = notes)
                }
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            notesRepository.deleteNote(note.id)
        }
    }
}