package com.example.andoirdsecondhandtradingsystem.Message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.andoirdsecondhandtradingsystem.R
import kotlinx.coroutines.launch

// 数据类定义
data class StaticMessage(val id: Int, val content: String)

// Mock 静态消息数据
val staticMessages = listOf(
    StaticMessage(1, "Hello! How are you?"),
    StaticMessage(10, "I'm good, thank you! And you?"),
    StaticMessage(9, "I'm great! Thanks for asking."),
    StaticMessage(8, "What are you up to today?"),
    StaticMessage(7, "Just working on some Compose code.What are you"),
    StaticMessage(6, "Just working on some Compose code.What are you"),
    StaticMessage(5, "Just working on some Compose code.What are you"),
    StaticMessage(4, "Just working on some Compose code.What are you"),
    StaticMessage(3, "Just working on some Compose codeWhat are you."),
    StaticMessage(2, "Just working on some Compose codeWhat are you."),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(messageId: Int,navController: NavHostController) {
    // 使用 mutableStateListOf 来维护可变的消息列表
    val messages = remember { mutableStateListOf<StaticMessage>().apply { addAll(staticMessages) } }

    Scaffold(
        topBar = {
            TopAppBar(
//                modifier = Modifier.height(36.dp),
                navigationIcon = {
                    IconButton(onClick = { /* Handle back press */  navController.popBackStack()}) {
                        Icon(painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp))
                    }
                },
                title = { Text(text = "Chat for Message ID: $messageId") },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7183B8))
            )
        },
        bottomBar = {
            ChatInputBar(onSend = { newMessage ->
                // 添加新消息到消息列表
                messages.add(0, newMessage)
            })
        }
    ) { paddingValues ->
        ChatContent(
            messages = messages,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ChatContent(messages: List<StaticMessage>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize(),
//            .background(Color(ContextCompat.getColor(context, R.color.teal_200))),
        contentAlignment = Alignment.BottomEnd
    ) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(bottom = 60.dp),
            modifier = Modifier.padding(start = 15.dp, end = 15.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.Top,
        ) {
            items(messages) { message ->
                FromMessageItemView(message)
                ToMessageItemView(message)
            }
        }

        // 自动滚动到最新消息
        LaunchedEffect(messages.size) {
            coroutineScope.launch {
                scrollState.scrollToItem(0)
            }
        }
    }
}

@Composable
fun FromMessageItemView(message: StaticMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 0.dp, max = 300.dp)
                .padding(start = 8.dp),
            color = Color.Yellow // 设置背景颜色为黄色
        ) {
            Text(
                text = message.content,
                modifier = Modifier
                    .padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.size(52.dp)) {
            Image(
                painter = painterResource(id = R.drawable.image5),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
        }
    }
}


@Composable
fun ToMessageItemView(message: StaticMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {

        Box(modifier = Modifier.size(52.dp)) {
        Image(
            painter = painterResource(id = R.drawable.image5),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
    }
        Spacer(modifier = Modifier.width(8.dp))

        Card(
            modifier = Modifier
                .padding(start = 8.dp)
                .widthIn(min = 0.dp, max = 300.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier
                    .padding(12.dp)

            )
        }

    }
}

@Composable
fun ChatInputBar(onSend: (StaticMessage) -> Unit) {
    var messageText by remember { mutableStateOf("") }
    var messageId by remember { mutableStateOf(11) } // 初始消息ID
    var isEmojiPanelVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color.LightGray, RoundedCornerShape(28.dp)), // 设置背景和圆角
                textStyle = TextStyle(color = Color.Gray, fontSize = 16.sp), // 设置文本样式
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (messageText.isEmpty()) {
                            Text(
                                text = "说点什么吧·····",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(0.28f)
                    .align(Alignment.CenterVertically)
            ) {
                if (messageText.isNotBlank()) {
                    Button(
                        onClick = {
                            onSend(StaticMessage(messageId++, messageText))
                            messageText = "" // 清空输入栏内容
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1AAD19), // 设置按钮背景颜色为绿色
                            contentColor = Color.White // 设置按钮文字颜色为白色
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 8.dp)
                    ) {
                        Text("发送")
                    }
                } else {
                    IconButton(onClick = { isEmojiPanelVisible = !isEmojiPanelVisible
                        keyboardController?.hide()}) {
                        Icon(
                            painter = painterResource(id = R.drawable.emoji),
                            contentDescription = "Emoji",
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            }
        }

        if (isEmojiPanelVisible) {
            EmojiPicker(onEmojiSelected = { emoji ->
                messageText += emoji
//                isEmojiPanelVisible = false
            })
        }
    }
}

@Composable
fun EmojiPicker(onEmojiSelected: (String) -> Unit) {
    AndroidView(
        factory = { context ->
            val emojiPickerView = EmojiPickerView(context)
            emojiPickerView.setOnEmojiPickedListener { emoji ->
                onEmojiSelected(emoji.emoji)
            }
            emojiPickerView
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.White)
    )
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val navController = rememberNavController()
    ChatScreen(messageId = 1,navController)
}

