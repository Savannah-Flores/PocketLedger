package com.pocketledger.app.ui.model

import com.pocketledger.app.viewmodel.RecordType

data class RecordItemUiModel(
    val content: String,
    val amount: String,
    val type: RecordType,
    val category: String,
    val time: String,
)
