package com.plantCare.plantcare.model


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.WateringDao
import com.plantCare.plantcare.database.WateringRepository
import com.plantCare.plantcare.database.WeatherRepository
import com.plantCare.plantcare.database.model.PlantWateringSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.LinkedList

data class MonthData (
    val yearMonth: YearMonth,
    val wateredDays: Set<Int>,
    val rainDays: Set<Int>
)

data class CalendarUiState(
    val isLoading: Boolean,
    val schedules: List<PlantWateringSchedule>,
    val monthData: List<MonthData>
)


@HiltViewModel
class CalendarViewModel @Inject constructor(
    val plantRepository: PlantRepository,
    val weatherRepository: WeatherRepository,
    val wateringRepository: WateringRepository
) : ViewModel() {
    private val calendarStateFlow =
        MutableStateFlow(CalendarUiState(true, listOf(), LinkedList<MonthData>()))

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


    fun onMonthVisible(month: YearMonth) {
        if (calendarState.value.monthData.any { it.yearMonth == month }) return

        viewModelScope.launch {
            calculateMonthData(month).collect { md ->
                calendarStateFlow.update {
                    it.copy(
                        monthData = it.monthData + md
                    )
                }
            }
        }
    }

    fun calculateMonthData(month: YearMonth) : Flow<MonthData> {
        val firstDay : LocalDate = month.atDay(1)
        val lastDay : LocalDate = month.atEndOfMonth()

        val rainyDays:  Flow<List<LocalDate>> = weatherRepository.rainDays(firstDay, lastDay)
        val wateredDays:  Flow<List<LocalDate>> = wateringRepository.wateringDays(firstDay,lastDay)

        return combine(wateredDays, rainyDays) { watered, rainy ->
            MonthData(
                yearMonth = month,
                wateredDays = watered.map { ld -> ld.dayOfMonth }.toSet(),
                rainDays = rainy.map { ld -> ld.dayOfMonth }.toSet()
            )
        }
    }

    fun getMonthData(month: YearMonth): MonthData? {
        return calendarState.value.monthData.find { it.yearMonth == month }
    }
}