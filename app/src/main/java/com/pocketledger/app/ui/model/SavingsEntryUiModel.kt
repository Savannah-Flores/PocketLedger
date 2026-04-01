package com.pocketledger.app.ui.model

data class SavingsEntryUiModel(
    val id: String,
    val title: String,
    val amount: String,
    val source: String,
    val time: String,
    val sortTimestamp: Long,
    val editable: Boolean,
    val manualDepositId: Long? = null,
)
