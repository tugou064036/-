package com.example.miaomiao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miaomiao.data.Transaction
import com.example.miaomiao.data.TransactionType
import com.example.miaomiao.ui.theme.*
import com.example.miaomiao.viewmodel.MainViewModel
import java.time.format.DateTimeFormatter

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    val monthlyBalance by viewModel.monthlyBalance.collectAsState()
    val monthlyIncome by viewModel.monthlyIncome.collectAsState()
    val monthlyExpense by viewModel.monthlyExpense.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    
    // 添加隐藏金额状态控制
    var isAmountVisible by remember { mutableStateOf(true) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            // 可爱的猫咪吉祥物和隐藏按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "🐱",
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                
                // 隐藏/显示金额按钮
                IconButton(
                    onClick = { isAmountVisible = !isAmountVisible },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color = MediumPink.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = if (isAmountVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isAmountVisible) "隐藏金额" else "显示金额",
                        tint = MediumPink,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        item {
            // 余额卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GradientStart, GradientEnd)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(25.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "本月余额",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isAmountVisible) "¥${String.format("%.2f", monthlyBalance)}" else "¥****",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                        Text(
                            text = "💰 财富在增长哦~",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        
        item {
            // 收支统计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                // 收入卡片
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "收入",
                            tint = IncomeGreen,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(bottom = 10.dp)
                        )
                        Text(
                            text = if (isAmountVisible) "¥${String.format("%.0f", monthlyIncome)}" else "¥****",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Text(
                            text = "本月收入",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                // 支出卡片
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "支出",
                            tint = ExpenseRed,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(bottom = 10.dp)
                        )
                        Text(
                            text = if (isAmountVisible) "¥${String.format("%.0f", monthlyExpense)}" else "¥****",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Text(
                            text = "本月支出",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        
        item {
            // 最近交易标题
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "最近交易",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        item {
            // 最近交易列表
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                if (recentTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🦫",
                                fontSize = 40.sp,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Text(
                                text = "还没有交易记录哦~",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        recentTransactions.forEach { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                isAmountVisible = isAmountVisible
                            )
                            if (transaction != recentTransactions.last()) {
                                Divider(
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

@Composable
fun TransactionItem(
    transaction: Transaction,
    isAmountVisible: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                    text = transaction.date.format(DateTimeFormatter.ofPattern("MM月dd日")),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        
        // 交易金额
        Text(
            text = if (isAmountVisible) {
                if (transaction.type == TransactionType.INCOME) 
                    "+¥${String.format("%.2f", transaction.amount)}"
                else 
                    "-¥${String.format("%.2f", transaction.amount)}"
            } else {
                if (transaction.type == TransactionType.INCOME) "+¥****" else "-¥****"
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
        )
    }
}