package com.example.andoirdsecondhandtradingsystem

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Login {

@SuppressLint("NotConstructor")
@Composable
fun Login(message: String,from: String, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var pwd by remember { mutableStateOf("") }

    val pwdVisualTransformation = PasswordVisualTransformation()
    var showPwd by remember { mutableStateOf(true) }

    val transformation = if (showPwd) pwdVisualTransformation else VisualTransformation.None

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.image5),
            contentDescription = null,
            contentScale = ContentScale.Crop,//全屏填充
            modifier = Modifier.fillMaxSize(),//填充全屏
            alpha = 0.5f,//透明度
        )
        Column() {
            Spacer(modifier = Modifier
                .weight(3f)
                .clip(RoundedCornerShape(16.dp)))
            Column (
                modifier = Modifier
                    .weight(3f)
                    .background(color = Color.White)
                    .padding(40.dp)
                    .fillMaxSize()
            ){
                Column() {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = name,
                        placeholder = { Text("用户名") },
                        onValueChange = {str -> name = str},

                        leadingIcon = {
                            Icon(imageVector = Icons.Default.AccountBox,
                                contentDescription = "用户名")
                        }


                    )
                    TextField(
                        value = pwd,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("密码") },
                        onValueChange = {str -> pwd = str},
                        visualTransformation = transformation,

                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock,
                                contentDescription = "密码")
                        },
                        trailingIcon = {
                            if (showPwd) {
                                IconButton(onClick = { showPwd = !showPwd }) {
                                    Icon(painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                        contentDescription = null,
                                        Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                IconButton(onClick = { showPwd = !showPwd }) {
                                    Icon(painter = painterResource(id = R.drawable.ic_launcher_background),
                                        contentDescription = null,
                                        Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween,modifier = Modifier.fillMaxWidth()) {
                    Text(text = "记住密码", fontSize = 16.sp, color = Color.Gray)
                    Text(text = "注册", fontSize = 16.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(12.dp,16.dp)
                ) {
                    Text("登录",color = Color.White,fontSize = 16.sp)
                }
            }
        }
    }
}
}