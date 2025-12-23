package com.plantCare.plantcare.viewModel

import android.content.Context
import android.os.FileObserver
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.NotesRepository
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.utils.FileUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


data class PlantScreenUiState(
    val media: List<File> = emptyList(),
    val notes: List<Note> = emptyList(),
    val plant: Plant? = null,
)

@HiltViewModel
class PlantScreenViewModel @Inject constructor(
    val plantRepository: PlantRepository,
    private val notesRepository: NotesRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val plantId: Long = checkNotNull(savedStateHandle.get<Long>("plantId"))
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
            notesRepository.getPlantNotesFlow(plantId).collect { notes ->
                stateFlow.update {
                    it.copy(notes = notes)
                }
            }
        }
        viewModelScope.launch {
            plantRepository.getPlantMediaFlow(plantId).collect { media ->
                stateFlow.update{
                    it.copy(media = media)
                }
            }
        }
    }
    suspend fun deletePlantMedia(file: File){
        plantRepository.deletePlantMedia(file)
    }
}