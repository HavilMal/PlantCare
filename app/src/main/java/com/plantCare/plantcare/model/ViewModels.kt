package com.plantCare.plantcare.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class PlantUiState(
    val plants: List<Plant> = emptyList()
)

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val repository: PlantRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PlantUiState())
    val uiState: StateFlow<PlantUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.plantDao.getPlants()
                .collect { plantList ->
                    _uiState.update { currentState ->
                        currentState.copy(plants = plantList)
                    }
                }
        }
    }

    fun insertPlant(plant: Plant) {
        viewModelScope.launch {
            repository.insertPlant(plant)
        }
    }

    fun insertPlant(
        name: String,
        description: String,
        species: String,
        plantedOn: Date = Date(),
        wateringSchedule: WateringSchedule = WateringSchedule.MONTHLY
    ) {
        viewModelScope.launch {
            repository.insertPlant(name, description,species, plantedOn, wateringSchedule)
        }
    }

    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            repository.deletePlant(plant)
        }
    }

    fun deleteAllPlants() {
        viewModelScope.launch {
            repository.deleteAllPlants()
        }
    }

    fun getPlantDirPath(plantId: Long): String {
        return repository.getPlantsDirPath(plantId)
    }
}