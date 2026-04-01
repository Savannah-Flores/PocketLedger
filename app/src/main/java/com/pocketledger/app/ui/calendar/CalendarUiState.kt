package com.pocketledger.app.ui.calendar

import com.pocketledger.app.ui.model.RecordItemUiModel
import java.time.LocalDate

data class CalendarDayCellUiModel(
    val date: LocalDate,
    val isInCurrentMonth: Boolean,
    val expenseTotal: Double,
)

data class CalendarUiState(
    val currentMonthLabel: String = "",
    val currentMonthDates: List<CalendarDayCellUiModel> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedDateExpense: String = "¥0.00",
    val selectedDateRecords: List<RecordItemUiModel> = emptyList(),
)
