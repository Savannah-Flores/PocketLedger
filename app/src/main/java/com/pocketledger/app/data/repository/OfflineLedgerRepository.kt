package com.pocketledger.app.data.repository

import com.pocketledger.app.data.local.TransactionDao
import com.pocketledger.app.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

class OfflineLedgerRepository(
    private val transactionDao: TransactionDao,
) : LedgerRepository {
    override fun observeTransactions(): Flow<List<TransactionEntity>> = transactionDao.observeAll()

    override suspend fun getTransaction(id: Long): TransactionEntity? = transactionDao.getById(id)

    override suspend fun addTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insert(transaction)
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.update(transaction)
    }

    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }
}
