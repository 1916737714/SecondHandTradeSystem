package com.example.andoirdsecondhandtradingsystem.Message
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.andoirdsecondhandtradingsystem.R
import com.example.andoirdsecondhandtradingsystem.ScreenPage

// 数据类
data class Message(val id: Int, val content: String)

@Composable
fun AppNavigation(navController: NavHostController, selectedScreen: ScreenPage, onShowBarsChanged: (Boolean) -> Unit) {
    NavHost(navController, startDestination = "message_screen") {
        composable("message_screen") {
            if (selectedScreen == ScreenPage.Message) {
                onShowBarsChanged(true) // 在MessageScreen中显示bars
                MessageScreen(navController)
            }
        }
        composable("chat_screen/{messageId}") { backStackEntry ->
            val messageId = backStackEntry.arguments?.getString("messageId")?.toIntOrNull()
            messageId?.let {
                onShowBarsChanged(false) // 在MessageScreen中隐藏bars
                ChatScreen(it,navController) }
        }

    }
}

@Composable
fun MessageScreen(navController: NavController) {
    // 使用 remember 保存消息列表的状态
    var messages by remember { mutableStateOf<List<Message>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // 模拟数据请求
    LaunchedEffect(Unit) {
        // 模拟网络请求延迟
        kotlinx.coroutines.delay(1000)
        // 请求完成后更新消息列表
        messages = (1..20).map { Message(it, "Message content $it") }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            // 显示加载指示器
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
        } else {
            // 显示消息列表
            messages?.let {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(it) { message ->
                        MessageItem(message) { messageId ->
                            navController.navigate("chat_screen/$messageId")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, onMessageClick: (Int) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onMessageClick(message.id) })
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Box(modifier = Modifier.size(64.dp))
            {
                //图片
                Image(painter = painterResource(id = R.drawable.image5),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                //红点
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .align(Alignment.TopEnd)  // 将红点定位在右上角
                        .offset(x = 4.dp, y = (-4).dp) // 可选：根据需要调整红点的位置
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            //聊天简介
            Column {
                Row {
                    Column (modifier = Modifier.weight(1f)){
                        Text(text = "Message ID: ${message.id}")
                        Text(text = message.content)
                        Text(text = "date")
                    }
                    Box(modifier = Modifier.size(64.dp))
                    {
                        Image(painter = painterResource(id = R.drawable.image5),
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            }
        }
    }
}
