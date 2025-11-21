package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.WateringSchedule
import com.plantCare.plantcare.ui.screens.plantEditScreen.WateringInterval
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// todo in database there is no way to store it


enum class EditMode(
) {
    EDIT,
    ADD,
}

data class PlantEditUiState(
    val mode: EditMode,
    val plantName: String = "",
    val species: String = "",
    val plantedOn: LocalDate = LocalDate.now(),
    val isIndoor: Boolean = false,
    val sensorName: String = "",
    val interval: WateringInterval = WateringInterval.WEEKLY,
    )

@HiltViewModel
class PlantEditViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val mode: EditMode = savedStateHandle["mode"]!!
    val id: Long = savedStateHandle["id"]!!
    private val plantEditFlow = MutableStateFlow(PlantEditUiState(mode = mode))
    val plantEditState = plantEditFlow.asStateFlow()

    init {
        viewModelScope.launch {
            if (mode == EditMode.EDIT) {
                val plantEntry = plantRepository.getPlant(id)
                plantEditFlow.update {
                    it.copy(
                        plantName = plantEntry.name,
                        species = plantEntry.species,
                        plantedOn = plantEntry.plantedOn,
                    )
                }
            }
        }
    }

    fun setIsIndoor(value: Boolean) {
        viewModelScope.launch {
            plantEditFlow.update {
                it.copy(isIndoor = value)
            }
        }
    }

    fun setSchedule(schedule: WateringInterval) {
        viewModelScope.launch {
            plantEditFlow.update {
                it.copy(interval = schedule)
            }
        }
    }

    fun setPlantName(name: String) {
        viewModelScope.launch {
            plantEditFlow.update {
                it.copy(plantName = name)
            }
        }
    }

    fun setPlantedOn(date: LocalDate) {
        viewModelScope.launch {
            plantEditFlow.update {
                it.copy(plantedOn = date)
            }
        }
    }

    fun setSensorName(name: String) {
        viewModelScope.launch {
            plantEditFlow.update {
                it.copy(sensorName = name)
            }
        }
    }

    fun setSpecties(species: String) {
        viewModelScope.launch {
            plantEditFlow.update {
                it.copy(species = species)
            }
        }
    }

    fun savePlant() {
        viewModelScope.launch {
            when (mode) {
                EditMode.ADD -> {
                    plantRepository.insertPlant(
                        name = plantEditState.value.plantName,
                        description = "todo",
                        species = plantEditState.value.species,
                        plantedOn = plantEditState.value.plantedOn,
                        wateringSchedule = when (plantEditState.value.interval) {
                            WateringInterval.WEEKLY -> WateringSchedule.WEEKLY
                            WateringInterval.MONTHLY -> WateringSchedule.MONTHLY
                        }
                    )
                }

                EditMode.EDIT -> {}

            }
        }
    }
}