package com.plantCare.plantcare.ui.screens.calendarScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
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
import com.plantCare.plantcare.model.MonthData
import com.plantCare.plantcare.ui.theme.radius
import com.plantCare.plantcare.ui.theme.size
import com.plantCare.plantcare.ui.theme.spacing
import com.plantCare.plantcare.utils.DateUtil
import java.time.LocalDate
import java.time.temporal.ChronoUnit


enum class WateringState {
    WATERED,
    MISSED,
    SCHEDULED,
    SATISFIED,
    NONE,
}

@Composable

fun Modifier.getDayStyle(state: WateringState, color: Color, isToday: Boolean, isWatered: Boolean, isRainy: Boolean): Modifier {
    val width = if (isToday) 6.dp else 2.dp

    val baseModifier = when {
        isWatered ->
            this.border(
                width = width,
                color = color,
                shape = CircleShape
            )

        isRainy -> this.border(
            width = width,
            color = color,
            shape = CircleShape
        )

        state == WateringState.SCHEDULED -> this.border(
            width = width,
            color = color,
            shape = RoundedCornerShape(MaterialTheme.radius.small)
        )

        state == WateringState.SATISFIED -> this.border(
            width = width,
            color = color,
            shape = RoundedCornerShape(MaterialTheme.radius.small)
        )

       else -> this
    }

    return if (isToday && state == WateringState.NONE) {
        baseModifier.border(
            width = width,
            color = color,
            shape = AbsoluteCutCornerShape(MaterialTheme.radius.small)
        )
    } else {
        baseModifier
    }
}


@Composable
fun getDayColor(state: WateringState, isToday: Boolean, isWatered: Boolean, isRainy: Boolean): Color {
    return when {
        isWatered -> Color.Blue
        isRainy -> Color.Cyan
        state == WateringState.SCHEDULED -> MaterialTheme.colorScheme.tertiary // todo change color scheme
        state == WateringState.SATISFIED -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.inverseSurface
        state == WateringState.NONE -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface
    }
}

fun getWateringState(date: LocalDate, schedules: List<PlantWateringSchedule>): WateringState {
    for (schedule in schedules) {
        val diff = ChronoUnit.DAYS.between(schedule.startingDate, date)

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
    monthData: MonthData? = null
) {

    val state: WateringState = getWateringState(day.date, plantWateringSchedules)

    val isWatered = monthData?.wateredDays?.contains(day.date.dayOfMonth) ?: false
    val isRainy = monthData?.rainDays?.contains(day.date.dayOfMonth) ?: false
    val isToday = DateUtil.localDateToday() == day.date

    val textColor = getDayColor(state, isToday,isWatered, isRainy)
    val modifier = Modifier.getDayStyle(state, textColor,isToday, isWatered, isRainy)

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
                    color = textColor
                )
            }
        }
    }
}
