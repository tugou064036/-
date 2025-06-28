package com.example.miaomiao.ui.screens
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miaomiao.ui.theme.GradientEnd
import com.example.miaomiao.ui.theme.GradientStart
import com.example.miaomiao.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: MainViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val registerSuccess by viewModel.registerSuccess.collectAsState()
    
    // 监听注册成功状态
    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            viewModel.setErrorMessage("注册成功")
            onRegisterSuccess()
            viewModel.clearRegisterSuccess()
        }
    }
    
    // 监听错误消息
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrEmpty()) {
            // 错误消息会在UI中显示，这里可以添加额外的处理逻辑
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 可爱的卡皮巴拉吉祥物
            Text(
                text = "🦫",
                fontSize = 40.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            
            // App Logo
            Text(
                text = "🐱💰",
                fontSize = 60.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            
            // App Name
            Text(
                text = "喵喵记账",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 40.dp)
            )
            
            // 注册表单
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 手机号输入
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("手机号") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(25.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                    
                    // 密码输入
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("设置密码") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(25.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )
                    
                    // 确认密码输入
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("确认密码") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        shape = RoundedCornerShape(25.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )
                    
                    // 错误提示
                    val currentErrorMessage = errorMessage
                    if (!currentErrorMessage.isNullOrEmpty()) {
                        Text(
                            text = currentErrorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    // 注册按钮
                    Button(
                        onClick = {
                            when {
                                phone.isBlank() -> {
                                    viewModel.setErrorMessage("手机号不能为空")
                                }
                                password.isBlank() -> {
                                    viewModel.setErrorMessage("密码不能为空")
                                }
                                confirmPassword.isBlank() -> {
                                    viewModel.setErrorMessage("确认密码不能为空")
                                }
                                password != confirmPassword -> {
                                    viewModel.setErrorMessage("密码不一致")
                                }
                                else -> {
                                    viewModel.register(phone, password)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text(
                            text = "注册",
                            color = GradientStart,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // 登录链接
                    TextButton(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = "已有账号？立即登录",
                            color = GradientStart,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}