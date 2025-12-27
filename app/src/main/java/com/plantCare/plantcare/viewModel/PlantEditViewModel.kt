package com.plantCare.plantcare.viewModel

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.common.capitalize
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.WateringInterval
import com.plantCare.plantcare.service.PlantDetailsRepository
import com.plantCare.plantcare.service.PlantSearchResult
import com.plantCare.plantcare.service.SensorService
import com.plantCare.plantcare.ui.screens.plantEditScreen.SensorButtonState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

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
    val interval: WateringInterval = WateringInterval.WEEK,
    val selectedDays: Set<DayOfWeek> = setOf(),
    val showSearchResults: Boolean = false,
    val isSearching: Boolean = false,
    val searchResults: List<PlantSearchResult> = listOf(),
    val selectedPlant: PlantSearchResult? = null,
    val sensorButtonState: SensorButtonState = SensorButtonState.ADD_SENSOR,
)

@HiltViewModel
class PlantEditViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val plantDetailsRepository: PlantDetailsRepository,
    private val sensorService: SensorService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val mode: EditMode = savedStateHandle["mode"]!!
    val plantId: Long = savedStateHandle["plantId"]!!
    private val plantEditFlow = MutableStateFlow(PlantEditUiState(mode = mode))
    val plantEditState = plantEditFlow.asStateFlow()

    private val toastMessageFlow = MutableSharedFlow<String>()
    val toastMessage = toastMessageFlow.asSharedFlow()

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

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun scanForSensors() {
        plantEditFlow.update {
            it.copy(sensorButtonState = SensorButtonState.SCANNING)
        }

        viewModelScope.launch {
            val device = sensorService.scanForSensor().first()
            if (device == null) {
                plantEditFlow.update {
                    it.copy(sensorButtonState = SensorButtonState.ADD_SENSOR)
                }
            } else {
                plantEditFlow.update {
                    it.copy(sensorButtonState = SensorButtonState.REMOVE_SENSOR)
                }
            }
        }
    }

    fun removeSensor() {
        plantEditFlow.update {
            it.copy(sensorButtonState = SensorButtonState.ADD_SENSOR)
        }
    }

    fun setSpeciesName(species: String) {
        plantEditFlow.update {
            it.copy(species = species)
        }
    }

    fun setSelectedPlant(plant: PlantSearchResult, locale: Locale) {
        plantEditFlow.update {
            it.copy(selectedPlant = plant, species = plant.commonName.capitalize(locale))
        }
    }

    fun setInterval(interval: WateringInterval) {
        plantEditFlow.update {
            it.copy(interval = interval)
        }
    }

    fun setShowResults(expanded: Boolean) {
        plantEditFlow.update {
            it.copy(showSearchResults = expanded)
        }
    }

    fun selectDay(day: DayOfWeek) {
        setSelectedDays(plantEditState.value.selectedDays + day)
    }


    fun unselectDay(day: DayOfWeek) {
        setSelectedDays(plantEditState.value.selectedDays - day)
    }

    fun setIsSearching(isSearching: Boolean) {
        plantEditFlow.update {
            it.copy(isSearching = isSearching)
        }
    }


    fun savePlant() {
        if (!plantEditState.value.isLoading) {
            when (mode) {
                EditMode.ADD -> {
                    viewModelScope.launch {

                        val id = plantRepository.insertPlant(
                            name = plantEditState.value.plantName,
                            description = "todo",
                            species = plantEditState.value.species,
                            plantedOn = plantEditState.value.plantedOn,
                            wateringInterval = plantEditState.value.interval,
                            apiId = plantEditState.value.selectedPlant?.id
                        )

                        plantRepository.setSchedule(
                            id,
                            plantEditState.value.selectedDays,
                            plantEditState.value.interval
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
                            apiId = plantEditState.value.selectedPlant?.id
                        )

                        plantRepository.setSchedule(
                            plantId,
                            plantEditState.value.selectedDays,
                            plantEditState.value.interval
                        )
                    }
                }
            }
        }
    }

    fun searchSpecies() {
        setIsSearching(true)
        viewModelScope.launch {
            val results = plantDetailsRepository.findPlant(plantEditState.value.species)
            if (results != null) {
                plantEditFlow.update {
                    it.copy(
                        showSearchResults = true,
                        isSearching = false,
                        searchResults = results,
                    )
                }
            } else {
                setIsSearching(false)
                toastMessageFlow.emit("Connection error.")
            }
        }
    }

    private fun setSelectedDays(days: Set<DayOfWeek>) {
        plantEditFlow.update {
            it.copy(selectedDays = days)
        }
    }
}