package com.pocketledger.app.data.repository

import com.pocketledger.app.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface LedgerRepository {
    fun observeTransactions(): Flow<List<TransactionEntity>>

    suspend fun getTransaction(id: Long): TransactionEntity?

    suspend fun addTransaction(transaction: TransactionEntity): Long

    suspend fun updateTransaction(transaction: TransactionEntity)

    suspend fun deleteTransaction(transaction: TransactionEntity)
}
