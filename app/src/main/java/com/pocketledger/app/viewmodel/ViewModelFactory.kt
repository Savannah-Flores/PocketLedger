package com.pocketledger.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.pocketledger.app.PocketLedgerApplication
import com.pocketledger.app.data.repository.LedgerRepository

fun CreationExtras.ledgerRepository(): LedgerRepository {
    val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PocketLedgerApplication
    return application.container.ledgerRepository
}
