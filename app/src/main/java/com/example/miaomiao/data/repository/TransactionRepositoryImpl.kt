package com.example.miaomiao.data.repository

import com.example.miaomiao.data.Transaction
import com.example.miaomiao.data.TransactionType
import com.example.miaomiao.data.TransactionCategory
import com.example.miaomiao.data.database.TransactionDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionRepositoryImpl(private val transactionDao: TransactionDao) : TransactionRepository {
    
    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }
    
    override suspend fun deleteTransaction(transactionId: String) {
        transactionDao.deleteTransaction(transactionId)
    }
    
    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }
    
    // 按用户查询交易
    override suspend fun getTransactionsByUser(userId: String): List<Transaction> {
        return transactionDao.getTransactionsByUser(userId)
    }
    
    override suspend fun getTransactionsByUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<Transaction> {
        val startDateStr = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDateStr = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return transactionDao.getTransactionsByUserAndDateRange(userId, startDateStr, endDateStr)
    }
    
    override suspend fun getTransactionsByUserAndType(userId: String, type: TransactionType): List<Transaction> {
        return transactionDao.getTransactionsByUserAndType(userId, type)
    }
    
    override suspend fun getTransactionsByUserAndCategory(userId: String, category: TransactionCategory): List<Transaction> {
        return transactionDao.getTransactionsByUserAndCategory(userId, category)
    }
    
    override fun getTransactionsByUserFlow(userId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByUserFlow(userId)
    }
    
    // 按用户的统计数据
    override suspend fun getMonthlyIncomeByUser(userId: String, year: Int, month: Int): Double {
        val yearMonth = String.format("%04d-%02d", year, month)
        return transactionDao.getMonthlyIncomeByUser(userId, yearMonth)
    }
    
    override suspend fun getMonthlyExpenseByUser(userId: String, year: Int, month: Int): Double {
        val yearMonth = String.format("%04d-%02d", year, month)
        return transactionDao.getMonthlyExpenseByUser(userId, yearMonth)
    }
    
    override suspend fun getTotalBalanceByUser(userId: String): Double {
        return transactionDao.getTotalBalanceByUser(userId)
    }
    
    // 保留原有的全局方法（向后兼容）
    override suspend fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactions()
    }
    
    override suspend fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): List<Transaction> {
        val startDateStr = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDateStr = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return transactionDao.getTransactionsByDateRange(startDateStr, endDateStr)
    }
    
    override suspend fun getTransactionsByType(type: TransactionType): List<Transaction> {
        return transactionDao.getTransactionsByType(type)
    }
    
    override suspend fun getTransactionsByCategory(category: TransactionCategory): List<Transaction> {
        return transactionDao.getTransactionsByCategory(category)
    }
    
    override fun getTransactionsFlow(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsFlow()
    }
    
    override suspend fun getMonthlyIncome(year: Int, month: Int): Double {
        val yearMonth = String.format("%04d-%02d", year, month)
        return transactionDao.getMonthlyIncome(yearMonth)
    }
    
    override suspend fun getMonthlyExpense(year: Int, month: Int): Double {
        val yearMonth = String.format("%04d-%02d", year, month)
        return transactionDao.getMonthlyExpense(yearMonth)
    }
    
    override suspend fun getTotalBalance(): Double {
        return transactionDao.getTotalBalance()
    }
}