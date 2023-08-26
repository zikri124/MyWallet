package com.example.mywallet.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mywallet.db.entity.TransactionAndCategory
import com.example.mywallet.db.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTransaction(vararg transaction: TransactionEntity)

    @Update
    fun updateTransaction(vararg transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    fun deleteTransaction(vararg transactionId: String?)

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC, time DESC")
    fun getTransactionAndCategory(): List<TransactionAndCategory>

    @Transaction
    @Query("SELECT * FROM transactions WHERE date > :date ORDER BY date DESC, time DESC")
    fun getTransactionAndCategoryFromAnyDate(date: String?): List<TransactionAndCategory>
}