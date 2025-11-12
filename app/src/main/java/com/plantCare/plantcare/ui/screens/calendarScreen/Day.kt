package com.plantCare.plantcare.ui.screens.calendarScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import com.plantCare.plantcare.ui.theme.radius
import com.plantCare.plantcare.ui.theme.size


enum class WATERING_STATE {
    WATERED,
    MISSED,
    SCHEDULED,
    SATISFIED,
    CURRENT_DAY,
    NONE,
}

@Composable
fun Modifier.getDayStyle(state: WATERING_STATE): Modifier {
    return when (state) {
        WATERING_STATE.WATERED -> this.background(
            color = getDayTextColor(state),
        )

        WATERING_STATE.MISSED -> this.border(
            width = 2.dp,
            color = getDayTextColor(state),
            shape = CircleShape
        )

        WATERING_STATE.SCHEDULED -> this.border(
            width = 2.dp,
            color = getDayTextColor(state),
            shape = RoundedCornerShape(MaterialTheme.radius.small)
        )

        WATERING_STATE.SATISFIED -> this.border(
            width = 2.dp,
            color = getDayTextColor(state),
            shape = RoundedCornerShape(MaterialTheme.radius.small)
        )

        WATERING_STATE.CURRENT_DAY -> this.border(
            width = 2.dp,
            color = getDayTextColor(state),
            shape = CircleShape
        )

        WATERING_STATE.NONE -> this
    }
}

@Composable
fun getDayTextColor(state: WATERING_STATE): Color {
    return when (state) {
        WATERING_STATE.WATERED -> MaterialTheme.colorScheme.primary
        WATERING_STATE.MISSED -> MaterialTheme.colorScheme.error
        WATERING_STATE.SCHEDULED -> MaterialTheme.colorScheme.tertiary // todo change color scheme
        WATERING_STATE.SATISFIED -> MaterialTheme.colorScheme.primary
        WATERING_STATE.CURRENT_DAY -> MaterialTheme.colorScheme.onSurface
        WATERING_STATE.NONE -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
fun Day(
    value: String,
    state: WATERING_STATE = WATERING_STATE.NONE,
) {
    val modifier = Modifier.getDayStyle(state)

    Box(
        modifier = Modifier
            .size(MaterialTheme.size.medium),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = modifier
                .size(MaterialTheme.size.mediumInner),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = value,
                color = getDayTextColor(state),
            )
        }

    }
}
