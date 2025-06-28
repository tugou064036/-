package com.example.miaomiao

import android.app.Application
import com.example.miaomiao.data.database.AppDatabase
import com.example.miaomiao.data.repository.TransactionRepository
import com.example.miaomiao.data.repository.TransactionRepositoryImpl
import com.example.miaomiao.data.repository.UserRepository
import com.example.miaomiao.data.repository.UserRepositoryImpl
import com.example.miaomiao.service.NetworkTimeService

class MiaomiaoApplication : Application() {
    
    val database by lazy { AppDatabase.getDatabase(this) }
    val transactionRepository: TransactionRepository by lazy { TransactionRepositoryImpl(database.transactionDao()) }
    val userRepository: UserRepository by lazy { UserRepositoryImpl(database.userDao()) }
    
    val networkTimeService by lazy {
        NetworkTimeService(this)
    }
}