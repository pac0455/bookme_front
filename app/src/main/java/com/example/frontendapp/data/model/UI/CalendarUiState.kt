package com.example.frontendapp.data.model.UI

import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val yearMonth: YearMonth,
    val dates: List<Date>
) {
    data class Date(
        val dayOfMonth: String,
        val isSelected: Boolean,
        val localDate: LocalDate,
        val isEnabled: Boolean = true
    ) {
        companion object {
            val Empty = Date(dayOfMonth = "", isSelected = false, localDate = LocalDate.MIN)
        }
    }

    companion object {
        val Init = CalendarUiState(
            yearMonth = YearMonth.now(),
            dates = emptyList()
        )
    }
}
