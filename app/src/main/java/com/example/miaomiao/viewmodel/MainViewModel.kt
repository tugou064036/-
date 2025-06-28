package com.example.miaomiao.viewmodel
import kotlinx.coroutines.flow.flow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.miaomiao.data.Transaction
import com.example.miaomiao.data.TransactionType
import com.example.miaomiao.data.TransactionCategory
import com.example.miaomiao.data.User
import com.example.miaomiao.data.repository.TransactionRepository
import com.example.miaomiao.data.repository.UserRepository
import com.example.miaomiao.service.NetworkTimeService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalDate
import android.content.Context
import android.os.Environment
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val networkTimeService: NetworkTimeService
) : ViewModel() {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // 添加缺失的 _successMessage 声明
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    // 当前用户的交易记录流
    val transactions: StateFlow<List<Transaction>> = combine(
        _currentUser,
        _isLoggedIn
    ) { user, isLoggedIn ->
        if (isLoggedIn && user != null) {
            transactionRepository.getTransactionsByUserFlow(user.id)
        } else {
            flowOf(emptyList())
        }
    }.flatMapLatest { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // 当前用户的统计数据
    val monthlyIncome: StateFlow<Double> = combine(
        transactions,
        _currentUser
    ) { transactions, user ->
        if (user != null) {
            val currentMonth = LocalDateTime.now().monthValue
            val currentYear = LocalDateTime.now().year
            transactions.filter { 
                it.type == TransactionType.INCOME && 
                it.date.monthValue == currentMonth &&
                it.date.year == currentYear
            }.sumOf { it.amount }
        } else {
            0.0
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )
    
    val monthlyExpense: StateFlow<Double> = combine(
        transactions,
        _currentUser
    ) { transactions, user ->
        if (user != null) {
            val currentMonth = LocalDateTime.now().monthValue
            val currentYear = LocalDateTime.now().year
            transactions.filter { 
                it.type == TransactionType.EXPENSE && 
                it.date.monthValue == currentMonth &&
                it.date.year == currentYear
            }.sumOf { it.amount }
        } else {
            0.0
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )
    
    val monthlyBalance: StateFlow<Double> = combine(monthlyIncome, monthlyExpense) { income, expense ->
        income - expense
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )
    
    // 分类统计
    val categoryExpenses: StateFlow<List<Pair<TransactionCategory, Double>>> = combine(
        transactions,
        _currentUser
    ) { transactions, user ->
        if (user != null) {
            val currentMonth = LocalDateTime.now().monthValue
            val currentYear = LocalDateTime.now().year
            transactions
                .filter { 
                    it.type == TransactionType.EXPENSE && 
                    it.date.monthValue == currentMonth &&
                    it.date.year == currentYear
                }
                .groupBy { it.category }
                .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }
        } else {
            emptyList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // 最近交易
    val recentTransactions: StateFlow<List<Transaction>> = transactions
        .map { it.take(10) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // 登录方法
    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = userRepository.loginUser(phone, password)
                if (result.isSuccess) {
                    _currentUser.value = result.getOrNull()
                    _isLoggedIn.value = true
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "登录失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "登录过程中发生错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()
    
    // 注册方法
    fun register(phone: String, password: String, username: String = "用户") {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _registerSuccess.value = false
            
            try {
                val result = userRepository.registerUser(phone, password, username)
                if (result.isSuccess) {
                    _registerSuccess.value = true
                    _errorMessage.value = "注册成功"
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "注册失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "注册过程中发生错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 清除注册成功状态
    fun clearRegisterSuccess() {
        _registerSuccess.value = false
        _errorMessage.value = null
    }
    
    // 设置错误消息
    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
    
    // 清除错误消息
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    // 登出方法
    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        _errorMessage.value = null
    }
    
    // 交易相关方法
    // 添加总余额计算
    val totalBalance: StateFlow<Double> = combine(
        transactions,
        _currentUser
    ) { transactions, user ->
        if (user != null) {
            val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            totalIncome - totalExpense
        } else {
            0.0
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )
    
    // 添加更新用户统计数据的方法
    private fun updateUserStatistics() {
        viewModelScope.launch {
            val currentUser = _currentUser.value
            if (currentUser != null) {
                try {
                    val userTransactions = transactionRepository.getTransactionsByUser(currentUser.id)
                    
                    // 计算记账天数（有交易记录的不同日期数）
                    val uniqueDays = userTransactions.map { it.date.toLocalDate() }.distinct().size
                    
                    // 计算总笔数
                    val totalTransactions = userTransactions.size
                    
                    // 更新用户数据
                    val updatedUser = currentUser.copy(
                        totalDays = uniqueDays,
                        totalTransactions = totalTransactions
                    )
                    
                    userRepository.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                } catch (e: Exception) {
                    // 静默处理统计更新错误，不影响主要功能
                }
            }
        }
    }
    
    // 修改 addTransaction 方法，添加统计数据更新
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val currentUser = _currentUser.value
            if (currentUser != null) {
                val userTransaction = transaction.copy(userId = currentUser.id)
                transactionRepository.insertTransaction(userTransaction)
                // 添加交易后更新统计数据
                updateUserStatistics()
            } else {
                _errorMessage.value = "请先登录"
            }
        }
    }
    
    // 修改 deleteTransaction 方法，添加统计数据更新
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction.id)
            // 删除交易后更新统计数据
            updateUserStatistics()
        }
    }
    
    // 修改 updateTransaction 方法，添加统计数据更新
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
            // 更新交易后更新统计数据
            updateUserStatistics()
        }
    }
    
    // 添加手动刷新统计数据的方法
    fun refreshUserStatistics() {
        updateUserStatistics()
    }
    
    // 添加公开的获取当前时间方法
    suspend fun getCurrentTime(): LocalDateTime {
        return if (networkTimeService.isUsingNetworkTime()) {
            networkTimeService.getCurrentTime()
        } else {
            LocalDateTime.now()
        }
    }
    
    // 添加获取时间状态的方法
    fun getTimeStatus(): String {
        return if (networkTimeService.isUsingNetworkTime()) {
            "使用网络时间"
        } else {
            "使用本地时间"
        }
    }
    
    // 删除这些重复的方法定义
    // fun deleteTransaction(transaction: Transaction) { ... }
    // fun updateTransaction(transaction: Transaction) { ... }
    
    // 修复返回类型不匹配的方法
    fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> {
        return flow {
            emit(transactionRepository.getTransactionsByDateRange(startDate, endDate))
        }
    }
    
    fun getTransactionsByCategory(category: TransactionCategory): Flow<List<Transaction>> {
        return flow {
            emit(transactionRepository.getTransactionsByCategory(category))
        }
    }
    
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return flow {
            emit(transactionRepository.getTransactionsByType(type))
        }
    }

    // 兼容性方法（保持与现有UI的兼容）
    fun getMonthlyIncome(): Double = monthlyIncome.value
    fun getMonthlyExpense(): Double = monthlyExpense.value
    fun getMonthlyBalance(): Double = monthlyBalance.value
    fun getRecentTransactions(limit: Int = 10): List<Transaction> = recentTransactions.value
    
    // 用户信息更新方法
    // 修改现有的 updateUserPassword 方法
    fun updateUserPassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            val currentUser = _currentUser.value
            if (currentUser != null) {
                try {
                    // 验证旧密码
                    val hashedOldPassword = hashPassword(oldPassword)
                    if (currentUser.password != hashedOldPassword) {
                        _errorMessage.value = "原密码错误"
                        return@launch
                    }
                    
                    // 验证新密码长度
                    if (newPassword.length < 6) {
                        _errorMessage.value = "新密码长度不能少于6位"
                        return@launch
                    }
                    
                    val hashedNewPassword = hashPassword(newPassword)
                    val updatedUser = currentUser.copy(password = hashedNewPassword)
                    userRepository.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                    _successMessage.value = "密码修改成功"
                } catch (e: Exception) {
                    _errorMessage.value = "密码修改失败：${e.message}"
                }
            } else {
                _errorMessage.value = "请先登录"
            }
        }
    }
    
    fun updateUserAvatar(newAvatar: String) {
        viewModelScope.launch {
            val currentUser = _currentUser.value
            if (currentUser != null) {
                try {
                    val updatedUser = currentUser.copy(avatar = newAvatar)
                    userRepository.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                    _successMessage.value = "头像修改成功"
                } catch (e: Exception) {
                    _errorMessage.value = "头像修改失败：${e.message}"
                }
            } else {
                _errorMessage.value = "请先登录"
            }
        }
    }
    
    fun updateUserInfo(newUsername: String, newAvatar: String) {
        viewModelScope.launch {
            val currentUser = _currentUser.value
            if (currentUser != null) {
                try {
                    val updatedUser = currentUser.copy(
                        username = newUsername,
                        avatar = newAvatar
                    )
                    userRepository.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                    _successMessage.value = "个人信息修改成功"
                } catch (e: Exception) {
                    _errorMessage.value = "个人信息修改失败：${e.message}"
                }
            } else {
                _errorMessage.value = "请先登录"
            }
        }
    }
    
    private fun hashPassword(password: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.fold("") { str, it -> str + "%02x".format(it) }
    }
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
    
    // 导出Excel功能 - 支持用户选择目录
    suspend fun exportTransactionsToExcel(
        context: Context,
        directoryUri: Uri,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val currentUser = _currentUser.value ?: return@withContext Result.failure(Exception("用户未登录"))
            
            // 验证URI权限
            try {
                context.contentResolver.takePersistableUriPermission(
                    directoryUri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                return@withContext Result.failure(Exception("目录访问权限不足：${e.message}"))
            }
            
            // 过滤交易数据
            val filteredTransactions = if (startDate != null && endDate != null) {
                transactions.value.filter { transaction ->
                    val transactionDate = transaction.date.toLocalDate()
                    !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate)
                }
            } else {
                transactions.value
            }
            
            if (filteredTransactions.isEmpty()) {
                return@withContext Result.failure(Exception("没有找到符合条件的交易记录"))
            }
            
            // 创建Excel工作簿
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("交易记录")
            
            // 创建标题行
            val headers = arrayOf("日期", "时间", "类型", "分类", "金额", "描述", "表情")
            val headerRow = sheet.createRow(0)
            
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                
                // 设置标题样式
                val headerStyle = workbook.createCellStyle()
                val font = workbook.createFont()
                font.bold = true
                headerStyle.setFont(font)
                cell.cellStyle = headerStyle
            }
            
            // 填充数据行
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            
            filteredTransactions.forEachIndexed { index, transaction ->
                val row = sheet.createRow(index + 1)
                
                row.createCell(0).setCellValue(transaction.date.format(dateFormatter))
                row.createCell(1).setCellValue(transaction.date.format(timeFormatter))
                row.createCell(2).setCellValue(if (transaction.type == TransactionType.INCOME) "收入" else "支出")
                row.createCell(3).setCellValue(getCategoryDisplayName(transaction.category))
                row.createCell(4).setCellValue(transaction.amount.toString())
                row.createCell(5).setCellValue(transaction.description ?: "")
                row.createCell(6).setCellValue(transaction.emoji ?: "")
            }
            
            // 在第二个 exportTransactionsToExcel 方法中（大约第615-617行）
            // 移除以下代码：
            // for (i in headers.indices) {
            //     sheet.autoSizeColumn(i)
            // }
            
            // 添加手动设置列宽的代码
            sheet.setColumnWidth(0, 3000)  // 日期列
            sheet.setColumnWidth(1, 2500)  // 时间列
            sheet.setColumnWidth(2, 2000)  // 类型列
            sheet.setColumnWidth(3, 3000)  // 分类列
            sheet.setColumnWidth(4, 3000)  // 金额列
            sheet.setColumnWidth(5, 5000)  // 描述列
            sheet.setColumnWidth(6, 1500)  // 表情列
            
            // 创建文件名
            val fileName = "喵喵记账_${currentUser.username}_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.xlsx"
            
            // 使用DocumentFile API保存到用户选择的目录
            val documentFile = DocumentFile.fromTreeUri(context, directoryUri)
                ?: return@withContext Result.failure(Exception("无法访问选择的目录"))
                
            if (!documentFile.exists() || !documentFile.isDirectory) {
                return@withContext Result.failure(Exception("选择的目录不存在或不是有效目录"))
            }
            
            val newFile = documentFile.createFile("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fileName)
                ?: return@withContext Result.failure(Exception("无法在目录中创建文件"))
            
            try {
                context.contentResolver.openOutputStream(newFile.uri)?.use { outputStream ->
                    workbook.write(outputStream)
                    outputStream.flush()
                } ?: return@withContext Result.failure(Exception("无法打开文件输出流"))
            } catch (e: Exception) {
                return@withContext Result.failure(Exception("写入文件失败：${e.message}"))
            } finally {
                workbook.close()
            }
            
            Result.success("文件已保存：$fileName")
        } catch (e: Exception) {
            Result.failure(Exception("导出失败：${e.message}"))
        }
    }
    
    // 保留原有方法作为兼容（可选）
    suspend fun exportTransactionsToExcel(
        context: Context,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val currentUser = _currentUser.value ?: return@withContext Result.failure(Exception("用户未登录"))
            
            // 过滤交易数据
            val filteredTransactions = if (startDate != null && endDate != null) {
                transactions.value.filter { transaction ->
                    val transactionDate = transaction.date.toLocalDate()
                    !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate)
                }
            } else {
                transactions.value
            }
            
            if (filteredTransactions.isEmpty()) {
                return@withContext Result.failure(Exception("没有找到符合条件的交易记录"))
            }
            
            // 创建Excel工作簿
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("交易记录")
            
            // 创建标题行
            val headers = arrayOf("日期", "时间", "类型", "分类", "金额", "描述", "表情")
            val headerRow = sheet.createRow(0)
            
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                
                // 设置标题样式
                val headerStyle = workbook.createCellStyle()
                val font = workbook.createFont()
                font.bold = true
                headerStyle.setFont(font)
                cell.cellStyle = headerStyle
            }
            
            // 填充数据行
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            
            filteredTransactions.forEachIndexed { index, transaction ->
                val row = sheet.createRow(index + 1)
                
                row.createCell(0).setCellValue(transaction.date.format(dateFormatter))
                row.createCell(1).setCellValue(transaction.date.format(timeFormatter))
                row.createCell(2).setCellValue(if (transaction.type == TransactionType.INCOME) "收入" else "支出")
                row.createCell(3).setCellValue(getCategoryDisplayName(transaction.category))
                row.createCell(4).setCellValue(transaction.amount.toString())
                row.createCell(5).setCellValue(transaction.description ?: "")
                row.createCell(6).setCellValue(transaction.emoji ?: "")
            }
            // 在第一个 exportTransactionsToExcel 方法中（大约第514-516行）
