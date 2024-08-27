package com.example.andoirdsecondhandtradingsystem

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

class Login(private val context: Context) {

    // 定义用于用户名和密码的键
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val PASSWORD_KEY = stringPreferencesKey("password")
    private val REMEMBER_USER_KEY = stringPreferencesKey("remember_user")

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("NotConstructor")
    @Composable
    fun Login(modifier: Modifier = Modifier, onRegisterClick: () -> Unit,onLoginSuccess: () -> Unit) {
        var name by remember { mutableStateOf("") } // 用户名状态
        var pwd by remember { mutableStateOf("") } // 密码状态
        var rememberUser by remember { mutableStateOf(false) } // 记住密码状态

        // Coroutine scope 用于启动协程
        val coroutineScope = rememberCoroutineScope()

        // 从 DataStore 中读取保存的数据
        LaunchedEffect(Unit) {
            val userPreferences = context.dataStore.data.first()
            name = userPreferences[USERNAME_KEY] ?: "" // 初始化用户名
            pwd = userPreferences[PASSWORD_KEY] ?: "" // 初始化密码
            rememberUser = userPreferences[REMEMBER_USER_KEY]?.toBoolean() ?: false // 初始化记住用户状态
        }

        val pwdVisualTransformation = PasswordVisualTransformation() // 密码可视化转换
        var showPwd by remember { mutableStateOf(true) } // 显示或隐藏密码状态
        val transformation = if (showPwd) pwdVisualTransformation else VisualTransformation.None // 密码显示转换

        Box(modifier = modifier) {
            Image(
                painter = painterResource(id = R.drawable.login), // 背景图片
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.7f,
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) // 增加圆角
                    .background(Color.White) // 设置背景颜色为白色
                    .padding(40.dp)
                    .align(Alignment.BottomCenter)

            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    placeholder = { Text(stringResource(R.string.username_placeholder)) }, // 使用字符串资源
                    onValueChange = { name = it },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AccountBox, contentDescription = stringResource(R.string.username_description)) // 用户名图标
                    },
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    value = pwd,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.password_placeholder)) }, // 使用字符串资源
                    onValueChange = { pwd = it },
                    visualTransformation = transformation, // 密码可视化转换
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = stringResource(R.string.password_description)) // 密码图标
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
                        checked = rememberUser,
                        onCheckedChange = { rememberUser = it } // 记住密码复选框
                    )
                    Text(text = stringResource(R.string.remember_password), color = Color.Gray) // 使用字符串资源

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(onClick = { onRegisterClick() }) {
                        Text(text = stringResource(R.string.register), // 使用字符串资源
                            color = Color(0xFFFF4081),
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val gradientBrush = Brush.horizontalGradient(colors = listOf(Color(0xFFFFEB3B), Color(0xFFFF9800))) // 渐变背景

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50)) // 圆角
                        .background(gradientBrush) // 渐变背景
                ) {
                    Button(
                        onClick = {
                            // 如果选中了记住密码，则保存数据到 DataStore
                            if (rememberUser) {
                                coroutineScope.launch {
                                    context.dataStore.edit { preferences ->
                                        preferences[USERNAME_KEY] = name // 保存用户名
                                        preferences[PASSWORD_KEY] = pwd // 保存密码
                                        preferences[REMEMBER_USER_KEY] = "true"
                                    }
                                }
                            }else {
                                // 如果未选择记住用户，则可以在此处清除数据
                                coroutineScope.launch {
                                    context.dataStore.edit { preferences ->
                                        preferences[USERNAME_KEY] = "" // 清空用户名
                                        preferences[PASSWORD_KEY] = "" // 清空密码
                                        preferences[REMEMBER_USER_KEY] = "false" // 清空记住用户状态
                                    }
                                }
                            }
                            // TODO: 添加登录功能
                            onLoginSuccess()

                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Text(stringResource(R.string.login), color = Color.White, fontSize = 24.sp) // 使用字符串资源
                    }
                }
            }
        }
    }
}
