package com.plantCare.plantcare.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.NotesRepository
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantDetails
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.service.PlantDetailsRepository
import com.plantCare.plantcare.service.SensorData
import com.plantCare.plantcare.service.SensorService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    val plantDetails: PlantDetails? = null,
    val dialogOpen: Boolean = false,
    val bluetoothOn: Boolean = false,
    val sensorData: SensorData? = null,
)

@SuppressLint("MissingPermission")
@HiltViewModel
class PlantScreenViewModel @Inject constructor(
    val plantRepository: PlantRepository,
    private val notesRepository: NotesRepository,
    private val detailsRepository: PlantDetailsRepository,
    private val sensorService: SensorService,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val plantId: Long = checkNotNull(savedStateHandle.get<Long>("plantId"))
    private val stateFlow = MutableStateFlow(PlantScreenUiState())
    val uiState: StateFlow<PlantScreenUiState> = stateFlow
    var sensorJob: Job? = null

    init {
        viewModelScope.launch {
            plantRepository.getPlant(plantId).collect { plant ->
                stateFlow.update {
                    it.copy(plant = plant)
                }
            }
        }

        viewModelScope.launch {
            plantRepository.getPlantMediaFlow(plantId).collect { media ->
                stateFlow.update {
                    it.copy(media = media)
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
            detailsRepository.getPlantDetails(plantId).collect { details ->
                stateFlow.update {
                    it.copy(plantDetails = details)
                }
            }
        }

        viewModelScope.launch {
            sensorService.getBluetoothStateFlow().collect { state ->
                stateFlow.update {
                    it.copy(bluetoothOn = state)
                }
            }
        }
    }

    fun setDialogState(open: Boolean) {
        stateFlow.update {
            it.copy(dialogOpen = open)
        }
    }

    fun deleteCurrentPlant() {
        viewModelScope.launch {
            uiState.value.plant?.let { plant ->
                plantRepository.deletePlant(plant)
            }
        }
        viewModelScope.launch {
            plantRepository.getPlantMediaFlow(plantId).collect { media ->
                stateFlow.update {
                    it.copy(media = media)
                }
            }
        }
    }

    fun getSensorData() {
        val address = uiState.value.plant?.sensorAddress ?: return
        sensorJob?.cancel()
        sensorJob = viewModelScope.launch {
            sensorService.getSensorDataFlow(address).collect { data ->
                stateFlow.update {
                    it.copy(sensorData = data)
                }
            }
        }
    }

    suspend fun deletePlantMedia(file: File) {
        plantRepository.deletePlantMedia(file)
    }
}