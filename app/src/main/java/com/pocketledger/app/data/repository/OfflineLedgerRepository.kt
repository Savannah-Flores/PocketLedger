package com.pocketledger.app.data.repository

import com.pocketledger.app.data.local.SavingsDepositDao
import com.pocketledger.app.data.local.SavingsDepositEntity
import com.pocketledger.app.data.local.TransactionDao
import com.pocketledger.app.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

class OfflineLedgerRepository(
    private val transactionDao: TransactionDao,
    private val savingsDepositDao: SavingsDepositDao,
) : LedgerRepository {
    override fun observeTransactions(): Flow<List<TransactionEntity>> = transactionDao.observeAll()

    override fun observeSavingsDeposits(): Flow<List<SavingsDepositEntity>> = savingsDepositDao.observeAll()

    override suspend fun getTransaction(id: Long): TransactionEntity? = transactionDao.getById(id)

    override suspend fun getSavingsDeposit(id: Long): SavingsDepositEntity? = savingsDepositDao.getById(id)

    override suspend fun addTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insert(transaction)
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.update(transaction)
    }

    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }

    override suspend fun addSavingsDeposit(deposit: SavingsDepositEntity): Long {
        return savingsDepositDao.insert(deposit)
    }

    override suspend fun updateSavingsDeposit(deposit: SavingsDepositEntity) {
        savingsDepositDao.update(deposit)
    }

    override suspend fun deleteSavingsDeposit(deposit: SavingsDepositEntity) {
        savingsDepositDao.delete(deposit)
    }
}
