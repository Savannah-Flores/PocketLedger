package com.pocketledger.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class PocketLedgerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: PocketLedgerDatabase? = null

        fun getInstance(context: Context): PocketLedgerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PocketLedgerDatabase::class.java,
                    "pocket_ledger.db",
                ).build().also { INSTANCE = it }
            }
        }
    }
}
