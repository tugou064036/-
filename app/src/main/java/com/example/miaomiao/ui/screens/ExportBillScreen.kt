package com.example.miaomiao.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.miaomiao.ui.theme.*
import com.example.miaomiao.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBillScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var isExporting by remember { mutableStateOf(false) }
    var exportAll by remember { mutableStateOf(true) }
    var selectedDirectory by remember { mutableStateOf<Uri?>(null) }
    var hasStoragePermission by remember { mutableStateOf(false) }
    
    // 添加日期选择器状态
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    
    // 检查存储权限
    LaunchedEffect(Unit) {
        hasStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ 检查 MANAGE_EXTERNAL_STORAGE
            Environment.isExternalStorageManager()
        } else {
            // Android 10 及以下使用 READ_EXTERNAL_STORAGE
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    // 权限请求启动器 - 仅用于Android 10及以下
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasStoragePermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "存储权限已授予", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "需要存储权限才能导出文件", Toast.LENGTH_LONG).show()
        }
    }
    
    // Android 11+ 设置页面启动器
    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // 重新检查权限状态
        hasStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        
        if (hasStoragePermission) {
            Toast.makeText(context, "存储权限已授予", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "需要存储权限才能导出文件", Toast.LENGTH_LONG).show()
        }
    }
    
    // 文件夹选择器
    val directoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        selectedDirectory = uri
        if (uri != null) {
            // 持久化权限
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            Toast.makeText(context, "已选择保存位置", Toast.LENGTH_SHORT).show()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 顶部标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
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
                text = "导出账单",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkPink,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // 权限状态卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (hasStoragePermission) Color(0xFFE8F5E8) else Color(0xFFFFF3E0)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = if (hasStoragePermission) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (hasStoragePermission) Color(0xFF4CAF50) else Color(0xFFFF9800),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = if (hasStoragePermission) "存储权限已授予" else "需要存储权限",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasStoragePermission) Color(0xFF2E7D32) else Color(0xFFE65100),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                if (!hasStoragePermission) {
                    Text(
                        text = "为了将Excel文件保存到您选择的位置，需要授予存储权限。",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                // Android 11+ 引导到设置页面
                                try {
                                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                    intent.data = Uri.parse("package:${context.packageName}")
                                    settingsLauncher.launch(intent)
                                } catch (e: Exception) {
                                    // 如果无法打开特定应用的设置页面，打开通用的存储权限设置页面
                                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                    settingsLauncher.launch(intent)
                                }
                            } else {
                                // Android 10 及以下使用普通权限请求
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("授予权限", color = Color.White)
                    }
                }
            }
        }
        
        // 文件夹选择卡片
        if (hasStoragePermission) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "保存位置",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkPink,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    if (selectedDirectory != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = MediumPink,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "已选择保存文件夹",
                                fontSize = 14.sp,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    
                    Button(
                        onClick = { directoryPickerLauncher.launch(null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MediumPink),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedDirectory != null) "重新选择文件夹" else "选择保存文件夹",
                            color = Color.White
                        )
                    }
                }
            }
        }
        
        // 导出选项卡片
        if (hasStoragePermission && selectedDirectory != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "导出范围",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkPink,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // 导出全部选项
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = exportAll,
                            onClick = { exportAll = true },
                            colors = RadioButtonDefaults.colors(selectedColor = MediumPink)
                        )
                        Text(
                            text = "导出全部账单",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    
                    // 导出指定日期范围选项
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !exportAll,
                            onClick = { exportAll = false },
                            colors = RadioButtonDefaults.colors(selectedColor = MediumPink)
                        )
                        Text(
                            text = "导出指定日期范围",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    
                    // 日期选择器（仅在选择指定范围时显示）
                    if (!exportAll) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // 开始日期
                            OutlinedButton(
                                onClick = { showStartDatePicker = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = startDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ?: "选择开始日期",
                                    fontSize = 14.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // 结束日期
                            OutlinedButton(
                                onClick = { showEndDatePicker = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = endDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ?: "选择结束日期",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // 导出说明卡片
        if (hasStoragePermission) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftPink.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MediumPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "导出说明",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkPink,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    
                    Text(
                        text = "• 导出的Excel文件将保存在您选择的文件夹中\n" +
                                "• 文件包含日期、时间、类型、分类、金额、描述等完整信息\n" +
                                "• 文件名格式：喵喵记账_用户名_导出时间.xlsx",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        
        // 导出按钮
        Button(
            onClick = {
                if (!hasStoragePermission) {
                    Toast.makeText(context, "请先授予存储权限", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                
                if (selectedDirectory == null) {
                    Toast.makeText(context, "请先选择保存文件夹", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                
                scope.launch {
                    isExporting = true
                    val result = if (exportAll) {
                        viewModel.exportTransactionsToExcel(context, selectedDirectory!!, null, null)
                    } else {
                        viewModel.exportTransactionsToExcel(context, selectedDirectory!!, startDate, endDate)
                    }
                    
                    isExporting = false
                    
                    if (result.isSuccess) {
                        Toast.makeText(context, "导出成功！文件已保存到选择的文件夹", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "导出失败：${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = !isExporting && hasStoragePermission && selectedDirectory != null && 
                    (exportAll || (startDate != null && endDate != null)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasStoragePermission && selectedDirectory != null) MediumPink else Color.Gray
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isExporting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("导出中...", fontSize = 16.sp, color = Color.White)
            } else {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        !hasStoragePermission -> "需要存储权限"
                        selectedDirectory == null -> "请选择保存位置"
                        else -> "开始导出"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    // 开始日期选择器
    if (showStartDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                startDate = date
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    // 结束日期选择器
    if (showEndDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                endDate = date
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

// 添加日期选择器对话框组件
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                    onDismiss() // 添加这行来关闭对话框
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}