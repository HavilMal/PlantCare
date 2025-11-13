package com.plantCare.plantcare.ui.screens.calendarScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.plantCare.plantcare.common.getLocale
import com.plantCare.plantcare.model.CalendarUiState
import com.plantCare.plantcare.model.CalendarViewModel
import com.plantCare.plantcare.model.MonthUiState
import com.plantCare.plantcare.ui.theme.size
import com.plantCare.plantcare.ui.theme.spacing
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


// todo use pagination this wont work


fun YearMonth.getDayOfMonthStartingFromMonday(): List<LocalDate> {
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val firstDayOfNextMonth = firstDayOfMonth.plusMonths(1)

    return generateSequence(firstDayOfMonth) { it.plusDays(1) }
        .takeWhile { it.isBefore(firstDayOfNextMonth) }
        .toList()
}

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = INITIAL_INDEX
    ),
    calendarViewModel: CalendarViewModel,
) {
    val state by calendarViewModel.calendarState.collectAsState()

    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = lazyListState,
        snapPosition = SnapPosition.Start,
    )

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { index->
                calendarViewModel.fetchCalendarDataIfNeeded(
                    state.currentDate.plusMonths((index - INITIAL_INDEX ).toLong())
                )
            }
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth(),
    ) {
        WeekHeader()

        LazyColumn(
            state = lazyListState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(
                count = Int.MAX_VALUE,
            ) { index ->
                val month = state.currentDate.plusMonths((index - INITIAL_INDEX ).toLong())
                val monthUiState = state.months[month]
                if (monthUiState != null) {
                    Month(monthUiState)
                } else {
                    Text("loading")
                }
            }
        }
    }

}

@Composable
fun Month(
    monthUiState: MonthUiState
) {

    val days = monthUiState.days
    val monthString = monthUiState.monthDate.month.getDisplayName(TextStyle.FULL_STANDALONE, getLocale())
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(getLocale()) else it.toString() }
    val yearString = monthUiState.monthDate.year.toString()
    val weekStart = DayOfWeek.MONDAY
    var index = 0

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .height(MaterialTheme.size.medium)
                .padding(PaddingValues(horizontal = MaterialTheme.spacing.medium)),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "$monthString $yearString"
            )
        }


        val offset = ((days.first().date.dayOfWeek.value - weekStart.value) + 7) % 7
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
        )
        {
            repeat(offset) {
                Day("")
            }
            repeat(7 - offset) {
                Day(days[index].date.dayOfMonth.toString())
                index++
            }
        }

        while (index < days.size) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                repeat(7) {
                    if (index < days.size) {
                        Day(days[index].date.dayOfMonth.toString())
                    } else {
                        Day("")
                    }
                    index++
                }
            }
        }
    }
}

@Composable
fun WeekHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        DayOfWeek.entries.forEach { it ->
            Day(it.getDisplayName(TextStyle.SHORT, getLocale()))
        }
    }
}
