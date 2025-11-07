package com.plantCare.plantcare.model

import com.plantCare.plantcare.model.PlantRepository

import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

class PlantViewModel(private val repository: PlantRepository) : ViewModel() {

    fun insertPlant(plant: Plant) = viewModelScope.launch {
        repository.insertPlant(plant)
    }

    fun insertPlant(name: String, species: String, plantedOn: Date = Date(), wateringSchedule: WateringSchedule = WateringSchedule.MONTHLY
    ) = viewModelScope.launch {
        repository.insertPlant(name, species, plantedOn, wateringSchedule)
    }

    fun deletePlant(plant: Plant) = viewModelScope.launch {
        repository.deletePlant(plant)
    }
    fun deleteAllPlants() = viewModelScope.launch {
        repository.deleteAllPlants()
    }

    fun getAllPlants() : Flow<List<Plant>> {
        return repository.plantDao.getPlants()
    }
    fun getPlantDirPath(plantId: Long): String {
        return repository.getPlantsDirPath(plantId)
    }

}


class PlantViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}