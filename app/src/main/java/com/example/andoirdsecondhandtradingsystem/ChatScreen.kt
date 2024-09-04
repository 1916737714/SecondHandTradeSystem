package com.example.andoirdsecondhandtradingsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(messageId: Int) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chat for Message ID: $messageId") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7183B8))
            )
        },
        bottomBar = {
            ChatInputBar()
        }
    ) { paddingValues ->
        ChatContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ChatContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 这里可以添加更多的聊天 UI 元素，例如聊天消息列表
        Text(text = "Chat messages go here...")
    }
}

@Composable
fun ChatInputBar() {
    var messageText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text(text = "Type a message") },
            shape = RoundedCornerShape(28.dp) // 设置圆角半径
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { /* Send message */ }) {
            Text("Send")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(messageId = 1)
}