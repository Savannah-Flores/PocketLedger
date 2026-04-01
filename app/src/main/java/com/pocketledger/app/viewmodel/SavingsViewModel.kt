package com.pocketledger.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pocketledger.app.data.local.SavingsDepositEntity
import com.pocketledger.app.data.repository.LedgerRepository
import com.pocketledger.app.ui.model.SavingsEntryUiModel
import com.pocketledger.app.utils.buildAutoSavingsEntries
import com.pocketledger.app.utils.formatCurrency
import com.pocketledger.app.utils.toSavingsEntryUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SavingsUiState(
    val totalBalance: String = formatCurrency(0.0),
    val entries: List<SavingsEntryUiModel> = emptyList(),
)

class SavingsViewModel(
    private val ledgerRepository: LedgerRepository,
) : ViewModel() {
    val uiState: StateFlow<SavingsUiState> = combine(
        ledgerRepository.observeTransactions(),
        ledgerRepository.observeSavingsDeposits(),
    ) { transactions, deposits ->
        val autoEntries = buildAutoSavingsEntries(transactions)
        val manualEntries = deposits.map { it.toSavingsEntryUiModel() }
        val allEntries = (autoEntries + manualEntries).sortedByDescending { it.sortTimestamp }
        val total = autoEntries.sumOf { parseCurrencyAmount(it.amount) } + deposits.sumOf { it.amount }
        SavingsUiState(
            totalBalance = formatCurrency(total),
            entries = allEntries,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SavingsUiState(),
    )

    fun addManualDeposit(title: String, amount: String): Boolean {
        val parsedAmount = amount.toDoubleOrNull() ?: return false
        if (parsedAmount <= 0) return false

        viewModelScope.launch {
            ledgerRepository.addSavingsDeposit(
                SavingsDepositEntity(
                    title = title.ifBlank { "手动存入" },
                    amount = parsedAmount,
                    timestamp = System.currentTimeMillis(),
                ),
            )
        }
        return true
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SavingsViewModel(
                    ledgerRepository = this.ledgerRepository(),
                )
            }
        }
    }
}

private fun parseCurrencyAmount(formatted: String): Double {
    return formatted.replace("¥", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
}
