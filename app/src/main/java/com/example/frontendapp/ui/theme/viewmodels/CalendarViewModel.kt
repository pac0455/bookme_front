package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.helper.CalendarDataSource
import com.example.frontendapp.data.model.UI.CalendarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth

class CalendarViewModel(
    private val dataSource: CalendarDataSource = CalendarDataSource()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CalendarUiState(
            yearMonth = YearMonth.now(),
            dates = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadDates(YearMonth.now())
    }

    fun toNextMonth() {
        val nextMonth = _uiState.value.yearMonth.plusMonths(1)
        loadDates(nextMonth)
    }

    fun toPreviousMonth() {
        val prevMonth = _uiState.value.yearMonth.minusMonths(1)
        loadDates(prevMonth)
    }

    private fun loadDates(targetMonth: YearMonth) {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(
                    yearMonth = targetMonth,
                    dates = dataSource.getDates(targetMonth)
                )
            }
        }
    }
}