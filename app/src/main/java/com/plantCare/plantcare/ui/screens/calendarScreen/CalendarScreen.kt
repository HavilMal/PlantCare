package com.plantCare.plantcare.ui.screens.calendarScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.plantCare.plantcare.model.CalendarViewModel
import java.time.DayOfWeek
import java.time.YearMonth

const val INITIAL_INDEX = Int.MAX_VALUE / 2



@Composable
fun CalendarScreen(viewModel: CalendarViewModel = hiltViewModel()) {
    val state by viewModel.calendarState.collectAsState()

    val currentMonth = remember { YearMonth.now() }

    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { DayOfWeek.entries[0] }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
    )

    val firstVisibleMonth by remember { derivedStateOf { calendarState.firstVisibleMonth } }
    LaunchedEffect(firstVisibleMonth) {
        viewModel.onMonthVisible(firstVisibleMonth.yearMonth)
    }

    CalendarScaffold(currentMonth, calendarState) { modifier ->
        Column(
            modifier = modifier
        ) {
            WeekHeader()
            VerticalCalendar(
                userScrollEnabled = true,
                calendarScrollPaged = true,
                state = calendarState,
                dayContent = { day ->
                    Day(
                        day = day,
                        plantWateringSchedules = state.schedules,
                        monthData = viewModel.getMonthData(YearMonth.from(day.date))
                    )
                },
                monthHeader = {
                    MonthHeader(it)
                }
            )
        }
    }
}

