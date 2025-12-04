package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

data class GalleryUiState(
    val images: List<File> = emptyList(),
    val plant: Plant? = null,
)

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: PlantRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val plantId: Long = checkNotNull(savedStateHandle.get<Long>("plantId"))
    private val stateFlow = MutableStateFlow<PlantScreenUiState>(PlantScreenUiState())
    val uiState: StateFlow<PlantScreenUiState> = stateFlow

    init {
        viewModelScope.launch {
            repository.plantDao.getPlantFlow(plantId).collect { plant ->
                val photos = repository.getPlantPhotos(plantId)
                stateFlow.value = PlantScreenUiState(images = photos, plant = plant)
            }

        }
    }
}