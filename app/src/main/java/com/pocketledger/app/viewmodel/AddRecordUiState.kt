package com.pocketledger.app.viewmodel

enum class RecordType(val label: String, val storageValue: String) {
    Expense("支出", "expense"),
    Refund("退款", "refund"),
    Income("收入", "income");

    companion object {
        fun fromStorage(value: String): RecordType {
            return entries.firstOrNull { it.storageValue == value } ?: Expense
        }
    }
}

data class AddRecordUiState(
    val recordId: Long? = null,
    val title: String = "记一笔",
    val content: String = "",
    val amount: String = "0",
    val selectedType: RecordType = RecordType.Expense,
    val category: String = "其他",
    val date: String = "2026-04-01",
    val time: String = "08:35",
    val saveButtonLabel: String = "保存",
)
