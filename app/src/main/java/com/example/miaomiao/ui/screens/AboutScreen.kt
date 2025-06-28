package com.example.miaomiao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miaomiao.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF0F5),
                        Color(0xFFF8F9FA)
                    )
                )
            )
    ) {
        // 顶部导航栏
        TopAppBar(
            title = {
                Text(
                    text = "关于我们",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkPink
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = DarkPink
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        
        // 内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 主标题卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SoftPink
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🐱",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "关于喵喵记账",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "——让记账像撸猫一样简单愉悦！",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // 我们是谁卡片
            AboutCard(
                title = "✨ 我们是谁？",
                content = "「喵喵记账」诞生于2025年，是一群爱猫又头疼算账的年轻人创造的。\n\n我们发现：传统记账App枯燥得像猫抓板上的旧毛线……于是决定做点不一样的！\n\n——\"如果记账能像撸猫一样治愈，谁还会拖延呢？\""
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 为什么选择我们卡片
            AboutCard(
                title = "🐾 为什么选择我们？",
                content = "✔ 猫主子认证的极简操作：3秒记一笔，连你家猫都能学会（开玩笑的😉）\n\n✔ 智能账单分析：用「猫饼图」「小鱼干报表」帮你一眼看穿钱都去哪儿了\n\n✔ 多设备同步：手机、平板、电脑，数据像猫毛一样无处不在\n\n✔ 10W+铲屎官的选择：累计为用户省下能买100吨猫粮的钱！"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 幕后团队卡片
            AboutCard(
                title = "👩💻 幕后团队",
                content = "苹总（创始人）\n前财务顾问，现全职猫奴\n名言：\"记账省下的钱，都要给主子买罐罐！\"\n\n代码鱼（技术喵）\n擅长把BUG踢进垃圾桶\n名言：\"喵生苦短，我用Java\""
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 联系我们卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "📮 联系我们",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkPink,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "有建议？想合作？还是单纯想晒猫？",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8F9FA))
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "邮箱",
                            tint = MediumPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2046815570@qq.com",
                            fontSize = 14.sp,
                            color = DarkPink,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 底部装饰
            Text(
                text = "🐾 感谢每一位铲屎官的支持 🐾",
                fontSize = 14.sp,
                color = MediumPink,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AboutCard(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkPink,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )
        }
    }
}