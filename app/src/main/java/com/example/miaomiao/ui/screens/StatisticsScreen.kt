package com.example.miaomiao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miaomiao.data.TransactionCategory
import com.example.miaomiao.data.TransactionType
import com.example.miaomiao.ui.theme.*
import com.example.miaomiao.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max
// 在文件顶部添加必要的导入
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

@Composable
fun StatisticsScreen(
    viewModel: MainViewModel
) {
    val transactions by viewModel.transactions.collectAsState()
    
    // 状态管理
    var selectedYear by remember { mutableStateOf(LocalDateTime.now().year) }
    var selectedMonth by remember { mutableStateOf(LocalDateTime.now().monthValue) }
    var selectedStatType by remember { mutableStateOf("支出") } // 支出、收入、结余
    var isLineChart by remember { mutableStateOf(false) } // false=柱形图, true=折线图
    
    // 计算当前选择月份的数据
    val currentMonthTransactions = transactions.filter { 
        it.date.year == selectedYear && it.date.monthValue == selectedMonth 
    }
    
    // 计算月收入
    val monthlyIncome = currentMonthTransactions
        .filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount }
    
    // 计算月支出
    val monthlyExpense = currentMonthTransactions
        .filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount }
    
    // 计算月结余
    val monthlyBalance = monthlyIncome - monthlyExpense
    
    // 计算图表数据（过去12个月）
    val chartData = (0..11).map { monthOffset ->
        val targetDate = LocalDate.of(selectedYear, selectedMonth, 1).minusMonths(monthOffset.toLong())
        val monthTransactions = transactions.filter {
            it.date.year == targetDate.year && it.date.monthValue == targetDate.monthValue
        }
        
        val monthLabel = "${targetDate.monthValue}月"
        val isSelected = monthOffset == 0
        
        Triple(
            monthLabel,
            when (selectedStatType) {
                "收入" -> monthTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                "支出" -> monthTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                "结余" -> monthTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount } - 
                          monthTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                else -> 0.0
            },
            isSelected
        )
    }.reversed()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "统计",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkPink
                )
            }
        }
        
        // 月份选择器
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (selectedMonth == 1) {
                                selectedMonth = 12
                                selectedYear -= 1
                            } else {
                                selectedMonth -= 1
                            }
                        }
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "上个月")
                    }
                    
                    Text(
                        text = "${selectedYear}年${selectedMonth}月",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = {
                            if (selectedMonth == 12) {
                                selectedMonth = 1
                                selectedYear += 1
                            } else {
                                selectedMonth += 1
                            }
                        }
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "下个月")
                    }
                }
            }
        }
        
        // 统计类型选择
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listOf("支出", "收入", "结余")) { type ->
                    val isSelected = selectedStatType == type
                    val backgroundColor = when (type) {
                        "收入" -> if (isSelected) Color(0xFF4ECDC4) else Color(0xFF4ECDC4).copy(alpha = 0.2f)
                        "支出" -> if (isSelected) Color(0xFFFF6B6B) else Color(0xFFFF6B6B).copy(alpha = 0.2f)
                        "结余" -> if (isSelected) Color(0xFF45B7D1) else Color(0xFF45B7D1).copy(alpha = 0.2f)
                        else -> Color.Gray
                    }
                    
                    Card(
                        modifier = Modifier.clickable { selectedStatType = type },
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected) 8.dp else 2.dp
                        )
                    ) {
                        Text(
                            text = type,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (isSelected) Color.White else Color.Black,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
        
        // 当月统计卡片
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 收入卡片
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4ECDC4).copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF4ECDC4), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "收入",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "¥${String.format("%.2f", monthlyIncome)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4ECDC4)
                        )
                    }
                }
                
                // 支出卡片
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B6B).copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFFFF6B6B), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "支出",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "¥${String.format("%.2f", monthlyExpense)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B6B)
                        )
                    }
                }
            }
        }
        
        // 修改图表卡片部分（第270-310行左右）
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 图表标题（移除切换按钮）
                    Text(
                        text = "${selectedStatType}趋势",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 图表（只显示柱状图）
                    SimpleChart(
                        data = chartData,
                        isLineChart = false,
                        selectedStatType = selectedStatType
                    )
                }
            }
        }
        
        // 饼状图和分类统计 - 只在非结余模式下显示
        if (selectedStatType != "结余") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "本月${selectedStatType}分类",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val categoryData = when (selectedStatType) {
                            "收入" -> currentMonthTransactions
                                .filter { it.type == TransactionType.INCOME }
                                .groupBy { it.category }
                                .map { (category, transactions) ->
                                    category to transactions.sumOf { it.amount }
                                }
                                .sortedByDescending { it.second }
                            "支出" -> currentMonthTransactions
                                .filter { it.type == TransactionType.EXPENSE }
                                .groupBy { it.category }
                                .map { (category, transactions) ->
                                    category to transactions.sumOf { it.amount }
                                }
                                .sortedByDescending { it.second }
                            else -> emptyList()
                        }
                        
                        if (categoryData.isNotEmpty()) {
                            // 添加真正的饼状图
                            val total = categoryData.sumOf { kotlin.math.abs(it.second) }
                            if (total > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(
                                        modifier = Modifier.size(160.dp)
                                    ) {
                                        val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                                        val radius = size.minDimension / 2 * 0.8f
                                        val innerRadius = radius * 0.5f
                                        
                                        var startAngle = -90f
                                        
                                        categoryData.forEachIndexed { index, (category, amount) ->
                                            val sweepAngle = (kotlin.math.abs(amount) / total * 360).toFloat()
                                            
                                            val colors = listOf(
                                                Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFF45B7D1),
                                                Color(0xFFFFA726), Color(0xFF66BB6A), Color(0xFFAB47BC),
                                                Color(0xFF26A69A), Color(0xFFEF5350), Color(0xFF42A5F5)
                                            )
                                            val color = colors[index % colors.size]
                                            
                                            drawArc(
                                                color = color,
                                                startAngle = startAngle,
                                                sweepAngle = sweepAngle,
                                                useCenter = true,
                                                topLeft = androidx.compose.ui.geometry.Offset(
                                                    center.x - radius,
                                                    center.y - radius
                                                ),
                                                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                                            )
                                            
                                            // 绘制内圆（环形效果）
                                            drawCircle(
                                                color = Color.White,
                                                radius = innerRadius,
                                                center = center
                                            )
                                            
                                            startAngle += sweepAngle
                                        }
                                    }
                                    
                                    // 中心显示总金额
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "¥${String.format("%.0f", total)}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "总${selectedStatType}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            // 图例
                            categoryData.forEachIndexed { index, (category, amount) ->
                                val colors = listOf(
                                    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFF45B7D1),
                                    Color(0xFFFFA726), Color(0xFF66BB6A), Color(0xFFAB47BC),
                                    Color(0xFF26A69A), Color(0xFFEF5350), Color(0xFF42A5F5)
                                )
                                val color = colors[index % colors.size]
                                val percentage = (kotlin.math.abs(amount) / categoryData.sumOf { kotlin.math.abs(it.second) } * 100)
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(color, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${category.emoji} ${category.displayName}",
                                            fontSize = 14.sp
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "¥${String.format("%.2f", amount)}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "${String.format("%.1f", percentage)}%",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = when (selectedStatType) {
                                    "收入" -> "暂无收入记录"
                                    "支出" -> "暂无支出记录"
                                    else -> "暂无记录"
                                },
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        
        // 结余模式下显示每日概况
        if (selectedStatType == "结余") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📅 每日概况",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 计算每日数据
                        val dailyData = currentMonthTransactions
                            .groupBy { it.date.toLocalDate() }
                            .map { (date, transactions) ->
                                val dayIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                                val dayExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                                val dayBalance = dayIncome - dayExpense
                                data class DailyStats(val date: LocalDate, val income: Double, val expense: Double, val balance: Double, val transactionCount: Int)
                                DailyStats(date, dayIncome, dayExpense, dayBalance, transactions.size)
                            }
                            .sortedByDescending { it.date }
                        
                        if (dailyData.isNotEmpty()) {
                            dailyData.forEach { dailyStats ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.Gray.copy(alpha = 0.05f)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        // 日期和交易笔数
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = dailyStats.date.format(DateTimeFormatter.ofPattern("MM月dd日")),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${dailyStats.transactionCount}笔交易",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        // 收入、支出、结余
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            // 收入
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "收入",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = "¥${String.format("%.2f", dailyStats.income)}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF4ECDC4)
                                                )
                                            }
                                            
                                            // 支出
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "支出",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = "¥${String.format("%.2f", dailyStats.expense)}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFFFF6B6B)
                                                )
                                            }
                                            
                                            // 结余
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "结余",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = if (dailyStats.balance >= 0) 
                                                        "+¥${String.format("%.2f", dailyStats.balance)}" 
                                                    else 
                                                        "-¥${String.format("%.2f", kotlin.math.abs(dailyStats.balance))}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (dailyStats.balance >= 0) Color(0xFF4ECDC4) else Color(0xFFFF6B6B)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // 月度汇总
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (monthlyBalance >= 0) 
                                        Color(0xFF4ECDC4).copy(alpha = 0.1f) 
                                    else 
                                        Color(0xFFFF6B6B).copy(alpha = 0.1f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "💰 月度汇总",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        // 月度收入
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "总收入",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = "¥${String.format("%.2f", monthlyIncome)}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF4ECDC4)
                                            )
                                        }
                                        
                                        // 月度支出
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "总支出",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = "¥${String.format("%.2f", monthlyExpense)}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFF6B6B)
                                            )
                                        }
                                        
                                        // 月度结余
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "总结余",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = if (monthlyBalance >= 0) 
                                                    "+¥${String.format("%.2f", monthlyBalance)}" 
                                                else 
                                                    "-¥${String.format("%.2f", kotlin.math.abs(monthlyBalance))}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (monthlyBalance >= 0) Color(0xFF4ECDC4) else Color(0xFFFF6B6B)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📊",
                                        fontSize = 40.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = "暂无交易记录",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // 动态排行榜 - 只在非结余模式下显示
        if (selectedStatType != "结余") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "${selectedStatType}排行榜",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val rankingData = when (selectedStatType) {
                            "收入" -> currentMonthTransactions
                                .filter { it.type == TransactionType.INCOME }
                                .groupBy { it.category }
                                .map { (category, transactions) ->
                                    category to transactions.sumOf { it.amount }
                                }
                                .sortedByDescending { it.second }
                            "支出" -> currentMonthTransactions
                                .filter { it.type == TransactionType.EXPENSE }
                                .groupBy { it.category }
                                .map { (category, transactions) ->
                                    category to transactions.sumOf { it.amount }
                                }
                                .sortedByDescending { it.second }
                            else -> emptyList()
                        }
                        
                        if (rankingData.isNotEmpty()) {
                            rankingData.take(5).forEachIndexed { index, (category, amount) ->
                                val percentage = when (selectedStatType) {
                                    "收入" -> if (monthlyIncome > 0) (amount / monthlyIncome) * 100 else 0.0
                                    "支出" -> if (monthlyExpense > 0) (amount / monthlyExpense) * 100 else 0.0
                                    else -> 0.0
                                }
                                RankingItem(
                                    rank = index + 1,
                                    category = category,
                                    amount = amount,
                                    percentage = percentage
                                )
                                if (index < rankingData.size - 1 && index < 4) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = Color.Gray.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (selectedStatType) {
                                        "收入" -> "暂无收入记录"
                                        "支出" -> "暂无支出记录"
                                        else -> "暂无记录"
                                    },
                                    color = Color.Gray,
                                    fontSize = 14.sp
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
fun SimpleChart(
    data: List<Triple<String, Double, Boolean>>,
    isLineChart: Boolean,
    selectedStatType: String
) {
    val maxValue = data.maxOfOrNull { kotlin.math.abs(it.second) } ?: 1.0
    val minValue = data.minOfOrNull { it.second } ?: 0.0
    val chartColor = when (selectedStatType) {
        "收入" -> Color(0xFF4ECDC4)
        "支出" -> Color(0xFFFF6B6B)
        "结余" -> Color(0xFF45B7D1)
        else -> Color(0xFFFF6B6B)
    }
    
    Column {
        // 图表区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEachIndexed { index, (month, value, isSelected) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // 计算高度，确保最小高度为2dp，最大高度为80dp
                    val height = if (maxValue > 0) {
                        val normalizedValue = if (selectedStatType == "结余") {
                            kotlin.math.abs(value) / maxValue
                        } else {
                            value / maxValue
                        }
                        kotlin.math.max(if (value != 0.0) 2.0 else 0.0, normalizedValue * 78.0).dp
                    } else {
                        if (value != 0.0) 2.dp else 0.dp
                    }
                    
                    if (isLineChart) {
                        // 折线图的点
                        Spacer(modifier = Modifier.height(80.dp - height))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (isSelected) chartColor else chartColor.copy(alpha = 0.7f),
                                    CircleShape
                                )
                        )
                        // 显示数值
                        if (value != 0.0) {
                            Text(
                                text = String.format("%.0f", kotlin.math.abs(value)),
                                fontSize = 8.sp,
                                color = chartColor,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    } else {
                        // 柱状图
                        Spacer(modifier = Modifier.height(80.dp - height))
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(height)
                                .background(
                                    if (isSelected) chartColor else chartColor.copy(alpha = 0.7f),
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        // 显示数值
                        if (value != 0.0) {
                            Text(
                                text = String.format("%.0f", kotlin.math.abs(value)),
                                fontSize = 8.sp,
                                color = chartColor,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // 添加折线图的连线（如果是折线图）
        if (isLineChart && data.isNotEmpty()) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                val width = size.width
                val height = size.height
                val stepWidth = width / (data.size - 1).coerceAtLeast(1)
                
                // 计算数据范围，包括正负值
                val minValue = data.minOfOrNull { it.second } ?: 0.0
                val maxValue = data.maxOfOrNull { it.second } ?: 0.0
                val dataRange = maxValue - minValue
                
                val path = Path()
                data.forEachIndexed { index, (_, value, _) ->
                    val x = index * stepWidth
                    val y = if (dataRange > 0) {
                        // 将数据映射到画布高度，保持正负值关系
                        height - ((value - minValue) / dataRange * height).toFloat()
                    } else {
                        height / 2 // 如果所有值相同，显示在中间
                    }
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = chartColor,
                    style = Stroke(width = 2.dp.toPx())
                )
                
                // 如果有负值，绘制零线
                if (minValue < 0 && maxValue > 0) {
                    val zeroY = height - ((-minValue) / dataRange * height).toFloat()
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.5f),
                        start = androidx.compose.ui.geometry.Offset(0f, zeroY),
                        end = androidx.compose.ui.geometry.Offset(width, zeroY),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 月份标签
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { (month, _, isSelected) ->
                Text(
                    text = month,
                    fontSize = 10.sp,
                    color = if (isSelected) chartColor else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RankingItem(
    rank: Int,
    category: TransactionCategory,
    amount: Double,
    percentage: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 排名
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        when (rank) {
                            1 -> Color(0xFFFFD700) // 金色
                            2 -> Color(0xFFC0C0C0) // 银色
                            3 -> Color(0xFFCD7F32) // 铜色
                            else -> Color.Gray.copy(alpha = 0.3f)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "${category.emoji} ${category.displayName}",
                fontSize = 14.sp
            )
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "¥${String.format("%.2f", amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${String.format("%.1f", percentage)}%",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}