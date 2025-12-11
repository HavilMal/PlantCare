package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.NotesRepository
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


data class PlantScreenUiState(
    val images: List<File> = emptyList(),
    val notes: List<Note> = emptyList(),
    val plant: Plant? = null,
)

@HiltViewModel
class PlantScreenViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val notesRepository: NotesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val plantId: Long = checkNotNull(savedStateHandle.get<Long>("plantId"))

    private val stateFlow = MutableStateFlow(PlantScreenUiState())
    val uiState: StateFlow<PlantScreenUiState> = stateFlow

    init {
        viewModelScope.launch {
            plantRepository.getPlant(plantId).collect { plant ->
                stateFlow.update{
                    it.copy(plant = plant)
                }
            }
        }

        viewModelScope.launch {
            val photos = plantRepository.getPlantPhotos(plantId)
            stateFlow.update {
                it.copy(images = photos)
            }
        }

        viewModelScope.launch {
            notesRepository.getPlantNotesFlow(plantId).collect { notes ->
                stateFlow.update {
                    it.copy(notes = notes)
                }
            }
        }

        viewModelScope.launch {


        }
    }
}