package com.plantCare.plantcare.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlin.math.abs

enum class WATERING_STATE {
    WATERED,
    MISSED,
    SCHEDULED,
    SATISFIED,
    CURRENT_DAY,
    NONE,
}

data class DayUiState(
    val date: LocalDate,
    val state: WATERING_STATE,
)

data class MonthUiState(
    val monthDate: YearMonth,
    val days: List<DayUiState>
)

data class CalendarUiState(
    val isLoading: Boolean,
    val currentDate: YearMonth,
    val fetchedFor: YearMonth,
    val months: Map<YearMonth, MonthUiState>
)


class CalendarViewModel : ViewModel() {
    private val currentDate = YearMonth.now()
    private val calendarStateFlow =
        MutableStateFlow(CalendarUiState(true, currentDate, currentDate, mapOf()))

    val calendarState = calendarStateFlow.asStateFlow()

    init {
        fetchCalendarData(currentDate)
    }

    fun fetchCalendarDataIfNeeded(date: YearMonth) {
        if (abs(ChronoUnit.MONTHS.between(date, calendarState.value.fetchedFor)) > 3) {
            fetchCalendarData(date)
        }
    }

    fun fetchCalendarData(date: YearMonth) {
        viewModelScope.launch {
            calendarStateFlow.update { it.copy(isLoading = true) }
            val newMonths = generateMonthsAround(date, 5)
            calendarStateFlow.update {
                it.copy(isLoading = false, fetchedFor = currentDate, months = newMonths)
            }
        }
    }

    private fun generateMonthsAround(center: YearMonth, range: Long): Map<YearMonth, MonthUiState> {
        val months = mutableMapOf<YearMonth, MonthUiState>()

        for (i in 1..range) {
            val month = center.minusMonths(i)
            months[month] = MonthUiState(month, createMonthDays(month))
        }

        months[center] = MonthUiState(center, createMonthDays(center))

        for (i in 1..range) {
            val month = center.plusMonths(i)
            months[month] = MonthUiState(month, createMonthDays(month))
        }

        return months
    }

    private fun createMonthDays(month: YearMonth): List<DayUiState> {
        val daysInMonth = month.lengthOfMonth()
        return (1..daysInMonth).map { day ->
            DayUiState(
                date = month.atDay(day),
                state = if (month.atDay(day) == currentDate) {
                    WATERING_STATE.NONE
                } else {
                    WATERING_STATE.CURRENT_DAY
                }
            )
        }
    }
}