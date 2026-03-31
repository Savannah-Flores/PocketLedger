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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val balance: String = formatCurrency(MONTHLY_LIVING_BUDGET),
    val todaySpent: String = formatCurrency(0.0),
    val monthExpense: String = formatCurrency(0.0),
    val budgetBaseline: String = formatCurrency(MONTHLY_LIVING_BUDGET),
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
    return HomeUiState(
        balance = formatCurrency(calculateMonthlyBalance(this)),
        todaySpent = formatCurrency(calculateTodayExpense(this)),
        monthExpense = formatCurrency(calculateMonthExpense(this)),
        budgetBaseline = formatCurrency(MONTHLY_LIVING_BUDGET),
    )
}
