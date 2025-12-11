package com.plantCare.plantcare.viewModel

import android.content.Context
import android.os.FileObserver
import android.os.FileObserver.CREATE
import android.os.FileObserver.DELETE
import android.os.FileObserver.MOVED_FROM
import android.os.FileObserver.MOVED_TO
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.NotesRepository
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.utils.FileUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.math.abs


data class PlantScreenUiState(
    val images: List<File> = emptyList(),
    val notes: List<Note> = emptyList(),
    val plant: Plant? = null,
)

@HiltViewModel
class PlantScreenViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val notesRepository: NotesRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val plantId: Long = checkNotNull(savedStateHandle.get<Long>("plantId"))
    private val stateFlow = MutableStateFlow(PlantScreenUiState())
    val uiState: StateFlow<PlantScreenUiState> = stateFlow
    private var plantImageObserver: FileObserver? = null

    init {
        viewModelScope.launch {
            plantRepository.getPlant(plantId).collect { plant ->
                stateFlow.update{
                    it.copy(plant = plant)
                }
            }
        }
        viewModelScope.launch {
            val plantDir = plantRepository.getPlantsDirPath(plantId)
            if (plantDir != null) {
                observePlantPhotos(context, plantDir).collect { images ->
                    stateFlow.update { it.copy(images = images) }
                }
            }
        }


        viewModelScope.launch {
            notesRepository.getPlantNotesFlow(plantId).collect { notes ->
                stateFlow.update {
                    it.copy(notes = notes)
                }
            }
        }
    }
    fun observePlantPhotos(context: Context, plantDir: String): Flow<List<File>> = callbackFlow {
        val imageDirPath = plantRepository.getPlantsImageDirPath(plantDir)
        val initial = FileUtil.getFiles(context, imageDirPath)
        trySend(initial)
        val observer = object : FileObserver(File(context.filesDir, imageDirPath), CREATE or MOVED_TO or DELETE or MOVED_FROM) {
            override fun onEvent(event: Int, path: String?) {
                val updated = FileUtil.getFiles(context, imageDirPath)
                trySend(updated)
            }
        }
        observer.startWatching()
        awaitClose { observer.stopWatching() }
    }
    override fun onCleared() {
        plantImageObserver?.stopWatching()
    }
}