// 移除以下代码：
// for (i in headers.indices) {
//     sheet.autoSizeColumn(i)
// }

// 保留手动设置列宽的代码
sheet.setColumnWidth(0, 3000)  // 日期列
sheet.setColumnWidth(1, 2500)  // 时间列
sheet.setColumnWidth(2, 2000)  // 类型列
sheet.setColumnWidth(3, 3000)  // 分类列
sheet.setColumnWidth(4, 3000)  // 金额列
sheet.setColumnWidth(5, 5000)  // 描述列
sheet.setColumnWidth(6, 1500)  // 表情列
            
            // 创建文件名
            val fileName = "喵喵记账_${currentUser.username}_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.xlsx"
            
            // 使用应用私有目录作为默认保存位置
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            if (downloadsDir != null && !downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val file = File(downloadsDir, fileName)
            
            // 写入文件
            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }
            workbook.close()
            
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getCategoryDisplayName(category: TransactionCategory): String {
        return when (category) {
            TransactionCategory.FOOD -> "餐饮"
            TransactionCategory.TRANSPORT -> "交通"
            TransactionCategory.SHOPPING -> "购物"
            TransactionCategory.ENTERTAINMENT -> "娱乐"
            TransactionCategory.HEALTHCARE -> "医疗"
            TransactionCategory.EDUCATION -> "教育"
            TransactionCategory.HOUSING -> "住房"
            TransactionCategory.UTILITIES -> "水电费"
            TransactionCategory.OTHER_EXPENSE -> "其他支出"
            TransactionCategory.SALARY -> "工资"
            TransactionCategory.BONUS -> "奖金"
            TransactionCategory.INVESTMENT -> "投资"
            TransactionCategory.GIFT -> "礼金"
            TransactionCategory.FREELANCE -> "兼职收入"
            TransactionCategory.OTHER_INCOME -> "其他收入"
        }
    }
    

}

