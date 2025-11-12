package com.plantCare.plantcare.ui.screens.calendarScreen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable

const val INITIAL_INDEX = Int.MAX_VALUE / 2

@Composable
fun CalendarScreen() {

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = INITIAL_INDEX
    )

    CalendarScaffold(lazyListState) { contentPadding ->
        Calendar(modifier = contentPadding, lazyListState = lazyListState)
    }
}