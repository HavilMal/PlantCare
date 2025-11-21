package com.plantCare.plantcare.viewModel

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


data class PlantScreenUiState(
    val images: List<File> = emptyList(),
    val plant: Plant? = null,
)

@HiltViewModel
class PlantScreenViewModel @Inject constructor(
    private val repository: PlantRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val plantId: Long = checkNotNull(savedStateHandle.get<Long>("plantId"))

    private val stateFlow = MutableStateFlow<PlantScreenUiState>(PlantScreenUiState())
    val uiState: StateFlow<PlantScreenUiState> = stateFlow

    init {
        viewModelScope.launch {
            repository.plantDao.getPlant(plantId).collect { plant ->
                val photos = repository.getPlantPhotos(plantId)
                stateFlow.value = PlantScreenUiState(images = photos, plant = plant)
            }

        }
    }
}