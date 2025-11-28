package com.plantCare.plantcare.ui.screens.calendarScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.plantCare.plantcare.database.model.PlantWateringSchedule
import com.plantCare.plantcare.ui.theme.radius
import com.plantCare.plantcare.ui.theme.size
import com.plantCare.plantcare.ui.theme.spacing
import java.time.LocalDate
import java.time.temporal.ChronoUnit


enum class WateringState {
    WATERED,
    MISSED,
    SCHEDULED,
    SATISFIED,
    CURRENT_DAY,
    NONE,
}

@Composable
fun Modifier.getDayStyle(state: WateringState): Modifier {
    return when (state) {
        WateringState.WATERED -> this.background(
            color = getDayTextColor(state),
        )

        WateringState.MISSED -> this.border(
            width = 2.dp,
            color = getDayTextColor(state),
            shape = CircleShape
        )

        WateringState.SCHEDULED -> this.border(
            width = 2.dp,
            color = getDayTextColor(state),
            shape = RoundedCornerShape(MaterialTheme.radius.small)
        )

        WateringState.SATISFIED -> this.border(
            width = 2.dp,
            color = getDayTextColor(state),
            shape = RoundedCornerShape(MaterialTheme.radius.small)
        )

        WateringState.CURRENT_DAY -> this.border(
            width = 2.dp,
            color = getDayTextColor(state),
            shape = CircleShape
        )

        WateringState.NONE -> this
    }
}

@Composable
fun getDayTextColor(state: WateringState): Color {
    return when (state) {
        WateringState.WATERED -> MaterialTheme.colorScheme.primary
        WateringState.MISSED -> MaterialTheme.colorScheme.error
        WateringState.SCHEDULED -> MaterialTheme.colorScheme.tertiary // todo change color scheme
        WateringState.SATISFIED -> MaterialTheme.colorScheme.primary
        WateringState.CURRENT_DAY -> MaterialTheme.colorScheme.onSurface
        WateringState.NONE -> MaterialTheme.colorScheme.onSurface
    }
}

fun getWateringState(date: LocalDate, schedules: List<PlantWateringSchedule>): WateringState {
    for (schedule in schedules) {
        val diff = ChronoUnit.DAYS.between(schedule.startingDate, date)
//        Log.d("day", "diff: $diff")
        if (diff > 0 && diff % schedule.wateringInterval.interval == 0L) {
            return WateringState.SCHEDULED
        }
    }

    return WateringState.NONE
}

@Composable
fun Day(
    day: CalendarDay,
    plantWateringSchedules: List<PlantWateringSchedule>,
) {
    val state: WateringState = getWateringState(day.date, plantWateringSchedules)
    val modifier = Modifier.getDayStyle(state)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(MaterialTheme.spacing.extraSmall),
        contentAlignment = Alignment.Center
    ) {
        if (day.position == DayPosition.MonthDate) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = getDayTextColor(state),
                )
            }
        }
    }
}
