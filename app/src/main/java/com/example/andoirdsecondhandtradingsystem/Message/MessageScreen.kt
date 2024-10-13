package com.example.andoirdsecondhandtradingsystem.Message
import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.andoirdsecondhandtradingsystem.R
import com.example.andoirdsecondhandtradingsystem.ScreenPage
import com.example.andoirdsecondhandtradingsystem.data.AuthViewModel
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.google.gson.Gson
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// 数据类
data class Message(val id: Int, val content: String)

sealed class Screen(val route: String) {
    object MessageListScreen : Screen("message_screen")
    object ChatScreen : Screen("chat_screen/{fromUserId}/{fromUsername}/{userId}") {
        fun createRoute(fromUserId: String, fromUsername: String, userId: String) =
            "chat_screen/$fromUserId/$fromUsername/$userId"
    }
}

@Composable
fun AppNavigation(navController: NavHostController, selectedScreen: ScreenPage, user: Data.User, onShowBarsChanged: (Boolean) -> Unit) {
    NavHost(navController, startDestination = Screen.MessageListScreen.route) {
        composable(Screen.MessageListScreen.route) {
            if (selectedScreen == ScreenPage.Message) {
                onShowBarsChanged(true) // 在MessageScreen中显示bars
                MessageScreen(navController, user)
            }
        }
        composable(
            route = Screen.ChatScreen.route,
            arguments = listOf(
                navArgument("fromUserId") { type = NavType.StringType },
                navArgument("fromUsername") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val fromUserId = backStackEntry.arguments?.getString("fromUserId").orEmpty()
            val fromUsername = backStackEntry.arguments?.getString("fromUsername").orEmpty()
            val userId = backStackEntry.arguments?.getString("userId").orEmpty()
            onShowBarsChanged(false) // 在ChatScreen中隐藏bars
            ChatScreen(fromUserId, fromUsername, userId.toInt(), navController)
        }
    }
}




@Composable
fun MessageScreen(navController: NavController, user: Data.User, viewModel: AuthViewModel = viewModel()) {
    var messages by remember { mutableStateOf<List<Data.MessageRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // 模拟网络请求延迟
        kotlinx.coroutines.delay(1000)
        viewModel.getMessageList(
            userId = user.id,
            onSuccess = { messageListData ->
                messageListData.forEach { message ->
                    viewModel.getMessageDetail(
                        fromUserId = message.fromUserId.toInt(),
                        userId = user.id,
                        page = 1,
                        onSuccess = { messageDetailData ->
                            messages = (messages + messageDetailData).distinctBy { it.id }
                            isLoading = false
                        },
                        onError = { errorMessage ->
                            Log.e("Error", "Error: $errorMessage")
                            isLoading = false
                        }
                    )
                }
            },
            onError = { errorMessage ->
                Log.e("ErrorMessageList", "Error: $errorMessage")
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
        } else {
            val latestMessagesMap = messages.groupBy { it.fromUsername }.mapValues { entry ->
                entry.value.maxByOrNull { it.id }!!
            }
            val latestMessages = latestMessagesMap.values.filter { it.fromUsername != user.username }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(latestMessages.toList()) { message ->
                    MessageItem(message) { messageId ->
                        val fromUserId = message.fromUserId
                        val fromUsername = message.fromUsername
                        val userId = user.id.toString()
                        navController.navigate(Screen.ChatScreen.createRoute(fromUserId, fromUsername, userId))

                        // 调用MarkMessageRead方法更新服务器端消息状态
                        viewModel.MarkMessageRead(
                            chatId = messageId,
                            onSuccess = {
                                // 更新本地消息状态为已读
                                messages = messages.map {
                                    if (it.id.toInt() == messageId) it.copy(status = true) else it
                                }
                            },
                            onError = { errorMessage ->
                                Log.e("MarkMessageRead", "Error: $errorMessage")
                            }
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun MessageItem(message: Data.MessageRecord, onMessageClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMessageClick(message.id.toInt()) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Box(modifier = Modifier.size(64.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.image5),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                // 显示红点的逻辑
                if (!message.status) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = message.fromUsername,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = message.content,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = formatTimestampToDate(message.createTime.toLong()),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    Box(modifier = Modifier.size(64.dp)) {
//                        Image(
//                            painter = painterResource(id = R.drawable.image5),
//                            contentDescription = null,
//                            modifier = Modifier.size(64.dp)
//                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            }
        }
    }
}

//将时间戳转换陈YYYY-MM-DD格式
fun formatTimestampToDate(timestamp: Long): String {
    val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return localDateTime.format(formatter)
}

