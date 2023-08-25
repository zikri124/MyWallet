package com.example.mywallet.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "categoryId")
    val categoryId: Int?,
    @ColumnInfo(name = "userId")
    val userId: String?,
    @ColumnInfo(name = "transactionType")
    val transactionType: Int?,
    @ColumnInfo(name = "amount")
    val amount: String?,
    @ColumnInfo(name = "date")
    val date: String?,
    @ColumnInfo(name = "time")
    val time: String?,
    @ColumnInfo(name = "note")
    val note: String?
)