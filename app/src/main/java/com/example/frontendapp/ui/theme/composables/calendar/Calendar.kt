    package com.example.frontendapp.ui.theme.composables.calendar
    import android.util.Log
    import androidx.compose.foundation.layout.*
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.tooling.preview.Preview
    import com.example.frontendapp.data.helper.CalendarHelper.getDaysForCalendar
    import com.example.frontendapp.data.model.UI.CalendarUiState
    import java.time.LocalDate
    import java.time.YearMonth

    @Composable
    fun Calendar(
        initialDate: LocalDate = LocalDate.now(),
        fechasConHorarios: Set<LocalDate> = emptySet(), // fechas habilitadas
        onDateSelected: (LocalDate) -> Unit,
        onMonthChanged: (YearMonth) -> Unit
    ) {
        // Log para ver contenido de fechasConHorarios cada recomposición
        Log.d("Calendar", "Entrando a Calendar - fechasConHorarios.size = ${fechasConHorarios.size}")
        if (fechasConHorarios.isEmpty()) {
            Log.d("Calendar", "fechasConHorarios está VACÍO")
        } else {
            fechasConHorarios.forEach { fecha ->
                Log.d("Calendar", "fechasConHorarios contiene: $fecha")
            }
        }
        var currentYearMonth by remember { mutableStateOf(YearMonth.from(initialDate)) }
        var selectedDate by remember { mutableStateOf(initialDate) }
        val dates = remember(currentYearMonth, selectedDate, fechasConHorarios) {
            Log.d("Calendar", "==== Inicio comparación fechas ====")
            Log.d("Calendar", "Fechas con horarios disponibles:")
            fechasConHorarios.forEach { Log.d("Calendar", " - $it") }

            currentYearMonth.getDaysForCalendar().map { date ->
                date?.let {
                    val isEnabled = fechasConHorarios.any { dia -> dia == it }
                    Log.d("Calendar", "¿$it está habilitado? $isEnabled")

                    CalendarUiState.Date(
                        dayOfMonth = it.dayOfMonth.toString(),
                        isSelected = it == selectedDate,
                        localDate = it,
                        isEnabled = isEnabled
                    )
                } ?: CalendarUiState.Date.Empty
            }
        }




        LaunchedEffect(currentYearMonth) {
            onMonthChanged(currentYearMonth)
        }
        Column {
            CalendarHeader(
                yearMonth = currentYearMonth,
                onPreviousMonthButtonClicked = { currentYearMonth = it },
                onNextMonthButtonClicked = { currentYearMonth = it }
            )
            DayOfWeekRow()
            Content(
                dates = dates,
                onDateClickListener = { date ->
                    selectedDate = date
                    onDateSelected(date)
                }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun CalendarPreview() {
        Calendar(
            initialDate = LocalDate.now(),
            fechasConHorarios = emptySet(), // O añade fechas si quieres probar selección
            onDateSelected = {},
            onMonthChanged = {} // Evita error de undefined variable
        )
    }
