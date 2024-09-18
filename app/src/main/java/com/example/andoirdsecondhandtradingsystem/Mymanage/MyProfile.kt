package com.example.andoirdsecondhandtradingsystem.Mymanage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.andoirdsecondhandtradingsystem.data.Data

@Composable
fun MyProfile(navController: NavController, user: Data.User) {
    var gender by remember { mutableStateOf("男") }
    var bio by remember { mutableStateOf("这是我的简历") }
    var school by remember { mutableStateOf("某某大学") }
    var location by remember { mutableStateOf("某某城市") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "当前登录用户名：${user.username}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = gender,
            onValueChange = { gender = it },
            label = { Text("性别") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        TextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("简历") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        TextField(
            value = school,
            onValueChange = { school = it },
            label = { Text("院校") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("所在地") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* TODO: 保存信息逻辑 */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "保存", color = Color.White, fontSize = 16.sp)
        }
    }
}

