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
import com.pocketledger.app.utils.timestampToLocalDate
import java.time.YearMonth
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

data class HomeUiState(
    val balance: String = formatCurrency(MONTHLY_LIVING_BUDGET),
    val todaySpent: String = formatCurrency(0.0),
    val monthExpense: String = formatCurrency(0.0),
    val budgetBaseline: String = formatCurrency(MONTHLY_LIVING_BUDGET),
    val spendingDistribution: List<SpendingCategorySummary> = emptyList(),
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
    )
}

private fun List<TransactionEntity>.buildSpendingDistribution(totalExpense: Double): List<SpendingCategorySummary> {
    if (totalExpense <= 0.0) return emptyList()
    val currentMonth = YearMonth.now()

    return filter { transaction ->
        val transactionMonth = YearMonth.from(timestampToLocalDate(transaction.timestamp))
        transactionMonth == currentMonth && RecordType.fromStorage(transaction.type) == RecordType.Expense
    }
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
                formattedAmount = formatCurrency(amount).replace("¥", "￥"),
                ratio = ratio,
                formattedRatio = String.format("%.1f%%", ratio * 100),
            )
        }
}
