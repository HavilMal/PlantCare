package com.plantCare.plantcare.ui.screens.calendarScreen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.plantCare.plantcare.model.CalendarViewModel

const val INITIAL_INDEX = Int.MAX_VALUE / 2

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = CalendarViewModel()) {
    val state by viewModel.calendarState.collectAsState()

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = INITIAL_INDEX
    )

    CalendarScaffold(lazyListState) { contentPadding ->
        Calendar(modifier = contentPadding, lazyListState = lazyListState, viewModel)
    }
}