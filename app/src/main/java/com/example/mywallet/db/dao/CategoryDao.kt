package com.example.mywallet.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mywallet.db.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun loadAllCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCategory(vararg category: CategoryEntity)

    @Update
    fun updateCategory(vararg category: CategoryEntity)

    @Delete
    fun deleteCategory(vararg category: CategoryEntity)
}