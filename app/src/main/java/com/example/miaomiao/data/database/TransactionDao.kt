package com.example.miaomiao.data.database

import androidx.room.*
import com.example.miaomiao.data.Transaction
import com.example.miaomiao.data.TransactionType
import com.example.miaomiao.data.TransactionCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // 按用户查询所有交易
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getTransactionsByUserFlow(userId: String): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    suspend fun getTransactionsByUser(userId: String): List<Transaction>
    
    // 按用户和日期范围查询
    @Query("SELECT * FROM transactions WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getTransactionsByUserAndDateRange(userId: String, startDate: String, endDate: String): List<Transaction>
    
    // 按用户和类型查询
    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY date DESC")
    suspend fun getTransactionsByUserAndType(userId: String, type: TransactionType): List<Transaction>
    
    // 按用户和分类查询
    @Query("SELECT * FROM transactions WHERE userId = :userId AND category = :category ORDER BY date DESC")
    suspend fun getTransactionsByUserAndCategory(userId: String, category: TransactionCategory): List<Transaction>
    
    // 按用户查询月收入
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE userId = :userId AND type = 'INCOME' AND strftime('%Y-%m', date) = :yearMonth")
    suspend fun getMonthlyIncomeByUser(userId: String, yearMonth: String): Double
    
    // 按用户查询月支出
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE userId = :userId AND type = 'EXPENSE' AND strftime('%Y-%m', date) = :yearMonth")
    suspend fun getMonthlyExpenseByUser(userId: String, yearMonth: String): Double
    
    // 按用户查询总余额
    @Query("SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0) FROM transactions WHERE userId = :userId")
    suspend fun getTotalBalanceByUser(userId: String): Double
    
    // 保留原有的全局查询方法（用于管理员或调试）
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsFlow(): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactions(): List<Transaction>
    
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getTransactionsByDateRange(startDate: String, endDate: String): List<Transaction>
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    suspend fun getTransactionsByType(type: TransactionType): List<Transaction>
    
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    suspend fun getTransactionsByCategory(category: TransactionCategory): List<Transaction>
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME' AND strftime('%Y-%m', date) = :yearMonth")
    suspend fun getMonthlyIncome(yearMonth: String): Double
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE' AND strftime('%Y-%m', date) = :yearMonth")
    suspend fun getMonthlyExpense(yearMonth: String): Double
    
    @Query("SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0) FROM transactions")
    suspend fun getTotalBalance(): Double
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)
    
    @Update
    suspend fun updateTransaction(transaction: Transaction)
    
    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransaction(transactionId: String)
    
    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteTransactionsByUser(userId: String)
    
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}