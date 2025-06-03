package com.example.frontendapp.ui.theme.composables.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    var currentYearMonth by remember { mutableStateOf(YearMonth.from(initialDate)) }
    var selectedDate by remember { mutableStateOf(initialDate) }
    val today = remember { LocalDate.now() }

    // Dirección de animación para cambio de mes
    var animationDirection by remember { mutableStateOf(0) }

    val dates = remember(currentYearMonth, selectedDate, fechasConHorarios) {
        currentYearMonth.getDaysForCalendar().map { date ->
            date?.let {
                val isEnabled = fechasConHorarios.any { dia -> dia == it }
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header mejorado
            CalendarHeader(
                yearMonth = currentYearMonth,
                onPreviousMonthButtonClicked = {
                    animationDirection = -1
                    currentYearMonth = it
                },
                onNextMonthButtonClicked = {
                    animationDirection = 1
                    currentYearMonth = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Días de la semana mejorados
            DayOfWeekRow()

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido del calendario con animación
            key(currentYearMonth) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { fullWidth -> fullWidth * animationDirection }
                            ),
                    exit = fadeOut(animationSpec = tween(300)) +
                            slideOutHorizontally(
                                animationSpec = tween(300),
                                targetOffsetX = { fullWidth -> -fullWidth * animationDirection }
                            )
                ) {
                    Content(
                        dates = dates,
                        today = today,
                        onDateClickListener = { date ->
                            selectedDate = date
                            onDateSelected(date)
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    Calendar(
        initialDate = LocalDate.now(),
        fechasConHorarios = emptySet(),
        onDateSelected = {},
        onMonthChanged = {}
    )
}