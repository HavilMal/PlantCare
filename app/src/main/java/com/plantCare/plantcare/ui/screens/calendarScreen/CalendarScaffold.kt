package com.plantCare.plantcare.ui.screens.calendarScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.ui.components.BottomBar
import com.plantCare.plantcare.ui.components.TitleTopBar
import kotlinx.coroutines.launch
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScaffold(
    currentMonth: YearMonth,
    calendarState: CalendarState,
    content: Content,
) {
    val showScrollButton by remember {
        derivedStateOf {
            calendarState.firstVisibleMonth.yearMonth > currentMonth || currentMonth > calendarState.lastVisibleMonth.yearMonth
        }
    }
    val isScrolledForward by remember {
        derivedStateOf {
            currentMonth < calendarState.firstVisibleMonth.yearMonth
        }
    }
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val screenEdgeDistance = 72.dp
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TitleTopBar(
                title = "Calendar",
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = { BottomBar() },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showScrollButton,
                enter = slideInHorizontally {
                    with(density) { screenEdgeDistance.roundToPx() }
                },
                exit = slideOutHorizontally {
                    with(density) { screenEdgeDistance.roundToPx() }
                },
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            calendarState.animateScrollToMonth(currentMonth)
                        }
                    }
                ) {
                    if (isScrolledForward) {
                        Icon(Icons.Default.ArrowUpward, "Up arrow")
                    } else {
                        Icon(Icons.Default.ArrowDownward, "Up arrow")
                    }
                }
            }
        }
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}
