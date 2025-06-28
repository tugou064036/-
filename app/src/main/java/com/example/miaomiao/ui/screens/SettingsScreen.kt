package com.example.miaomiao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miaomiao.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    var notificationEnabled by remember { mutableStateOf(true) }
    var autoBackup by remember { mutableStateOf(false) }
    var darkMode by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightPink)
            .padding(16.dp)
    ) {
        // 顶部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = DarkPink
                )
            }
            Text(
                text = "设置 ⚙️",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkPink,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // 通知设置
        SettingsSection(title = "通知设置 🔔") {
            SettingsSwitchItem(
                title = "推送通知",
                description = "接收记账提醒和统计报告",
                checked = notificationEnabled,
                onCheckedChange = { notificationEnabled = it }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 数据设置
        SettingsSection(title = "数据设置 💾") {
            SettingsSwitchItem(
                title = "自动备份",
                description = "每日自动备份账单数据",
                checked = autoBackup,
                onCheckedChange = { autoBackup = it }
            )
            
            SettingsClickItem(
                title = "清除缓存",
                description = "清除应用缓存数据",
                icon = Icons.Default.CleaningServices,
                onClick = { /* 清除缓存逻辑 */ }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 外观设置
        SettingsSection(title = "外观设置 🎨") {
            SettingsSwitchItem(
                title = "深色模式",
                description = "使用深色主题（开发中）",
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 关于设置
        SettingsSection(title = "关于 ℹ️") {
            SettingsClickItem(
                title = "版本信息",
                description = "喵喵记账 v1.0.0",
                icon = Icons.Default.Info,
                onClick = { /* 显示版本信息 */ }
            )
            
            SettingsClickItem(
                title = "用户协议",
                description = "查看用户服务协议",
                icon = Icons.Default.Description,
                onClick = { /* 显示用户协议 */ }
            )
            
            SettingsClickItem(
                title = "隐私政策",
                description = "查看隐私保护政策",
                icon = Icons.Default.Security,
                onClick = { /* 显示隐私政策 */ }
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkPink,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = DarkPink
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MediumPink,
                checkedTrackColor = SoftPink
            )
        )
    }
}

@Composable
fun SettingsClickItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MediumPink,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = DarkPink
            )
            Text(
                text = description,
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