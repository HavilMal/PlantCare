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
    val wateredDays: Set<Int>,
    val rainDays: Set<Int>
)

data class CalendarUiState(
    val isLoading: Boolean,
    val schedules: List<PlantWateringSchedule>,
    val monthData: Map<YearMonth, MonthData>
)


@HiltViewModel
class CalendarViewModel @Inject constructor(
    val plantRepository: PlantRepository,
    val weatherRepository: WeatherRepository,
    val wateringRepository: WateringRepository
) : ViewModel() {
    private val calendarStateFlow =
        MutableStateFlow(CalendarUiState(true, listOf(), emptyMap()))

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
        Log.d("devocal","months loaded = ${calendarState.value.monthData}")
        val monthsToLoad = (-2..2).map { month.plusMonths(it.toLong()) }
            .filter { !calendarState.value.monthData.containsKey(it) }

        monthsToLoad.forEach { m ->
            Log.d("devocal","month to load = $m")
            viewModelScope.launch {
                calculateMonthData(m).collect { md ->
                    calendarStateFlow.update {
                        it.copy(
                            monthData = it.monthData + (m to md)
                        )
                    }
                }
            }
        }

        val monthsToKeep = (-2..2).map { month.plusMonths(it.toLong()) }.toSet()
        calendarStateFlow.update {
            it.copy(monthData = it.monthData.filterKeys { it in monthsToKeep })
        }
    }

    fun calculateMonthData(month: YearMonth) : Flow<MonthData> {
        val firstDay = month.atDay(1)
        val lastDay = month.atEndOfMonth()

        val rainyDays = weatherRepository.rainDays(firstDay, lastDay)
        val wateredDays = wateringRepository.wateringDays(firstDay, lastDay)

        return combine(wateredDays, rainyDays) { watered, rainy ->
            MonthData(
                wateredDays = watered.map { it.dayOfMonth }.toSet(),
                rainDays = rainy.map { it.dayOfMonth }.toSet()
            )
        }
    }

    fun getMonthData(month: YearMonth): MonthData? {
        return calendarState.value.monthData[month]
    }
}