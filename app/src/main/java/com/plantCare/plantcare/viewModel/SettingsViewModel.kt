package com.plantCare.plantcare.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.AppSettingNotificationMode
import com.plantCare.plantcare.database.NotesRepository
import com.plantCare.plantcare.database.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUIState(
    val dateFormat: String = "unknown",
    val location: Pair<Double, Double> = 0.0 to 0.0,
    val notificationMode: AppSettingNotificationMode  = AppSettingNotificationMode.NEVER
)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    val settingsRepository: SettingsRepository
) : ViewModel() {
    private val stateFlow = MutableStateFlow(SettingsUIState())

    val uiState: StateFlow<SettingsUIState> = stateFlow

    init {
        viewModelScope.launch {
            settingsRepository.location.collect { loc ->
                stateFlow.update {
                    it.copy(location = loc)
                }
            }
        }
        viewModelScope.launch {
            settingsRepository.dateFormat.collect { d ->
                stateFlow.update {
                    it.copy(dateFormat = d)
                }
            }
        }
        viewModelScope.launch {
            settingsRepository.notificationMode.collect { n ->
                stateFlow.update {
                    it.copy(notificationMode = n)
                }
            }
        }
    }
    fun setLocation(lat: Double, lon: Double){
        viewModelScope.launch {
            settingsRepository.setLocation(lat, lon)
        }
    }
}