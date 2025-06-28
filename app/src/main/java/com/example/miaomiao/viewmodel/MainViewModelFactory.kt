package com.example.miaomiao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.miaomiao.data.repository.TransactionRepository
import com.example.miaomiao.data.repository.UserRepository
import com.example.miaomiao.service.NetworkTimeService

class MainViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val networkTimeService: NetworkTimeService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(transactionRepository, userRepository, networkTimeService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}