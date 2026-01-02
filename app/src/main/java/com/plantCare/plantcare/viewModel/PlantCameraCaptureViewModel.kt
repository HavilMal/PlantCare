package com.plantCare.plantcare.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlantCameraCaptureViewModel @Inject constructor(
    private val repository: PlantRepository,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val plantId: Long = savedStateHandle["plantId"]!!
    fun savePhoto(file: File?){
        viewModelScope.launch {
//            Log.d("photo", "Save id: $plantId")
            if(file != null) {
                repository.addPlantMedia(plantId, file)
            }
        }
    }
}