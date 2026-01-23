package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.NotesRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteListUiState(
    val isLoading: Boolean = true,
    val notes: List<Note>,
)

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val notesRepository: NotesRepositoryImpl,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val plantId: Long = savedStateHandle["plantId"]!!
    private val noteListFlow = MutableStateFlow(NoteListUiState(notes = listOf()))
    val noteListState = noteListFlow.asStateFlow()

    init {
        viewModelScope.launch {
            notesRepository.getPlantNotesFlow(plantId).collect { notes ->
                noteListFlow.update {
                    it.copy(
                        notes = notes,
                        isLoading = false,
                    )
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