package com.example.miaomiao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miaomiao.ui.theme.*
import com.example.miaomiao.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showUsernameDialog by remember { mutableStateOf(false) }
    
    // 显示消息
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            // 这里可以显示 Snackbar 或 Toast
        }
    }
    
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            // 这里可以显示 Snackbar 或 Toast
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        // 顶部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
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
                text = "个人信息",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkPink,
                modifier = Modifier.weight(1f)
            )
        }
        
        // 用户信息卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
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
                // 头像
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { showAvatarDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser?.avatar ?: "🐱",
                        fontSize = 50.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = currentUser?.username ?: "喵喵用户",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "手机号：${currentUser?.phone ?: "未知"}",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        
        // 功能选项
        val options = listOf(
            PersonalInfoOption("修改用户名", Icons.Default.Person, "更改您的显示名称") { showUsernameDialog = true },
            PersonalInfoOption("修改头像", Icons.Default.Face, "选择您喜欢的头像") { showAvatarDialog = true },
            PersonalInfoOption("修改密码", Icons.Default.Lock, "更改登录密码") { showPasswordDialog = true }
        )
        
        options.forEach { option ->
            PersonalInfoOptionCard(
                option = option,
                onClick = option.onClick
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // 显示消息
        errorMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
            ) {
                Text(
                    text = message,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        successMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f))
            ) {
                Text(
                    text = message,
                    color = Color.Green,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
    
    // 修改密码对话框调用逻辑
    if (showPasswordDialog) {
        PasswordChangeDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { oldPassword, newPassword, confirmPassword ->
                // 在这里进行前端验证
                when {
                    oldPassword.isBlank() -> {
                        viewModel.setErrorMessage("请输入当前密码")
                    }
                    newPassword.isBlank() -> {
                        viewModel.setErrorMessage("请输入新密码")
                    }
                    confirmPassword.isBlank() -> {
                        viewModel.setErrorMessage("请确认新密码")
                    }
                    newPassword != confirmPassword -> {
                        viewModel.setErrorMessage("两次输入的新密码不一致")
                    }
                    newPassword.length < 6 -> {
                        viewModel.setErrorMessage("新密码长度不能少于6位")
                    }
                    oldPassword == newPassword -> {
                        viewModel.setErrorMessage("新密码不能与原密码相同")
                    }
                    else -> {
                        // 所有验证通过，调用ViewModel更新密码
                        viewModel.updateUserPassword(oldPassword, newPassword)
                        showPasswordDialog = false
                    }
                }
            }
        )
    }
    
    // 修改头像对话框
    if (showAvatarDialog) {
        AvatarSelectionDialog(
            currentAvatar = currentUser?.avatar ?: "🐱",
            onDismiss = { showAvatarDialog = false },
            onConfirm = { newAvatar ->
                viewModel.updateUserAvatar(newAvatar)
                showAvatarDialog = false
            }
        )
    }
    
    // 修改用户名对话框
    if (showUsernameDialog) {
        UsernameChangeDialog(
            currentUsername = currentUser?.username ?: "喵喵用户",
            onDismiss = { showUsernameDialog = false },
            onConfirm = { newUsername ->
                viewModel.updateUserInfo(newUsername, currentUser?.avatar ?: "🐱")
                showUsernameDialog = false
            }
        )
    }
}

data class PersonalInfoOption(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val description: String,
    val onClick: () -> Unit
)

@Composable
fun PersonalInfoOptionCard(
    option: PersonalInfoOption,
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
                imageVector = option.icon,
                contentDescription = option.title,
                tint = MediumPink,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkPink
                )
                Text(
                    text = option.description,
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
fun PasswordChangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit // 修改参数，增加确认密码
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // 实时验证状态
    val isPasswordMatch = newPassword == confirmPassword
    val isFormValid = oldPassword.isNotBlank() && 
                     newPassword.isNotBlank() && 
                     confirmPassword.isNotBlank() && 
                     isPasswordMatch &&
                     newPassword.length >= 6 &&
                     oldPassword != newPassword
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("修改密码", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("当前密码") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("新密码") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        if (newPassword.isNotBlank() && newPassword.length < 6) {
                            Text(
                                text = "密码长度不能少于6位",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("确认新密码") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        if (confirmPassword.isNotBlank() && !isPasswordMatch) {
                            Text(
                                text = "两次输入的密码不一致",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = passwordVisible,
                        onCheckedChange = { passwordVisible = it }
                    )
                    Text("显示密码", fontSize = 14.sp)
                }
                
                // 额外的验证提示
                if (oldPassword.isNotBlank() && newPassword.isNotBlank() && oldPassword == newPassword) {
                    Text(
                        text = "新密码不能与原密码相同",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(oldPassword, newPassword, confirmPassword)
                },
                enabled = isFormValid
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun AvatarSelectionDialog(
    currentAvatar: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedAvatar by remember { mutableStateOf(currentAvatar) }
    
    val avatarOptions = listOf(
        "🐱", "🐶", "🐰", "🐻", "🐼", "🦊", "🐸", "🐷",
        "🐵", "🐯", "🦁", "🐨", "🐹", "🐭", "🐺", "🦝"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("选择头像", fontWeight = FontWeight.Bold)
        },
        text = {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(avatarOptions.chunked(4)) { row ->
                    Column {
                        row.forEach { avatar ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (avatar == selectedAvatar) SoftPink else Color.Gray.copy(alpha = 0.1f)
                                    )
                                    .clickable { selectedAvatar = avatar },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = avatar,
                                    fontSize = 30.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedAvatar) }
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun UsernameChangeDialog(
    currentUsername: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newUsername by remember { mutableStateOf(currentUsername) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("修改用户名", fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text("用户名") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(newUsername) },
                enabled = newUsername.isNotBlank() && newUsername != currentUsername
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}