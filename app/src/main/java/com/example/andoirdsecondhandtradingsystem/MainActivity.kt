package com.example.andoirdsecondhandtradingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.andoirdsecondhandtradingsystem.ui.theme.AndoirdSecondHandTradingSystemTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndoirdSecondHandTradingSystemTheme {
                val login = Login()
                val start = Start()
                val register = Register()
                var currentScreen by remember { mutableStateOf("Start") }

                //回退
                BackHandler(currentScreen != "Start") {
                    currentScreen = "Start"
                }

                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                    // 动画弹簧
                    AnimatedVisibility(
                        visible = currentScreen == "Start",
                        enter = slideInVertically(initialOffsetY = { -it/2 }) + fadeIn(animationSpec = tween(300)),
                        exit = slideOutVertically(targetOffsetY = { -it/2 }) + fadeOut(animationSpec = tween(300))
                    ) {
                        start.Start(
                            modifier = Modifier.fillMaxSize(),
                            onLoginClick = {
                                currentScreen = "Login"
                            },
                            onRegisterClick = {
                                currentScreen = "Register"
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = currentScreen == "Login",
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(300)),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(animationSpec = tween(300))
                    ) {

                        login.Login(modifier = Modifier.fillMaxSize())
                    }

                    AnimatedVisibility(
                        visible = currentScreen == "Register",
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(300)),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(animationSpec = tween(300))
                    ) {
                        register.Register(modifier = Modifier.fillMaxSize(),
                            onRegisterSuccess = {currentScreen = "Login"})
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    AndoirdSecondHandTradingSystemTheme {
        val register = Register()
        register.Register(onRegisterSuccess = {})
    }
}
