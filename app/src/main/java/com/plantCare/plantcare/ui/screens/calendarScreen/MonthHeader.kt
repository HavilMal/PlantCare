package com.plantCare.plantcare.ui.screens.calendarScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.kizitonwose.calendar.core.CalendarMonth
import com.plantCare.plantcare.common.getLocale
import com.plantCare.plantcare.ui.theme.size
import com.plantCare.plantcare.ui.theme.spacing
import java.time.format.TextStyle

@Composable
fun MonthHeader(month: CalendarMonth) {
    val monthString =
        month.yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, getLocale())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(getLocale()) else it.toString() }

    val yearString = month.yearMonth.year.toString()

    Box(
        modifier = Modifier
            .height(MaterialTheme.size.medium)
            .padding(PaddingValues(horizontal = MaterialTheme.spacing.medium)),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = "$monthString $yearString "
        )
    }

}