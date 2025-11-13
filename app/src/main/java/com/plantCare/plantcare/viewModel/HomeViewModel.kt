package com.plantCare.plantcare.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val plants: List<Plant> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    private val homeFlow = MutableStateFlow(HomeUiState())
    val homeState = homeFlow.asStateFlow()

    init {
        viewModelScope.launch {
            repository.plantDao.getPlants()
                .collect { plantList ->
                    homeFlow.update { currentState ->
                        currentState.copy(plants = plantList)
                    }
                }
        }
    }
}