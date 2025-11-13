package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

// todo in database there is no way to store it
sealed class WATERING_SCHEDULE {
    class WEEKLY(val onDays: List<DayOfWeek>): WATERING_SCHEDULE()
    class MONTHLY(val onDays: List<Int>): WATERING_SCHEDULE()
}


data class PlantEditUiState(
    val isNewPlant: Boolean = false,
    val plantName: String = "",
    val species: String = "",
    val plantedOn: LocalDate = LocalDate.now(),
    val isIndoor: Boolean = false,
    val sensorName: String = "",
    val scheduleType: WATERING_SCHEDULE = WATERING_SCHEDULE.MONTHLY(listOf()),
)

@HiltViewModel
class PlantEditViewModel @Inject constructor(
    private val repository: PlantRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    // todo is new plant
    private val plantEditFlow = MutableStateFlow(PlantEditUiState())

    val plantEditState = plantEditFlow.asStateFlow()

    fun setIsIndoor(value: Boolean) {
        viewModelScope.launch {
            plantEditFlow.update {
                it.copy(isIndoor = value)
            }
        }
    }

    fun setSchedule(schedule: WATERING_SCHEDULE) {
        viewModelScope.launch {
            plantEditFlow.update {
                it.copy(scheduleType = schedule)
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
            // todo
        }
    }
}