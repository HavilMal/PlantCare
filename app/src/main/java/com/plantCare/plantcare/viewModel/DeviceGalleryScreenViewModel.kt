package com.plantCare.plantcare.viewModel

import android.content.Context
import android.net.Uri
import android.os.FileObserver
import android.os.FileObserver.CREATE
import android.os.FileObserver.DELETE
import android.os.FileObserver.MOVED_FROM
import android.os.FileObserver.MOVED_TO
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.utils.FileUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
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
    suspend fun saveMedia(uris: List<Uri>) {
        uris.forEach { uri ->
            val file = FileUtil.copyUriToFile(context, uri)
            repository.addPlantMedia(plantId,file)
        }
    }
}