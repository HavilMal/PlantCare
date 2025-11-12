package com.plantCare.plantcare.model

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.plantCare.plantcare.model.PlantRepository
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plantCare.plantcare.localAppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class PlantUiState(
    val plants: List<Plant> = emptyList()
)

class PlantViewModel(
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

class PlantViewModelFactory(
    private val repository: PlantRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


@Composable
inline fun <reified VM : ViewModel> appViewModel(crossinline factoryProvider: (AppRepository) -> VM): VM {
    val repo = localAppRepository.current
    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factoryProvider(repo) as T
            }
        }
    }
    return viewModel(factory = factory)
}


@Composable
fun plantViewModel(): PlantViewModel {
    val repo = localAppRepository.current
    val application = LocalContext.current.applicationContext as Application
    val factory = remember { PlantViewModelFactory(repo.plantRepository, application) }
    return viewModel(factory = factory)
}