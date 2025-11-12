package com.plantCare.plantcare.model

import androidx.lifecycle.ViewModel
import java.time.LocalDate

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

    val days: List<DayUiState>
)

data class CalendarUiState(
    val isLoading: Boolean,
    val months: List<MonthUiState>
)


class CalendarViewModel : ViewModel() {
}