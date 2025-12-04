package com.plantCare.plantcare.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.WateringSchedule
import com.plantCare.plantcare.database.model.PlantWateringSchedule
import com.plantCare.plantcare.ui.screens.calendarScreen.WateringState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

data class CalendarUiState(
    val isLoading: Boolean,
    val schedules: List<PlantWateringSchedule>
)


@HiltViewModel
class CalendarViewModel @Inject constructor(
    plantRepository: PlantRepository
) : ViewModel() {
    private val currentDate = LocalDate.now()
    private val calendarStateFlow =
        MutableStateFlow(CalendarUiState(true, listOf()))

    val calendarState = calendarStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            plantRepository.getPlantWateringSchedules().collect { schedules ->
                calendarStateFlow.update {
                    it.copy(
                        isLoading = false,
                        schedules = schedules
                    )
                }
            }
        }
    }

}