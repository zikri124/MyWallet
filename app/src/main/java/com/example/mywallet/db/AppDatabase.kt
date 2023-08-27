package com.example.mywallet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mywallet.db.dao.CategoryDao
import com.example.mywallet.db.dao.TransactionDao
import com.example.mywallet.db.entity.CategoryEntity
import com.example.mywallet.db.entity.TransactionEntity

@Database(entities = [CategoryEntity::class, TransactionEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "mywallet-db"
            )
                .createFromAsset("database/mywallet-db.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}