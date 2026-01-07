package com.plantCare.plantcare.ui.screens.calendarScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.common.capitalize
import com.plantCare.plantcare.common.getLocale
import com.plantCare.plantcare.ui.theme.size
import java.time.DayOfWeek
import java.time.format.TextStyle

@Composable
fun WeekHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        val locale = getLocale()
        DayOfWeek.entries.forEach { it ->
            Box(
                modifier = Modifier.size(MaterialTheme.size.medium),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = it.getDisplayName(
                        TextStyle.SHORT,
                       locale
                    ).filter { it != '.' }
                        .take(3).capitalize(locale)
                )
            }
        }
    }
}