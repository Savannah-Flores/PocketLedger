package com.pocketledger.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pocketledger.app.data.local.TransactionEntity
import com.pocketledger.app.data.repository.LedgerRepository
import com.pocketledger.app.utils.buildTimestamp
import com.pocketledger.app.utils.inferCategory
import com.pocketledger.app.utils.timestampToDate
import com.pocketledger.app.utils.timestampToTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddRecordViewModel(
    private val ledgerRepository: LedgerRepository,
    private val recordId: Long? = null,
) : ViewModel() {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private val _uiState = MutableStateFlow(
        AddRecordUiState(
            recordId = recordId,
            title = if (recordId == null) "记一笔" else "修改记录",
            date = LocalDate.now().format(dateFormatter),
            time = LocalTime.now().format(timeFormatter),
            saveButtonLabel = if (recordId == null) "保存" else "更新",
        ),
    )
    val uiState: StateFlow<AddRecordUiState> = _uiState.asStateFlow()

    init {
        if (recordId != null) {
            viewModelScope.launch {
                ledgerRepository.getTransaction(recordId)?.let { transaction ->
                    _uiState.value = AddRecordUiState(
                        recordId = transaction.id,
                        title = "修改记录",
                        content = transaction.title,
                        amount = formatAmount(transaction.amount),
                        selectedType = RecordType.fromStorage(transaction.type),
                        category = transaction.category,
                        isCategoryManual = true,
                        date = timestampToDate(transaction.timestamp),
                        time = timestampToTime(transaction.timestamp),
                        saveButtonLabel = "更新",
                    )
                }
            }
        }
    }

    fun updateContent(value: String) {
        _uiState.update { current ->
            val autoCategory = inferCategory(value)
            current.copy(
                content = value,
                category = if (current.isCategoryManual) current.category else autoCategory,
            )
        }
    }

    fun updateDate(date: String) {
        _uiState.update { it.copy(date = date) }
    }

    fun updateTime(time: String) {
        _uiState.update { it.copy(time = time) }
    }

    fun selectCategory(category: String) {
        _uiState.update {
            it.copy(
                category = category,
                isCategoryManual = true,
            )
        }
    }

    fun resetCategoryToAuto() {
        _uiState.update {
            it.copy(
                category = inferCategory(it.content),
                isCategoryManual = false,
            )
        }
    }

    fun selectType(type: RecordType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun appendNumber(input: String) {
        _uiState.update { current ->
            current.copy(amount = buildAmount(current.amount, input))
        }
    }

    fun deleteLast() {
        _uiState.update { current ->
            val next = current.amount.dropLast(1)
            current.copy(amount = if (next.isBlank() || next == "-") "0" else next)
        }
    }

    fun saveRecord(onSaved: (String) -> Unit) {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull() ?: 0.0
        val entity = TransactionEntity(
            id = state.recordId ?: 0L,
            title = state.content.ifBlank { "未命名记录" },
            amount = amount,
            category = state.category,
            timestamp = buildTimestamp(state.date, state.time),
            type = state.selectedType.storageValue,
        )

        viewModelScope.launch {
            if (state.recordId == null) {
                ledgerRepository.addTransaction(entity)
            } else {
                ledgerRepository.updateTransaction(entity)
            }
            onSaved(buildSaveSummary())
        }
    }

    private fun buildSaveSummary(): String {
        val state = _uiState.value
        val action = if (state.recordId == null) "已保存" else "已更新"
        return "$action：${state.content.ifBlank { "未填写" }}，金额 ${state.amount}，类型 ${state.selectedType.label}，分类 ${state.category}"
    }

    private fun buildAmount(current: String, input: String): String {
        if (input == "." && current.contains('.')) return current
        if (current == "0" && input != ".") return input
        if (current == "0" && input == ".") return "0."
        return current + input
    }

    private fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) amount.toInt().toString() else amount.toString()
    }

    companion object {
        fun factory(recordId: Long? = null): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AddRecordViewModel(
                    ledgerRepository = this.ledgerRepository(),
                    recordId = recordId,
                )
            }
        }
    }
}
