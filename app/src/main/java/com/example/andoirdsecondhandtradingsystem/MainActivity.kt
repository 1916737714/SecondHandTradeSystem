package com.example.andoirdsecondhandtradingsystem
//当你完成一个模块或者是什么的时候或者是代码修改了，记得提交到远程仓库
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.andoirdsecondhandtradingsystem.HomePage.MyApp

import com.example.andoirdsecondhandtradingsystem.ui.theme.AndoirdSecondHandTradingSystemTheme
import com.example.andoirdsecondhandtradingsystem.data.Data

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AndoirdSecondHandTradingSystemTheme {

            }
                //表情配置
                val config = BundledEmojiCompatConfig(this)
                EmojiCompat.init(config)


                val login = Login(context = this)
                val register = Register()
                var currentScreen by remember { mutableStateOf("Login") }
                var isLoggedIn by remember { mutableStateOf(false) }
                var user by remember { mutableStateOf<Data.User?>(null) }

                BackHandler(currentScreen != "Login") {
                    currentScreen = "Login"
                }

                if(isLoggedIn) {
                    user?.let {
                        MyApp(user = it)
                    }
                }else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // 动画弹簧
                        AnimatedVisibility(
                            visible = currentScreen == "Login",
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(300)),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(animationSpec = tween(300))
                        ) {
                            login.Login(
                                modifier = Modifier.fillMaxSize(),
                                onRegisterClick = {
                                    currentScreen = "Register"
                                },
                                onLoginSuccess = { loggedInUser ->
                                    user = loggedInUser
                                    isLoggedIn = true
                                }
                            )
                        }
                        AnimatedVisibility(
                            visible = currentScreen == "Register",
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(300)),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(animationSpec = tween(300))
                        ) {
                            register.Register(
                                modifier = Modifier.fillMaxSize(),
                                onRegisterSuccess = { currentScreen = "Login" },
                                onError = { errorMessage ->
                                    println("注册失败: $errorMessage")
                                }
                            )
                        }
                    }
                }
            }
        }

}




