package com.example.miaomiao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miaomiao.ui.theme.*
import com.example.miaomiao.viewmodel.MainViewModel // 添加这行导入

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel, // 添加 viewModel 参数
    onNavigateToSettings: () -> Unit = {},
    onNavigateToPersonalInfo: () -> Unit = {},
    onNavigateToExportBills: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState() // 获取当前用户数据
    val totalBalance by viewModel.totalBalance.collectAsState() // 添加总余额状态
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        // 顶部标题
        Text(
            text = "个人中心",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkPink,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // 用户信息卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .clickable { onNavigateToPersonalInfo() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoftPink),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 头像 - 使用动态数据
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser?.avatar ?: "🐱", // 使用用户的实际头像
                        fontSize = 40.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = currentUser?.username ?: "喵喵用户", // 使用用户的实际用户名
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "记账达人 · 已记账${currentUser?.totalDays ?: 0}天", // 使用用户的实际记账天数
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 统计信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("${currentUser?.totalDays ?: 0}", "记账天数")
                    StatItem("${currentUser?.totalTransactions ?: 0}", "总笔数")
                    StatItem("¥${String.format("%.2f", totalBalance)}", "总余额") // 使用计算出的总余额
                }
            }
        }
        
        // 功能菜单 - 只保留四个功能
        val menuItems = listOf(
            MenuItem("导出账单", Icons.Default.Download, "导出您的账单数据"),
            MenuItem("个人信息", Icons.Default.Person, "修改密码和头像"),
            MenuItem("关于我们", Icons.Default.Info, "了解喵喵记账应用"),
            MenuItem("退出登录", Icons.Default.Logout, "安全退出应用")
        )
        
        menuItems.forEach { item ->
            MenuItemCard(
                item = item,
                onClick = {
                    when (item.title) {
                        "导出账单" -> onNavigateToExportBills()
                        "个人信息" -> onNavigateToPersonalInfo()
                        "关于我们" -> onNavigateToAbout()
                        "退出登录" -> onLogout()
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val description: String
)

@Composable
fun MenuItemCard(
    item: MenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = MediumPink,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkPink
                )
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}