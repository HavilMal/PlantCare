package com.plantCare.plantcare.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.WateringInterval
import com.plantCare.plantcare.service.PlantApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

enum class EditMode(
) {
    EDIT,
    ADD,
}

data class PlantEditUiState(
    val mode: EditMode,
    val isLoading: Boolean = true,
    val loadingError: Boolean = false,
    val plantName: String = "",
    val species: String = "",
    val plantedOn: LocalDate = LocalDate.now(),
    val isIndoor: Boolean = false,
    val sensorName: String = "",
    val interval: WateringInterval = WateringInterval.WEEK,
    val selectedDays: Set<DayOfWeek> = setOf(),
)

@HiltViewModel
class PlantEditViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val plantApiRepository: PlantApiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val mode: EditMode = savedStateHandle["mode"]!!
    val plantId: Long = savedStateHandle["plantId"]!!
    private val plantEditFlow = MutableStateFlow(PlantEditUiState(mode = mode))
    val plantEditState = plantEditFlow.asStateFlow()

    init {
        if (mode == EditMode.EDIT) {
            val combinedFlow = plantRepository.getPlant(plantId)
                .combine(plantRepository.getSchedule(plantId)) { plant, schedule ->
                    Pair(plant, schedule)
                }

            combinedFlow
                .onEach { (plant, schedule) ->
                    if (plant != null) {
                        plantEditFlow.update { it ->
                            it.copy(
                                plantName = plant.name,
                                species = plant.species,
                                plantedOn = plant.plantedOn,
                                selectedDays = schedule.map { it.day }.toSet(),
                                interval = plant.wateringInterval,
                                isLoading = false
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        } else {
            viewModelScope.launch {
                plantEditFlow.update { it.copy(isLoading = false) }
            }
        }
    }

    fun setIsIndoor(value: Boolean) {
        plantEditFlow.update {
            it.copy(isIndoor = value)
        }
    }

    fun setSchedule(schedule: WateringInterval) {
        plantEditFlow.update {
            it.copy(interval = schedule)
        }
    }

    fun setPlantName(name: String) {
        plantEditFlow.update {
            it.copy(plantName = name)
        }
    }

    fun setPlantedOn(date: LocalDate) {
        plantEditFlow.update {
            it.copy(plantedOn = date)
        }
    }

    fun setSensorName(name: String) {
        plantEditFlow.update {
            it.copy(sensorName = name)
        }
    }

    fun setSpecties(species: String) {
        plantEditFlow.update {
            it.copy(species = species)
        }
    }

    fun setInterval(interval: WateringInterval) {
        plantEditFlow.update {
            it.copy(interval = interval)
        }
    }

    fun selectDay(day: DayOfWeek) {
        setSelectedDays(plantEditState.value.selectedDays + day)
    }

    fun unselectDay(day: DayOfWeek) {
        setSelectedDays(plantEditState.value.selectedDays - day)
    }


    fun savePlant() {
        if (!plantEditState.value.isLoading) {
            when (mode) {
                EditMode.ADD -> {
                    viewModelScope.launch {
                        plantRepository.insertPlant(
                            name = plantEditState.value.plantName,
                            description = "todo",
                            species = plantEditState.value.species,
                            plantedOn = plantEditState.value.plantedOn,
                            wateringInterval = plantEditState.value.interval,
                        )
                    }

                }

                EditMode.EDIT -> {
                    viewModelScope.launch {
                        plantRepository.updatePlant(
                            plantId,
                            plantEditState.value.plantName,
                            isIndoor = plantEditState.value.isIndoor,
                            species = plantEditState.value.species,
                            plantedOn = plantEditState.value.plantedOn,
                            wateringInterval = plantEditState.value.interval,
                        )
                    }
                }
            }

            viewModelScope.launch {
                plantRepository.setSchedule(
                    plantId,
                    plantEditState.value.selectedDays,
                    plantEditState.value.interval
                )
            }
        }
    }

    fun searchSpecies() {
        viewModelScope.launch {
            val results = plantApiRepository.findPlant(plantEditState.value.species)
            Log.d("searchSpecies", results.toString())
        }
    }

    private fun setSelectedDays(days: Set<DayOfWeek>) {
        plantEditFlow.update {
            it.copy(selectedDays = days)
        }
    }
}