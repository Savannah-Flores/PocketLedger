package com.pocketledger.app.data

import android.content.Context
import com.pocketledger.app.data.local.PocketLedgerDatabase
import com.pocketledger.app.data.repository.LedgerRepository
import com.pocketledger.app.data.repository.OfflineLedgerRepository

interface AppContainer {
    val ledgerRepository: LedgerRepository
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    private val database = PocketLedgerDatabase.getInstance(context)

    override val ledgerRepository: LedgerRepository by lazy {
        OfflineLedgerRepository(database.transactionDao())
    }
}
