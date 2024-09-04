package com.example.andoirdsecondhandtradingsystem

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.andoirdsecondhandtradingsystem.data.AuthViewModel

class Register {




    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("NotConstructor")
    @Composable
    fun Register(modifier: Modifier = Modifier,
                 onRegisterSuccess:()->Unit,
                 onError: (String) -> Unit,
                 viewModel: AuthViewModel = AuthViewModel()
                 ){

        //获取到内容
        var Account by remember { mutableStateOf("") }
        var Firstpwd by remember { mutableStateOf("") }
        var Secondpwd by remember { mutableStateOf("") }
        var isDialogVisible by remember { mutableStateOf(false) }
        var dialogMessage by remember { mutableStateOf("") }

        //控制眼睛变化
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
                    value = Account,
                    placeholder = { Text("请输入用户名") },
                    onValueChange = { Account = it },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AccountBox, contentDescription = "用户名")
                    },
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    value = Firstpwd,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("请输入密码") },
                    onValueChange = { Firstpwd = it },
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
                TextField(
                    value = Secondpwd,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("请再次输入密码") },
                    onValueChange = { Secondpwd = it },
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
                        onClick = { /*TODO*/
                                    if (Firstpwd == Secondpwd) {
                                        viewModel.registerUser(Account, Firstpwd, onSuccess = {
                                            dialogMessage = "注册成功"
                                            isDialogVisible = true // 显示成功弹窗
                                            onRegisterSuccess()
                                        }, onError = { errorMessage ->
                                            dialogMessage = errorMessage
                                            isDialogVisible = true // 显示错误弹窗
                                        })
                                    } else {
                                        dialogMessage = "两次输入的密码不一致"
                                        isDialogVisible = true // 显示错误弹窗
                                    }
                                  },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("注册", color = Color.White, fontSize = 24.sp)
                    }
                }
                // 弹窗显示逻辑
                if (isDialogVisible) {
                    AlertDialog(
                        onDismissRequest = { isDialogVisible = false },
                        title = { Text(text = "提示") },
                        text = { Text(dialogMessage, fontSize = 24.sp) },
                        confirmButton = {
                            Button(
                                onClick = { isDialogVisible = false
                                    onRegisterSuccess() }
                            ) {
                                Text("确定",)
                            }
                        },
                        dismissButton = null
                    )
                }


            }
        }
    }   
}