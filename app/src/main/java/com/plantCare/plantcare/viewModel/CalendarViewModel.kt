package com.plantCare.plantcare.model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantCare.plantcare.database.PlantRepositoryImpl
import com.plantCare.plantcare.database.UserActivityRepository
import com.plantCare.plantcare.database.WateringRepository
import com.plantCare.plantcare.database.WeatherRepository
import com.plantCare.plantcare.database.model.PlantWateringSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth

data class MonthData (
    val wateredDays: Set<Int>,
    val rainDays: Set<Int>,
    val streakBreaks: Set<Int>
)

data class CalendarUiState(
    val isLoading: Boolean,
    val schedules: List<PlantWateringSchedule>,
    val monthData: Map<YearMonth, MonthData>
)


@HiltViewModel
class CalendarViewModel @Inject constructor(
    val plantRepository: PlantRepositoryImpl,
    val weatherRepository: WeatherRepository,
    val wateringRepository: WateringRepository,
    val userActivityRepository: UserActivityRepository
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
        val monthsToLoad = (-2..2).map { month.plusMonths(it.toLong()) }
            .filter { !calendarState.value.monthData.containsKey(it) }

        monthsToLoad.forEach { m ->
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

        val monthsToKeep = (-4..4).map { month.plusMonths(it.toLong()) }.toSet()
        calendarStateFlow.update {
            it.copy(monthData = it.monthData.filterKeys { it in monthsToKeep })
        }
    }

    fun calculateMonthData(month: YearMonth) : Flow<MonthData> {
        val firstDay = month.atDay(1)
        val lastDay = month.atEndOfMonth()

        val rainyDays = weatherRepository.rainDays(firstDay, lastDay)
        val wateredDays = wateringRepository.wateringDays(firstDay, lastDay)
        val streakBreaks = userActivityRepository.getStreakBreaks(firstDay,lastDay)

        return combine(wateredDays, rainyDays, streakBreaks) { watered, rainy, streakBreak ->
            MonthData(
                wateredDays = watered.map { it.dayOfMonth }.toSet(),
                rainDays = rainy.map { it.dayOfMonth }.toSet(),
                streakBreaks = streakBreak.map { it.dayOfMonth }.toSet()
            )
        }
    }

    fun getMonthData(month: YearMonth): MonthData? {
        return calendarState.value.monthData[month]
    }
}