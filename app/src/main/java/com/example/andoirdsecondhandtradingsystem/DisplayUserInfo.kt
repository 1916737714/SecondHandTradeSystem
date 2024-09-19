package com.example.andoirdsecondhandtradingsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.andoirdsecondhandtradingsystem.data.Data

@Composable
fun DisplayUserInfo(navController: NavHostController, user: Data.User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))  // 设置背景颜色
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 显示当前登录用户名
        Text(
            text = "当前登录用户名：${user.username}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 显示用户ID
        Text(
            text = "用户ID：${user.id}",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF333333)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 添加按钮
        Button(
            onClick = { /* TODO: Handle "我的资料" Click */
                navController.navigate("my_profile/${user.username}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "我的资料", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Handle "我的交易记录" Click */
                navController.navigate("my_transactions/${user.username}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "我的交易记录", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Handle "我发布的商品" Click */
                navController.navigate("my_products/${user.username}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "我发布的商品", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Handle "余额充值" Click */
                navController.navigate("recharge/${user.username}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "余额充值", color = Color.White, fontSize = 16.sp)
        }
    }
}