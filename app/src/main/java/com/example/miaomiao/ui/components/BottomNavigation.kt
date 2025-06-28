package com.example.miaomiao.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miaomiao.navigation.Screen
import com.example.miaomiao.ui.theme.GradientStart

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "首页",
                isSelected = currentRoute == Screen.Home.route,
                onClick = { onNavigate(Screen.Home.route) }
            )
            
            BottomNavItem(
                icon = Icons.Default.PieChart,
                label = "统计",
                isSelected = currentRoute == Screen.Statistics.route,
                onClick = { onNavigate(Screen.Statistics.route) }
            )
            
            // 添加按钮
            FloatingActionButton(
                onClick = { onNavigate(Screen.AddTransaction.route) },
                modifier = Modifier.size(56.dp),
                containerColor = GradientStart,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加",
                    modifier = Modifier.size(24.dp)
                )
            }
            
            BottomNavItem(
                icon = Icons.Default.List,
                label = "账单",
                isSelected = currentRoute == Screen.TransactionList.route,
                onClick = { onNavigate(Screen.TransactionList.route) }
            )
            
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "我的",
                isSelected = currentRoute == Screen.Profile.route,
                onClick = { onNavigate(Screen.Profile.route) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (isSelected) GradientStart.copy(alpha = 0.1f) else Color.Transparent
            )
            .padding(8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) GradientStart else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isSelected) GradientStart else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}