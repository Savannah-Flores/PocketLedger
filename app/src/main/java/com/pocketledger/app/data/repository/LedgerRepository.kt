package com.pocketledger.app.data.repository

import com.pocketledger.app.data.local.SavingsDepositEntity
import com.pocketledger.app.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface LedgerRepository {
    fun observeTransactions(): Flow<List<TransactionEntity>>

    fun observeSavingsDeposits(): Flow<List<SavingsDepositEntity>>

    suspend fun getTransaction(id: Long): TransactionEntity?

    suspend fun addTransaction(transaction: TransactionEntity): Long

    suspend fun updateTransaction(transaction: TransactionEntity)

    suspend fun deleteTransaction(transaction: TransactionEntity)

    suspend fun addSavingsDeposit(deposit: SavingsDepositEntity): Long
}
