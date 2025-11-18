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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlantCameraCaptureViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    private val homeFlow = MutableStateFlow(HomeUiState())
    val homeState = homeFlow.asStateFlow()

    suspend fun savePhoto(file: File?){
        repository.addPlantPhoto(,file)
    }
}