package com.pocketledger.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDepositDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deposit: SavingsDepositEntity): Long

    @Query("SELECT * FROM savings_deposits ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<SavingsDepositEntity>>
}
