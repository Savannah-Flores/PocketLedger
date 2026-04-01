package com.pocketledger.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDepositDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deposit: SavingsDepositEntity): Long

    @Update
    suspend fun update(deposit: SavingsDepositEntity)

    @Delete
    suspend fun delete(deposit: SavingsDepositEntity)

    @Query("SELECT * FROM savings_deposits WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SavingsDepositEntity?

    @Query("SELECT * FROM savings_deposits ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<SavingsDepositEntity>>
}
