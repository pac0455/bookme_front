package com.example.frontendapp.data.helper

import com.example.frontendapp.data.model.UI.CalendarUiState
import java.time.LocalDate
import java.time.YearMonth

class CalendarDataSource {

    fun getDates(yearMonth: YearMonth): List<CalendarUiState.Date> {
        val today = LocalDate.now()

        return yearMonth.getDaysForCalendar().map { date ->
            CalendarUiState.Date(
                dayOfMonth = if (date.month == yearMonth.month) {
                    date.dayOfMonth.toString()
                } else {
                    "" // Los días fuera del mes actual se representan vacíos
                },
                isSelected = date == today && date.month == yearMonth.month,
                localDate = date
            )
        }
    }
}
fun YearMonth.getDaysForCalendar(): List<LocalDate> {
    val firstOfMonth = this.atDay(1)
    val firstDayOfWeek = firstOfMonth.dayOfWeek.value % 7 // Domingo = 0
    val calendarStart = firstOfMonth.minusDays(firstDayOfWeek.toLong())

    return List(42) { calendarStart.plusDays(it.toLong()) } // 6 semanas × 7 días
}

