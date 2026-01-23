package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class ListUiState(
    val isLoading: Boolean = true,
    val plants: List<Plant> = listOf(),
    val thumbnails: Map<Long, File?> = mapOf(),
)

@HiltViewModel
class ListViewModel @Inject constructor(
    plantRepository: PlantRepositoryImpl,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val listFlow = MutableStateFlow(ListUiState())
    val listState = listFlow.asStateFlow()

    init {
        viewModelScope.launch {
            plantRepository.plantDao.getPlantsFlow()
                .collect { plantList ->
                    listFlow.update { currentState ->
                        currentState.copy(
                            plants = plantList,
                            isLoading = false,
                        )
                    }
                }
        }

        viewModelScope.launch {
            plantRepository.getPlantsTumbnailFlow().collect { thumbnails ->
                listFlow.update {
                    it.copy(thumbnails = thumbnails)
                }
            }
        }
    }
}