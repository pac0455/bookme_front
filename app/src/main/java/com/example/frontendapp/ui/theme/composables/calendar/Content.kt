package com.example.frontendapp.ui.theme.composables.calendar


import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.UI.CalendarUiState
import java.time.LocalDate
import java.time.YearMonth


@Composable
fun Content(
    dates: List<CalendarUiState.Date>,
    today: LocalDate,
    onDateClickListener: (LocalDate) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val itemWidth = maxWidth / 7 // 7 días de la semana

        Column {
            dates.chunked(7).forEach { week ->
                Row {
                    week.forEach { date ->
                        if (date == CalendarUiState.Date.Empty) {
                            // Celda vacía
                            Spacer(
                                modifier = Modifier
                                    .width(itemWidth)
                                    .aspectRatio(1f)
                            )
                        } else {
                            // Celda con fecha válida
                            ContentItem(
                                date = date,
                                isToday = date.localDate == today,
                                onClickListener = {
                                    if (date.isEnabled) {
                                        onDateClickListener(date.localDate)
                                    }
                                },
                                modifier = Modifier
                                    .width(itemWidth)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    val today = LocalDate.now()
    val yearMonth = YearMonth.from(today)
    val daysInMonth = yearMonth.lengthOfMonth()

    val mockDates = (1..daysInMonth).map { offset ->
        val date = today.plusDays(offset.toLong())
        CalendarUiState.Date(
            localDate = date,
            isSelected = offset == 0,
            dayOfMonth = date.dayOfMonth.toString())
    }

    Content(
        dates = mockDates,
        today = LocalDate.now(),
        onDateClickListener = {}
    )
}
