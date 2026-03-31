package com.pocketledger.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pocketledger.app.data.repository.LedgerRepository
import com.pocketledger.app.ui.model.RecordItemUiModel
import com.pocketledger.app.utils.toRecordItemUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RecordUiState(
    val records: List<RecordItemUiModel> = emptyList(),
)

class RecordViewModel(
    private val ledgerRepository: LedgerRepository,
) : ViewModel() {
    val uiState: StateFlow<RecordUiState> = ledgerRepository.observeTransactions()
        .map { transactions ->
            RecordUiState(records = transactions.map { it.toRecordItemUiModel() })
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecordUiState(),
        )

    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            ledgerRepository.getTransaction(recordId)?.let { transaction ->
                ledgerRepository.deleteTransaction(transaction)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RecordViewModel(
                    ledgerRepository = this.ledgerRepository(),
                )
            }
        }
    }
}
