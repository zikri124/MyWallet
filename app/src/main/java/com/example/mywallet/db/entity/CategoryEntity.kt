package com.example.mywallet.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "type")
    val type: String?
)

data class CategoryAndTransaction(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "categoryId"
    )
    val transaction: List<TransactionEntity>
)