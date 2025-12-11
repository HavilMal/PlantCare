package com.plantCare.plantcare.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.utils.FileUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DeviceGalleryScreenViewModel @Inject constructor(
    private val repository: PlantRepository,
    val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val plantId: Long = savedStateHandle["plantId"]!!

    fun saveImages(uris: List<Uri>) {
        viewModelScope.launch {
            uris.forEach { uri ->
                val file = FileUtil.copyUriToFile(context, uri)
                repository.addPlantPhoto(plantId,file)
            }
        }
    }
}