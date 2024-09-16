package com.example.andoirdsecondhandtradingsystem

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import com.example.andoirdsecondhandtradingsystem.data.AuthViewModel
import com.example.andoirdsecondhandtradingsystem.data.Data
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
    fun Login(
        modifier: Modifier = Modifier,
        onRegisterClick: () -> Unit,
        onLoginSuccess: (Data.User) -> Unit,  // 修改回调以传递用户信息
        viewModel: AuthViewModel = AuthViewModel()
    ) {
        var name by remember { mutableStateOf("") }
        var pwd by remember { mutableStateOf("") }
        var rememberUser by remember { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            val userPreferences = context.dataStore.data.first()
            name = userPreferences[USERNAME_KEY] ?: ""
            pwd = userPreferences[PASSWORD_KEY] ?: ""
            rememberUser = userPreferences[REMEMBER_USER_KEY]?.toBoolean() ?: false
        }

        val pwdVisualTransformation = PasswordVisualTransformation()
        var showPwd by remember { mutableStateOf(true) }
        val transformation = if (showPwd) pwdVisualTransformation else VisualTransformation.None

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
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(40.dp)
                    .align(Alignment.BottomCenter)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    placeholder = { Text(stringResource(R.string.username_placeholder)) },
                    onValueChange = { name = it },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AccountBox, contentDescription = stringResource(R.string.username_description))
                    },
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    value = pwd,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.password_placeholder)) },
                    onValueChange = { pwd = it },
                    visualTransformation = transformation,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = stringResource(R.string.password_description))
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
                        onCheckedChange = { rememberUser = it }
                    )
                    Text(text = stringResource(R.string.remember_password), color = Color.Gray)

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(onClick = { onRegisterClick() }) {
                        Text(
                            text = stringResource(R.string.register),
                            color = Color(0xFFFF4081),
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val gradientBrush = Brush.horizontalGradient(colors = listOf(Color(0xFFFFEB3B), Color(0xFFFF9800)))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .background(gradientBrush)
                ) {
                    Button(
                        onClick = {
                            Log.d("Login666", "Login successful:")
                            if (rememberUser) {
                                coroutineScope.launch {
                                    context.dataStore.edit { preferences ->
                                        preferences[USERNAME_KEY] = name
                                        preferences[PASSWORD_KEY] = pwd
                                        preferences[REMEMBER_USER_KEY] = "true"
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    context.dataStore.edit { preferences ->
                                        preferences[USERNAME_KEY] = ""
                                        preferences[PASSWORD_KEY] = ""
                                        preferences[REMEMBER_USER_KEY] = "false"
                                    }
                                }
                            }

                            viewModel.loginUser(
                                username = name,
                                password = pwd,
                                onSuccess = { user ->
                                    Log.d("Login666", "Login successful: $user")
                                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess(user)  // 传递用户信息
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                    Log.e("Login666", "Login successful: $errorMessage")

                                }
                            )

                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Text(stringResource(R.string.login), color = Color.White, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
