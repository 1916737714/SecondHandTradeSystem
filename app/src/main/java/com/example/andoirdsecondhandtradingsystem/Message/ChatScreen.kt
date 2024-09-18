package com.example.andoirdsecondhandtradingsystem.Message

import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.andoirdsecondhandtradingsystem.R
import com.example.andoirdsecondhandtradingsystem.data.AuthViewModel
import com.example.andoirdsecondhandtradingsystem.data.Data
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    fromUserId: String,
    fromUsername: String,
    userId: Int,
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var currentPage by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }

    // 设置用户 ID 以启动轮询
    LaunchedEffect(Unit) {
        viewModel.setUserIds(fromUserId.toInt(), userId, currentPage)
    }

    // 观察 ViewModel 的数据，使用默认值为空列表
    val messageDetailData by viewModel.messageRecords.observeAsState(emptyList())

    // 获取消息详情数据
    LaunchedEffect(currentPage) {
        if (!isLoading) {
            isLoading = true
            viewModel.getMessageDetail(
                fromUserId = fromUserId.toInt(),
                userId = userId,
                page = currentPage,
                onSuccess = { data ->
                    isLoading = false
                },
                onError = { errorMessage ->
                    isLoading = false
                    Log.e("Error", "Error: $errorMessage")
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                title = { Text(text = fromUsername, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            ChatInputBar(onSend = { newMessage ->
                // 使用 ViewModel 发送消息
                viewModel.sendMessage(
                    userId = userId,
                    toUserId = fromUserId.toInt(),
                    content = newMessage,
                    onSuccess = {
                        // 发送消息成功后重置分页并刷新消息列表
                        currentPage = 1
                        viewModel.getMessageDetail(
                            fromUserId = fromUserId.toInt(),
                            userId = userId,
                            page = currentPage,
                            onSuccess = { /* 这里不需要做什么 */ },
                            onError = { errorMessage ->
                                Log.e("Error", "Error: $errorMessage")
                            }
                        )
                    },
                    onError = { errorMessage ->
                        Log.e("SendMessage", "Error: $errorMessage")
                    }
                )
            })
        }
    ) { paddingValues ->
        ChatContent(
            messages = messageDetailData,
            currentUserId = userId,
            modifier = Modifier.padding(paddingValues),
            onLoadMore = {
                currentPage += 1
            }
        )
    }
}



@Composable
fun ChatContent(messages: List<Data.MessageRecord>, currentUserId: Int, modifier: Modifier = Modifier, onLoadMore: () -> Unit) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxSize(),
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
                if (message.fromUserId.toInt() == currentUserId) {
                    FromMessageItemView(message)
                } else {
                    ToMessageItemView(message)
                }
            }

            // 加载更多
            item {
                LaunchedEffect(scrollState.firstVisibleItemIndex) {
                    if (scrollState.firstVisibleItemIndex == 0) {
                        onLoadMore()
                    }
                }
            }
        }

        // 自动滚动到最新消息
        LaunchedEffect(messages.size) {
            coroutineScope.launch {
                // 仅当用户没有手动滚动时才自动滚动到最新消息
                if (!scrollState.isScrollInProgress &&
                    scrollState.layoutInfo.visibleItemsInfo.isNotEmpty() &&
                    scrollState.firstVisibleItemIndex == 0) {
                    scrollState.scrollToItem(0)
                }
            }
        }
    }
}



@Composable
fun FromMessageItemView(message: Data.MessageRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 0.dp, max = 300.dp)
                .padding(start = 8.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = Color.Yellow // 设置背景颜色为黄色
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp)
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
fun ToMessageItemView(message: Data.MessageRecord) {
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
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}


@Composable
fun ChatInputBar(onSend: (String) -> Unit) {
    var messageText by remember { mutableStateOf("") }
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
                    .background(Color.White, RoundedCornerShape(28.dp)), // 设置背景和圆角
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
                            onSend(messageText)
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

//表情面板
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


