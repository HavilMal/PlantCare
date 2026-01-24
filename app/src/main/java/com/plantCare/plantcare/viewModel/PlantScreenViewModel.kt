package com.plantCare.plantcare.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.NotesRepositoryImpl
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantDetails
import com.plantCare.plantcare.database.PlantRepositoryImpl
import com.plantCare.plantcare.service.PlantDetailsRepositoryImpl
import com.plantCare.plantcare.service.SensorData
import com.plantCare.plantcare.service.SensorServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed class SortableCard(
    val id: Int,
    private val defaultPriority: Int,
    isAvailable: Boolean = false,
    var priority: Int = defaultPriority,
) {
    init {
        priority = if (isAvailable) {
            defaultPriority
        } else {
            -1
        }
    }

    class SensorCard(hasSensor: Boolean) :
        SortableCard(id = 1, defaultPriority = 5, isAvailable = hasSensor)

    class NotesCard(hasNotes: Boolean) :
        SortableCard(id = 2, defaultPriority = 4, isAvailable = hasNotes)

    class TipsCard(hasTips: Boolean) :
        SortableCard(id = 3, defaultPriority = 3, isAvailable = hasTips)

    class DescriptionCard(hasDescription: Boolean) :
        SortableCard(id = 4, defaultPriority = 2, isAvailable = hasDescription)
}

data class PlantScreenUiState(
    val media: List<File> = emptyList(),
    val notes: List<Note> = emptyList(),
    val plant: Plant? = null,
    val plantDetails: PlantDetails? = null,
    val dialogOpen: Boolean = false,
    val bluetoothOn: Boolean = false,
    val sensorData: SensorData? = null,
    val cardOrder: List<SortableCard>,
)

@SuppressLint("MissingPermission")
@HiltViewModel
class PlantScreenViewModel @Inject constructor(
    private val plantRepository: PlantRepositoryImpl,
    private val notesRepository: NotesRepositoryImpl,
    private val detailsRepository: PlantDetailsRepositoryImpl,
    private val sensorService: SensorServiceImpl,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val plantId: Long = checkNotNull(savedStateHandle.get<Long>("plantId"))
    private val stateFlow = MutableStateFlow(PlantScreenUiState(cardOrder = getCardOrder()))
    val state: StateFlow<PlantScreenUiState> = stateFlow
    var sensorJob: Job? = null

    init {
        viewModelScope.launch {
            plantRepository.getPlant(plantId).collect { plant ->
                stateFlow.update {
                    it.copy(plant = plant)
                }
                updateOrder()
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
                updateOrder()
            }
        }

        viewModelScope.launch {
            detailsRepository.getPlantDetails(plantId).collect { details ->
                stateFlow.update {
                    it.copy(plantDetails = details)
                }
                updateOrder()
            }
        }

        viewModelScope.launch {
            sensorService.getBluetoothStateFlow().collect { state ->
                stateFlow.update {
                    it.copy(bluetoothOn = state)
                }
                updateOrder()
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
            state.value.plant?.let { plant ->
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
        val address = state.value.plant?.sensorAddress ?: return
        sensorJob?.cancel()
        sensorJob = viewModelScope.launch {
            sensorService.getSensorDataFlow(address).collect { data ->
                stateFlow.update {
                    it.copy(sensorData = data)
                }
            }
        }
    }

    private fun updateOrder() {
        stateFlow.update {
            it.copy(cardOrder = getCardOrder())
        }
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    private fun getCardOrder(): List<SortableCard> {
        val order: List<SortableCard> = mutableListOf(
            SortableCard.SensorCard(hasSensor = state?.value?.plant?.sensorAddress != null),
            SortableCard.NotesCard(hasNotes = state?.value?.notes?.isNotEmpty() ?: false),
            SortableCard.TipsCard(hasTips = state?.value?.plantDetails != null),
            SortableCard.DescriptionCard(
                hasDescription = state?.value?.plant?.description?.isNotEmpty() ?: false
            )
        )
        val sorted = order.sortedBy { it.priority }.reversed()
        return sorted
    }
}