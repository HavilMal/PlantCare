package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListUiState(
    val plants: List<Plant> = listOf()
)

@HiltViewModel
class ListViewModel @Inject constructor(
    plantRepository: PlantRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val listFlow = MutableStateFlow(ListUiState())
    val listState = listFlow.asStateFlow()

    init {
        viewModelScope.launch {
            plantRepository.plantDao.getPlants()
                .collect { plantList ->
                    listFlow.update { currentState ->
                        currentState.copy(plants = plantList)
                    }
                }
        }
    }
}