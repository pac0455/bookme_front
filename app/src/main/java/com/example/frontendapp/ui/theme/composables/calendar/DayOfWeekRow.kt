package com.example.frontendapp.ui.theme.composables.calendar
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextAlign
import com.example.frontendapp.data.helper.CalendarHelper
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun DayOfWeekRow() {
    val daysOfWeek = remember {
        DayOfWeek.values().map {
            it.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
                .replaceFirstChar { char -> char.uppercase() }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysOfWeek.forEach { day ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
