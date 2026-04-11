package com.pocketledger.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pocketledger.app.data.local.TransactionEntity
import com.pocketledger.app.data.repository.LedgerRepository
import com.pocketledger.app.utils.MONTHLY_LIVING_BUDGET
import com.pocketledger.app.utils.calculateMonthExpense
import com.pocketledger.app.utils.calculateMonthlyBalance
import com.pocketledger.app.utils.calculateTodayExpense
import com.pocketledger.app.utils.formatCurrency
import com.pocketledger.app.utils.timestampToDate
import com.pocketledger.app.utils.timestampToLocalDate
import com.pocketledger.app.utils.timestampToTime
import java.time.YearMonth
import java.util.Locale
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SpendingCategorySummary(
    val category: String,
    val amount: Double,
    val formattedAmount: String,
    val ratio: Float,
    val formattedRatio: String,
)

data class SpendingDetailItem(
    val id: Long,
    val content: String,
    val amount: Double,
    val formattedAmount: String,
    val date: String,
    val time: String,
    val category: String,
    val timestamp: Long,
)

data class HomeUiState(
    val balance: String = formatCurrency(MONTHLY_LIVING_BUDGET),
    val todaySpent: String = formatCurrency(0.0),
    val monthExpense: String = formatCurrency(0.0),
    val budgetBaseline: String = formatCurrency(MONTHLY_LIVING_BUDGET),
    val spendingDistribution: List<SpendingCategorySummary> = emptyList(),
    val spendingDetailsByCategory: Map<String, List<SpendingDetailItem>> = emptyMap(),
)

class HomeViewModel(
    ledgerRepository: LedgerRepository,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = ledgerRepository.observeTransactions()
        .map { transactions -> transactions.toHomeUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(
                    ledgerRepository = this.ledgerRepository(),
                )
            }
        }
    }
}

private fun List<TransactionEntity>.toHomeUiState(): HomeUiState {
    val monthExpense = calculateMonthExpense(this)
    return HomeUiState(
        balance = formatCurrency(calculateMonthlyBalance(this)),
        todaySpent = formatCurrency(calculateTodayExpense(this)),
        monthExpense = formatCurrency(monthExpense),
        budgetBaseline = formatCurrency(MONTHLY_LIVING_BUDGET),
        spendingDistribution = buildSpendingDistribution(monthExpense),
        spendingDetailsByCategory = buildSpendingDetailsByCategory(),
    )
}

private fun List<TransactionEntity>.currentMonthExpenseTransactions(): List<TransactionEntity> {
    val currentMonth = YearMonth.now()
    return filter { transaction ->
        val transactionMonth = YearMonth.from(timestampToLocalDate(transaction.timestamp))
        transactionMonth == currentMonth && RecordType.fromStorage(transaction.type) == RecordType.Expense
    }
}

private fun List<TransactionEntity>.buildSpendingDistribution(totalExpense: Double): List<SpendingCategorySummary> {
    if (totalExpense <= 0.0) return emptyList()

    return currentMonthExpenseTransactions()
        .groupBy { it.category }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
        .filterValues { it > 0.0 }
        .toList()
        .sortedByDescending { (_, amount) -> amount }
        .map { (category, amount) ->
            val ratio = (amount / totalExpense).toFloat().coerceIn(0f, 1f)
            SpendingCategorySummary(
                category = category,
                amount = amount,
                formattedAmount = formatCurrency(amount).replace("гд", "гд"),
                ratio = ratio,
                formattedRatio = String.format(Locale.CHINA, "%.1f%%", ratio * 100),
            )
        }
}

private fun List<TransactionEntity>.buildSpendingDetailsByCategory(): Map<String, List<SpendingDetailItem>> {
    return currentMonthExpenseTransactions()
        .sortedByDescending { it.timestamp }
        .groupBy { it.category }
        .mapValues { (_, transactions) ->
            transactions.map { transaction ->
                SpendingDetailItem(
                    id = transaction.id,
                    content = transaction.title,
                    amount = transaction.amount,
                    formattedAmount = formatCurrency(transaction.amount).replace("гд", "гд"),
                    date = timestampToDate(transaction.timestamp),
                    time = timestampToTime(transaction.timestamp),
                    category = transaction.category,
                    timestamp = transaction.timestamp,
                )
            }
        }
}
