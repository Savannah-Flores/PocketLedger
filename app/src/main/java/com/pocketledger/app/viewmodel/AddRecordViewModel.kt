package com.pocketledger.app.viewmodel

import androidx.lifecycle.ViewModel
import com.pocketledger.app.utils.inferCategory
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddRecordViewModel : ViewModel() {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private val _uiState = MutableStateFlow(
        AddRecordUiState(
            date = LocalDate.now().format(dateFormatter),
            time = LocalTime.now().format(timeFormatter),
        ),
    )
    val uiState: StateFlow<AddRecordUiState> = _uiState.asStateFlow()

    fun updateContent(value: String) {
        _uiState.update { current ->
            current.copy(
                content = value,
                category = inferCategory(value),
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

    fun buildSaveSummary(): String {
        val state = _uiState.value
        return "内容：${state.content.ifBlank { "未填写" }}，金额：${state.amount}，类型：${state.selectedType.label}，分类：${state.category}，日期：${state.date}，时间：${state.time}"
    }

    private fun buildAmount(current: String, input: String): String {
        if (input == "." && current.contains('.')) return current
        if (current == "0" && input != ".") return input
        if (current == "0" && input == ".") return "0."
        return current + input
    }
}
