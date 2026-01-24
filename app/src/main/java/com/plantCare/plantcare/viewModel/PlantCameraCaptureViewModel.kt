package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.PlantRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlantCameraCaptureViewModel @Inject constructor(
    private val repository: PlantRepositoryImpl,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val plantId: Long = savedStateHandle["plantId"]!!
    fun savePhoto(file: File?){
        viewModelScope.launch {
            if(file != null) {
                repository.addPlantMedia(plantId, file)
            }
        }
    }
}