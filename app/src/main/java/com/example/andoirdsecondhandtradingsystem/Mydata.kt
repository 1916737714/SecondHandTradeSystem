package com.example.andoirdsecondhandtradingsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.andoirdsecondhandtradingsystem.Mymanage.MyProfile
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.example.androidsecondhandtradingsystem.MyAmount
import com.example.androidsecondhandtradingsystem.MyMerchandise

import androidx.compose.ui.unit.sp

import com.example.androidsecondhandtradingsystem.MyOrders
import com.example.androidsecondhandtradingsystem.MySoldItems

@Composable
fun MineScreen(navController: NavHostController, user: Data.User) {
    NavHost(navController = navController, startDestination = "my_data") {
        composable("my_data") {
            Mydata(navController = navController, user = user)
        }
        composable("my_profile/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            MyProfile(navController = navController, user = user)
        }
        composable("my_merchandise/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            MyMerchandise(navController = navController, user = user)
        }
        composable("my_orders/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            MyOrders(navController = navController, user = user)
        }
        composable("my_amount/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            MyAmount(navController = navController, user = user)
        }
        // 新增路由
        composable("my_sold_items/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            MySoldItems(navController = navController, user = user)
        }
    }
}

@Composable
fun Mydata(navController: NavHostController, user: Data.User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))  // 设置背景颜色
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 可选的顶部图标或图片
        Text(
            text = "当前登录用户名：${user.username}",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color(0xFF333333)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 添加按钮
        Button(
            onClick = { navController.navigate("my_profile/${user.username}") },
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
            onClick = { navController.navigate("my_merchandise/${user.username}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "我发布的商品", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("my_orders/${user.username}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "我的订单", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("my_sold_items/${user.username}") }, // 新增的按钮
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // 选择一个合适的颜色
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "我卖出的物品", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("my_amount/${user.username}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "金额充值", color = Color.White, fontSize = 16.sp)
        }
    }
}
