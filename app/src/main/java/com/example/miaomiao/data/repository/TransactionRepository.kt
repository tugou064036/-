package com.example.miaomiao.data.repository

import com.example.miaomiao.data.Transaction
import com.example.miaomiao.data.TransactionType
import com.example.miaomiao.data.TransactionCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TransactionRepository {
    // 按用户的交易操作
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transactionId: String)
    suspend fun updateTransaction(transaction: Transaction)
    
    // 按用户查询交易
    suspend fun getTransactionsByUser(userId: String): List<Transaction>
    suspend fun getTransactionsByUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<Transaction>
    suspend fun getTransactionsByUserAndType(userId: String, type: TransactionType): List<Transaction>
    suspend fun getTransactionsByUserAndCategory(userId: String, category: TransactionCategory): List<Transaction>
    fun getTransactionsByUserFlow(userId: String): Flow<List<Transaction>>
    
    // 按用户的统计数据
    suspend fun getMonthlyIncomeByUser(userId: String, year: Int, month: Int): Double
    suspend fun getMonthlyExpenseByUser(userId: String, year: Int, month: Int): Double
    suspend fun getTotalBalanceByUser(userId: String): Double
    
    // 保留原有的全局方法（向后兼容）
    suspend fun getAllTransactions(): List<Transaction>
    suspend fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): List<Transaction>
    suspend fun getTransactionsByType(type: TransactionType): List<Transaction>
    suspend fun getTransactionsByCategory(category: TransactionCategory): List<Transaction>
    fun getTransactionsFlow(): Flow<List<Transaction>>
    suspend fun getMonthlyIncome(year: Int, month: Int): Double
    suspend fun getMonthlyExpense(year: Int, month: Int): Double
    suspend fun getTotalBalance(): Double
}