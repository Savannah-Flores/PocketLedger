package com.pocketledger.app.viewmodel

enum class RecordType(val label: String) {
    Expense("支出"),
    Refund("退款"),
    Income("收入"),
}

data class AddRecordUiState(
    val content: String = "",
    val amount: String = "0",
    val selectedType: RecordType = RecordType.Expense,
    val category: String = "其他",
    val date: String = "2026-04-01",
    val time: String = "08:35",
)
