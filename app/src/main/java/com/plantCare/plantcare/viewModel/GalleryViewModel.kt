package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.PlantRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


data class GalleryState(
    val isLoading: Boolean = true,
    val media: List<File> = listOf()
)

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val plantRepository: PlantRepositoryImpl,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val plantId: Long = savedStateHandle["plantId"]!!

    private val stateFlow = MutableStateFlow(GalleryState())

    val state = stateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            plantRepository.getPlantMediaFlow(plantId).collect { media ->
                stateFlow.update {
                    it.copy(
                        isLoading = false,
                        media = media,
                    )
                }
            }
        }
    }

    fun deletePlantMedia(file: File) {
        viewModelScope.launch {
            plantRepository.deletePlantMedia(file)
        }
    }
}