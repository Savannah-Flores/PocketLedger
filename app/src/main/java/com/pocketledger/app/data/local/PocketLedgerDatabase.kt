package com.pocketledger.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class PocketLedgerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}

