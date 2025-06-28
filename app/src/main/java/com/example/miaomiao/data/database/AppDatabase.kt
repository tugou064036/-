package com.example.miaomiao.data.database

import android.content.Context
import androidx.room.*
import com.example.miaomiao.data.Transaction
import com.example.miaomiao.data.User
import com.example.miaomiao.data.Converters
import com.example.miaomiao.data.database.TransactionDao
import com.example.miaomiao.data.dao.UserDao

@Database(
    entities = [Transaction::class, User::class],
    version = 3, // 增加版本号
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "miaomiao_database"
                )
                .fallbackToDestructiveMigration() // 简单处理版本升级
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}