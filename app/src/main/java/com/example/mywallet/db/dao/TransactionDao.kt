package com.example.mywallet.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mywallet.db.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    fun loadAllTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE date > :date")
    fun loadTransactionFrom(date: String): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTransaction(vararg transaction: TransactionEntity)

    @Update
    fun updateTransaction(vararg transaction: TransactionEntity)

    @Delete
    fun deleteTransaction(vararg transaction: TransactionEntity)
}