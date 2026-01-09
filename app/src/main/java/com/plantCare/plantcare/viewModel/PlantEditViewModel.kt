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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    val nameError: String = "",
    val species: String = "",
    val speciesError: String = "",
    val description: String = "",
    val plantedOn: LocalDate = LocalDate.now(),
    val isIndoor: Boolean = false,
    val interval: WateringInterval = WateringInterval.WEEK,
    val selectedDays: Set<DayOfWeek> = setOf(),
    val showSearchResults: Boolean = false,
    val isSearching: Boolean = false,
    val searchResults: List<PlantSearchResult> = listOf(),
    val selectedPlant: PlantSearchResult? = null,
    val sensorButtonState: SensorButtonState = SensorButtonState.ADD_SENSOR,
    val sensorAddress: String? = null,
    val bluetoothOn: Boolean = false,
    val scanError: Boolean = false,
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
            viewModelScope.launch {
                val combinedFlow = plantRepository.getPlant(plantId)
                    .combine(plantRepository.getSchedule(plantId)) { plant, schedule ->
                        Pair(plant, schedule)
                    }

                combinedFlow
                    .first()
                    .let { (plant, schedule) ->
                        if (plant != null) {
                            plantEditFlow.update { it ->
                                it.copy(
                                    plantName = plant.name,
                                    species = plant.species,
                                    description = plant.description,
                                    plantedOn = plant.plantedOn,
                                    selectedDays = schedule.map { it.day }.toSet(),
                                    interval = plant.wateringInterval,
                                    isLoading = false,
                                    sensorAddress = plant.sensorAddress,
                                    sensorButtonState = if (plant.sensorAddress == null) {
                                        SensorButtonState.ADD_SENSOR
                                    } else {
                                        SensorButtonState.REMOVE_SENSOR
                                    }
                                )
                            }
                        }
                    }
            }
        } else {
            viewModelScope.launch {
                plantEditFlow.update { it.copy(isLoading = false) }
            }
        }

        viewModelScope.launch {
            sensorService.getBluetoothStateFlow().collect { state ->
                plantEditFlow.update {
                    it.copy(bluetoothOn = state)
                }
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

    fun setDescription(description: String) {
        plantEditFlow.update {
            it.copy(description = description)
        }
    }

    fun setPlantName(name: String) {
        plantEditFlow.update {
            it.copy(plantName = name)
        }
        validateName()
    }

    fun setPlantedOn(date: LocalDate) {
        plantEditFlow.update {
            it.copy(plantedOn = date)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun scanForSensors() {
        plantEditFlow.update {
            it.copy(
                sensorButtonState = SensorButtonState.SCANNING,
                scanError = false,
            )
        }

        viewModelScope.launch {
            val device = sensorService.scanForSensor().firstOrNull()
            if (device == null) {
                plantEditFlow.update {
                    it.copy(
                        sensorButtonState = SensorButtonState.ADD_SENSOR,
                        scanError = true,
                    )
                }
            } else {
                plantEditFlow.update {
                    it.copy(
                        sensorButtonState = SensorButtonState.REMOVE_SENSOR,
                        sensorAddress = device.address,
                        scanError = false,
                    )
                }
            }
        }
    }

    fun removeSensor() {
        plantEditFlow.update {
            it.copy(
                sensorButtonState = SensorButtonState.ADD_SENSOR,
                sensorAddress = null,
            )
        }
    }

    fun setSpeciesName(species: String) {
        plantEditFlow.update {
            it.copy(species = species)
        }
        validateSpecies()
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


    fun savePlant(): Boolean {
        if (plantEditState.value.isLoading) {
            return false
        }

        val conditions = listOf(
            !validateName(),
            !validateSpecies(),
        )

        if (conditions.any { it }) {
            return false
        }

        when (mode) {
            EditMode.ADD -> {
                viewModelScope.launch {

                    val id = plantRepository.insertPlant(
                        name = plantEditState.value.plantName,
                        description = plantEditState.value.description,
                        species = plantEditState.value.species,
                        plantedOn = plantEditState.value.plantedOn,
                        wateringInterval = plantEditState.value.interval,
                        apiId = plantEditState.value.selectedPlant?.id
                    )

                    plantRepository.setSensorAddress(id, plantEditState.value.sensorAddress)

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
                        id = plantId,
                        name = plantEditState.value.plantName,
                        description = plantEditState.value.description,
                        species = plantEditState.value.species,
                        plantedOn = plantEditState.value.plantedOn,
                        isIndoor = plantEditState.value.isIndoor,
                        wateringInterval = plantEditState.value.interval,
                    )

                    plantRepository.setApiId(plantId, plantEditState.value.selectedPlant?.id)
                    plantRepository.setSensorAddress(plantId, plantEditState.value.sensorAddress)

                    plantRepository.setSchedule(
                        plantId,
                        plantEditState.value.selectedDays,
                        plantEditState.value.interval
                    )
                }
            }
        }

        return true
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

    private fun validateName(): Boolean {
        if (plantEditState.value.plantName.isEmpty()) {
            plantEditFlow.update {
                it.copy(nameError = "Plant name can't be empty.")
            }
            return false
        } else {
            plantEditFlow.update {
                it.copy(nameError = "")
            }
            return true
        }
    }

    private fun validateSpecies(): Boolean {
        if (plantEditState.value.species.isEmpty()) {
            plantEditFlow.update {
                it.copy(speciesError = "Plant species can't be empty.")
            }
            return false
        } else {
            plantEditFlow.update {
                it.copy(speciesError = "")
            }
            return true
        }
    }

    private fun setSelectedDays(days: Set<DayOfWeek>) {
        plantEditFlow.update {
            it.copy(selectedDays = days)
        }
    }
}