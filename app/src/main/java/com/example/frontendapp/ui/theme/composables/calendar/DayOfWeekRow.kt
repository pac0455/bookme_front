package com.example.frontendapp.ui.theme.composables.calendar
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier

import androidx.compose.ui.text.style.TextAlign
import com.example.frontendapp.data.helper.CalendarHelper


@Composable
fun DayOfWeekRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        CalendarHelper.daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}