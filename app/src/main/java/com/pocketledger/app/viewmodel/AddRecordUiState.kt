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
    val isCategoryManual: Boolean = false,
    val date: String = "",
    val time: String = "",
    val saveButtonLabel: String = "保存",
)

