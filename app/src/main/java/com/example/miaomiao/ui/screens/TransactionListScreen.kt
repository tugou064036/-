package com.example.miaomiao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.miaomiao.data.Transaction
import com.example.miaomiao.data.TransactionType
import com.example.miaomiao.data.TransactionCategory
import com.example.miaomiao.ui.theme.*
import com.example.miaomiao.viewmodel.MainViewModel
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: MainViewModel
) {
    val transactions by viewModel.transactions.collectAsState()
    
    // 添加排序状态
    var sortByAmount by remember { mutableStateOf(false) }
    
    // 编辑状态
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Transaction?>(null) }
    
    // 根据排序状态对交易进行排序
    val sortedTransactions = if (sortByAmount) {
        transactions.sortedByDescending { it.amount }
    } else {
        transactions.sortedByDescending { it.date }
    }
    
    // 按日期分组
    val groupedTransactions = if (sortByAmount) {
        // 按金额排序时不分组，直接显示所有交易
        mapOf(LocalDate.now() to sortedTransactions)
    } else {
        sortedTransactions.groupBy { 
            it.date.toLocalDate()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // 顶部栏
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🐱",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "账单明细",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { 
                        sortByAmount = !sortByAmount
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = if (sortByAmount) "按时间排序" else "按金额排序",
                        tint = if (sortByAmount) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )
        
        // 排序提示
        if (sortByAmount) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "💰 当前按金额从大到小排序",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        if (sortedTransactions.isEmpty()) {
            // 空状态
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🦫",
                        fontSize = 60.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    Text(
                        text = "还没有账单记录",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "快去记录第一笔账单吧~",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                groupedTransactions.forEach { (date, dayTransactions) ->
                    if (!sortByAmount) {
                        item {
                            // 日期标题（仅在按时间排序时显示）
                            Text(
                                text = formatDate(date),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                    
                    item {
                        // 当日交易列表
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(15.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                dayTransactions.forEachIndexed { index, transaction ->
                                    TransactionListItem(
                                        transaction = transaction,
                                        showDate = sortByAmount,
                                        onEdit = { editingTransaction = transaction },
                                        onDelete = { showDeleteDialog = transaction }
                                    )
                                    if (index < dayTransactions.size - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            color = Color.Gray.copy(alpha = 0.2f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // 编辑对话框
    editingTransaction?.let { transaction ->
        EditTransactionDialog(
            transaction = transaction,
            onDismiss = { editingTransaction = null },
            onSave = { updatedTransaction ->
                viewModel.updateTransaction(updatedTransaction)
                editingTransaction = null
            }
        )
    }
    
    // 删除确认对话框
    showDeleteDialog?.let { transaction ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("删除账单") },
            text = { Text("确定要删除这笔账单吗？此操作无法撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(transaction)
                        showDeleteDialog = null
                    }
                ) {
                    Text("删除", color = ExpenseRed)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun TransactionListItem(
    transaction: Transaction,
    showDate: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showMenu = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 交易图标
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (transaction.type == TransactionType.INCOME) 
                                IncomeGreen.copy(alpha = 0.2f)
                            else 
                                ExpenseRed.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transaction.category.emoji,
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // 交易信息
                Column {
                    Text(
                        text = transaction.description.ifEmpty { transaction.category.displayName },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (showDate) {
                            // 按金额排序时显示完整日期
                            transaction.date.format(DateTimeFormatter.ofPattern("MM月dd日"))
                        } else {
                            // 按时间排序时只显示日期（因为已经有日期分组标题）
                            transaction.date.format(DateTimeFormatter.ofPattern("MM月dd日"))
                        },
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 交易金额
                Text(
                    text = if (transaction.type == TransactionType.INCOME) 
                        "+¥${String.format("%.2f", transaction.amount)}"
                    else 
                        "-¥${String.format("%.2f", transaction.amount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type == TransactionType.INCOME) 
                        IncomeGreen else ExpenseRed
                )
                
                // 更多操作图标
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "更多操作",
                        tint = Color.Gray
                    )
                }
            }
        }
        
        // 下拉菜单
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("编辑") },
                onClick = {
                    showMenu = false
                    onEdit()
                },
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            )
            DropdownMenuItem(
                text = { Text("删除", color = ExpenseRed) },
                onClick = {
                    showMenu = false
                    onDelete()
                },
                leadingIcon = {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = ExpenseRed)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var selectedType by remember { mutableStateOf(transaction.type) }
    var selectedCategory by remember { mutableStateOf(transaction.category) }
    var description by remember { mutableStateOf(transaction.description) }
    var selectedDate by remember { mutableStateOf(transaction.date.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(transaction.date.toLocalTime()) }
    
    // 日期和时间选择器状态
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute
    )
    
    val expenseCategories = listOf(
        TransactionCategory.FOOD,
        TransactionCategory.TRANSPORT,
        TransactionCategory.SHOPPING,
        TransactionCategory.ENTERTAINMENT,
        TransactionCategory.HEALTHCARE,
        TransactionCategory.EDUCATION,
        TransactionCategory.UTILITIES,
        TransactionCategory.HOUSING,
        TransactionCategory.OTHER_EXPENSE
    )
    
    val incomeCategories = listOf(
        TransactionCategory.SALARY,
        TransactionCategory.BONUS,
        TransactionCategory.INVESTMENT,
        TransactionCategory.GIFT,
        TransactionCategory.OTHER_INCOME
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "编辑账单",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // 金额输入
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = newValue
                        }
                    },
                    label = { Text("金额") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                
                // 收支类型选择
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { 
                            selectedType = TransactionType.EXPENSE
                            selectedCategory = expenseCategories.first()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == TransactionType.EXPENSE) 
                                ExpenseRed else Color.Gray.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "支出",
                            color = if (selectedType == TransactionType.EXPENSE) 
                                Color.White else Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { 
                            selectedType = TransactionType.INCOME
                            selectedCategory = incomeCategories.first()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == TransactionType.INCOME) 
                                IncomeGreen else Color.Gray.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "收入",
                            color = if (selectedType == TransactionType.INCOME) 
                                Color.White else Color.Gray
                        )
                    }
                }
                
                // 分类选择
                val categories = if (selectedType == TransactionType.EXPENSE) expenseCategories else incomeCategories
                
                Text(
                    text = "选择分类",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                LazyColumn(
                    modifier = Modifier.height(120.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(categories) { category ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCategory = category },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCategory == category) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (selectedCategory == category) 4.dp else 1.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.emoji,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Text(
                                    text = category.displayName,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                
                // 备注输入
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                
                // 日期选择
                OutlinedTextField(
                    value = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    onValueChange = { },
                    label = { Text("日期") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "选择日期")
                        }
                    }
                )
                
                // 时间选择
                OutlinedTextField(
                    value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onValueChange = { },
                    label = { Text("时间") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.Schedule, contentDescription = "选择时间")
                        }
                    }
                )
                
                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }
                    
                    Button(
                        onClick = {
                            val amountValue = amount.toDoubleOrNull()
                            if (amountValue != null && amountValue > 0) {
                                val updatedTransaction = transaction.copy(
                                    amount = amountValue,
                                    type = selectedType,
                                    category = selectedCategory,
                                    description = description,
                                    date = LocalDateTime.of(selectedDate, selectedTime)
                                )
                                onSave(updatedTransaction)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GradientStart
                        )
                    ) {
                        Text("保存", color = Color.White)
                    }
                }
            }
        }
    }
    
    // 日期选择器对话框
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // 时间选择器对话框
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("选择时间") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        showTimePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

fun formatDate(date: LocalDate): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    
    return when (date) {
        today -> "今天"
        yesterday -> "昨天"
        else -> {
            val formatter = DateTimeFormatter.ofPattern("MM月dd日")
            date.format(formatter)
        }
    }
}