package com.example.frontendapp.data.helper



import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

object CalendarHelper {

    val daysOfWeek: Array<String>
        get() = DayOfWeek.entries.map {
            it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }.toTypedArray()

    fun YearMonth.getDaysForCalendar(): List<LocalDate?> {
        val firstOfMonth = this.atDay(1) // Primer día del mes
        val firstDayOfWeek = firstOfMonth.dayOfWeek.value // 1 (Lunes) ... 7 (Domingo)

        val daysBefore = (firstDayOfWeek - DayOfWeek.MONDAY.value + 7) % 7 // Ajustado

        val daysInMonth = this.lengthOfMonth()
        val totalDays = daysBefore + daysInMonth

        return List(42) { index ->
            when {
                index < daysBefore -> null
                index < totalDays -> this.atDay(index - daysBefore + 1)
                else -> null
            }
        }
    }



    fun YearMonth.getDisplayName(): String {
        val name = this.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        return "${name.replaceFirstChar { it.uppercase() }} ${this.year}"
    }
}
