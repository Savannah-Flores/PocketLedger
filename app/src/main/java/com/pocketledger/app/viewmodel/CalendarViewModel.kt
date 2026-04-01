package com.pocketledger.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pocketledger.app.data.local.TransactionEntity
import com.pocketledger.app.data.repository.LedgerRepository
import com.pocketledger.app.ui.calendar.CalendarUiState
import com.pocketledger.app.utils.buildCalendarMonthCells
import com.pocketledger.app.utils.formatCurrency
import com.pocketledger.app.utils.formatMonthTitle
import com.pocketledger.app.utils.timestampToLocalDate
import com.pocketledger.app.utils.toRecordItemUiModel
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CalendarViewModel(
    ledgerRepository: LedgerRepository,
) : ViewModel() {
    private val currentMonth = MutableStateFlow(YearMonth.now())
    private val selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<CalendarUiState> = combine(
        ledgerRepository.observeTransactions(),
        currentMonth,
        selectedDate,
    ) { transactions, month, pickedDate ->
        val monthSelectedDate = when {
            YearMonth.from(pickedDate) == month -> pickedDate
            LocalDate.now().let { YearMonth.from(it) == month } -> LocalDate.now()
            else -> month.atDay(1)
        }

        buildCalendarUiState(
            transactions = transactions,
            month = month,
            selectedDate = monthSelectedDate,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CalendarUiState(
            currentMonthLabel = formatMonthTitle(YearMonth.now()),
        ),
    )

    fun goToPreviousMonth() {
        currentMonth.update { it.minusMonths(1) }
    }

    fun goToNextMonth() {
        currentMonth.update { it.plusMonths(1) }
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
        currentMonth.value = YearMonth.from(date)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CalendarViewModel(
                    ledgerRepository = this.ledgerRepository(),
                )
            }
        }
    }
}

private fun buildCalendarUiState(
    transactions: List<TransactionEntity>,
    month: YearMonth,
    selectedDate: LocalDate,
): CalendarUiState {
    val expenseByDate = transactions
        .filter { RecordType.fromStorage(it.type) == RecordType.Expense }
        .groupBy { timestampToLocalDate(it.timestamp) }
        .mapValues { (_, items) -> items.sumOf { it.amount } }

    val selectedDateRecords = transactions
        .filter { timestampToLocalDate(it.timestamp) == selectedDate }
        .sortedByDescending { it.timestamp }
        .map { it.toRecordItemUiModel() }

    return CalendarUiState(
        currentMonthLabel = formatMonthTitle(month),
        currentMonthDates = buildCalendarMonthCells(month, expenseByDate),
        selectedDate = selectedDate,
        selectedDateExpense = formatCurrency(expenseByDate[selectedDate] ?: 0.0),
        selectedDateRecords = selectedDateRecords,
    )
}
