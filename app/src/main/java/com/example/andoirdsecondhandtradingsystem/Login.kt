package com.example.andoirdsecondhandtradingsystem

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Login {

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("NotConstructor")
    @Composable
    fun Login(modifier: Modifier = Modifier) {
        var name by remember { mutableStateOf("") }
        var pwd by remember { mutableStateOf("") }

        val pwdVisualTransformation = PasswordVisualTransformation()
        var showPwd by remember { mutableStateOf(true) }
        val transformation = if (showPwd) pwdVisualTransformation else VisualTransformation.None

        val rememberUser = remember { mutableStateOf(false) }

        Box(modifier = modifier) {
            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.7f,
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) // 增加圆角
                    .background(Color.White) // 背景设置为白色
                    .padding(40.dp)
                    .align(Alignment.BottomCenter)


            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    placeholder = { Text("用户名") },
                    onValueChange = { name = it },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AccountBox, contentDescription = "用户名")
                    },
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    value = pwd,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("密码") },
                    onValueChange = { pwd = it },
                    visualTransformation = transformation,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "密码")
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPwd = !showPwd }) {
                            Icon(
                                painter = painterResource(id = if (showPwd) R.drawable.eye_show else R.drawable.eye_hide),
                                contentDescription = null,
                                Modifier.size(24.dp)
                            )
                        }
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberUser.value,
                            onCheckedChange = { rememberUser.value = it }
                        )
                        Text(text = "记住密码", color = Color.Gray)
                    }


                Spacer(modifier = Modifier.height(16.dp))

                // 创建渐变颜色的画刷
                val gradientBrush = Brush.horizontalGradient(colors = listOf(Color(0xFFFFEB3B), Color(0xFFFF9800)))

                // 使用 Box 来设置背景渐变
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50)) // 圆角
                        .background(gradientBrush) // 渐变背景
                ) {
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("登录", color = Color.White, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
