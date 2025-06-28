package com.example.miaomiao.ui.screens
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miaomiao.data.Transaction
import com.example.miaomiao.data.TransactionCategory
import com.example.miaomiao.data.TransactionType
import com.example.miaomiao.ui.theme.*
import com.example.miaomiao.viewmodel.MainViewModel
import java.time.LocalDateTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(null) }
    var description by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加交易") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(BackgroundLight, Color.White)
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 金额输入
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        // 只允许数字和小数点
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = newValue
                        }
                    },
                    label = { Text("输入金额") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
            
            // 收支类型选择
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // 支出按钮
                    Button(
                        onClick = { 
                            selectedType = TransactionType.EXPENSE
                            selectedCategory = null
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == TransactionType.EXPENSE) 
                                ExpenseRed else Color.Gray.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "支出",
                            color = if (selectedType == TransactionType.EXPENSE) 
                                Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    // 收入按钮
                    Button(
                        onClick = { 
                            selectedType = TransactionType.INCOME
                            selectedCategory = null
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == TransactionType.INCOME) 
                                IncomeGreen else Color.Gray.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "收入",
                            color = if (selectedType == TransactionType.INCOME) 
                                Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // 分类选择
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
                    Text(
                        text = "选择分类",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.height(200.dp)
                    ) {
                        val categories = if (selectedType == TransactionType.EXPENSE) 
                            expenseCategories else incomeCategories
                            
                        items(categories) { category ->
                            CategoryItem(
                                category = category,
                                isSelected = selectedCategory == category,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }
            }
            
            // 备注输入
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("备注 (可选)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    maxLines = 3
                )
            }
            
            // 确认按钮
            Button(
                onClick = {
                    if (amount.isNotEmpty() && selectedCategory != null) {
                        val amountValue = amount.toDoubleOrNull()
                        if (amountValue != null && amountValue > 0) {
                            showConfirmDialog = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 10.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GradientStart
                ),
                enabled = amount.isNotEmpty() && selectedCategory != null
            ) {
                Text(
                    text = "确认添加",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
    
    // 确认对话框
    if (showConfirmDialog) {
        ConfirmTransactionDialog(
            amount = amount,
            type = selectedType,
            category = selectedCategory!!,
            description = description,
            onConfirm = {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null && amountValue > 0 && selectedCategory != null) {
                    val currentUser = viewModel.currentUser.value
                    if (currentUser != null) {
                        scope.launch {
                            val currentTime = viewModel.getCurrentTime()
                            val transaction = Transaction(
                                userId = currentUser.id,
                                amount = amountValue,
                                type = selectedType,
                                category = selectedCategory!!,
                                description = description,
                                date = currentTime
                            )
                            viewModel.addTransaction(transaction)
                        }
                    } else {
                        viewModel.setErrorMessage("请先登录")
                    }
                    showConfirmDialog = false
                    onNavigateBack()
                }
            },
            onDismiss = {
                showConfirmDialog = false
            }
        )
        
        // 添加时间状态显示
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "时间同步状态",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = viewModel.getTimeStatus(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (viewModel.getTimeStatus().contains("网络")) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}


@Composable
fun ConfirmTransactionDialog(
    amount: String,
    type: TransactionType,
    category: TransactionCategory,
    description: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "确认交易信息",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("类型：")
                    Text(
                        text = if (type == TransactionType.INCOME) "收入" else "支出",
                        color = if (type == TransactionType.INCOME) IncomeGreen else ExpenseRed,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("金额：")
                    Text(
                        text = "¥$amount",
                        fontWeight = FontWeight.Bold,
                        color = if (type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("分类：")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = category.emoji,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(category.displayName)
                    }
                }
                
                if (description.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("备注：")
                        Text(
                            text = description,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GradientStart
                )
            ) {
                Text(
                    text = "确认保存",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "取消",
                    color = Color.Gray
                )
            }
        },
        shape = RoundedCornerShape(15.dp)
    )
}

@Composable
fun CategoryItem(
    category: TransactionCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GradientStart.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f)
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = category.emoji,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = category.displayName,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = if (isSelected) GradientStart else Color.Gray
            )
        }
    }
}