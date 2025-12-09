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
import com.plantCare.plantcare.database.PlantRepository.Companion.PLANT_PHOTO_PREFIX
import com.plantCare.plantcare.utils.FileUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


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
            plantRepository.getPlantPhotos(plantId).collect { photo ->
                stateFlow.update {
                    it.copy(images = photo)
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

        viewModelScope.launch {
            stateFlow.collect { state ->
                if(state.plant != null){
                    if (plantImageObserver != null) return@collect
                    val plantDir = plantRepository.getPlantsDirPath(state.plant)
                    Log.d("lukas","file observer init ${plantDir}")
                    Log.d("lukas","file observer init ${File(plantDir).absolutePath}")
                    plantImageObserver = object : FileObserver(
                        File(context.filesDir,plantDir),
                        CREATE or DELETE or MOVED_TO or MOVED_FROM or CLOSE_WRITE
                    ) {

                        override fun onEvent(event: Int, path: String?) {
                            Log.d("lukas","file event occured")
                            val updated = FileUtil.getFiles(context, plantDir)
                                .filter { it.name.startsWith(PLANT_PHOTO_PREFIX) }

                            stateFlow.update { it.copy(images = updated) }
                        }
                    }
                    Log.d("lukas","file observer started watching $plantDir")
                    plantImageObserver?.startWatching()
                    Log.d("lukas","test file after")
                    awaitCancellation()
                }
            }
        }
    }
}