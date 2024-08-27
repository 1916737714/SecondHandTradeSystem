package com.example.andoirdsecondhandtradingsystem

import ScreenPage
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.andoirdsecondhandtradingsystem.ui.theme.AndoirdSecondHandTradingSystemTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndoirdSecondHandTradingSystemTheme {
                val login = Login(context = this)
                val register = Register()
                var currentScreen by remember { mutableStateOf("Login") }


                var isLoggedIn by remember { mutableStateOf(false) }

                //回退
                BackHandler(currentScreen != "Login") {
                    currentScreen = "Login"
                }

                if(isLoggedIn) {
                    MainContent()
                }else
                {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                        // 动画弹簧

                        // 登录
                        AnimatedVisibility(
                            visible = currentScreen == "Login",
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(300)),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(animationSpec = tween(300))
                        ) {

                            login.Login(modifier = Modifier.fillMaxSize(),
                                onRegisterClick = {
                                    currentScreen = "Register"
                                },
                                onLoginSuccess = {
                                    isLoggedIn = true // 更新登录状态
                                })
                        }
                        //注册
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
}

//@Preview(showBackground = true)
//@Composable
//fun RegisterPreview() {
//    AndoirdSecondHandTradingSystemTheme {
//        val register = Register()
//        register.Register(onRegisterSuccess = {})
//    }
//}

//底部导航栏
@Composable
fun MainContent() {
    var selectedScreen by remember { mutableStateOf<ScreenPage>(ScreenPage.Home) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedScreen) { screen ->
                selectedScreen = screen
            }
        }
    ) { paddingValues ->
        // 显示当前选择的屏幕内容
        Column(
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding()) // 确保底部有足够的空间
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (selectedScreen) {
                is ScreenPage.Home -> Text(text = "Home Screen")
                is ScreenPage.Love -> Text(text = "Love Screen")
                is ScreenPage.Capture -> Text(text = "Capture Screen")
                is ScreenPage.Message -> Text(text = "Message Screen")
                is ScreenPage.Mine -> Text(text = "Mine Screen")
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedScreen: ScreenPage, onScreenSelected: (ScreenPage) -> Unit) {
    BottomNavigation {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val screens = listOf(ScreenPage.Home, ScreenPage.Love, ScreenPage.Capture, ScreenPage.Message, ScreenPage.Mine)

            screens.forEach { screen ->
                BottomNavigationItem(
                    icon = { color -> // 接受颜色参数
                        Icon(
                            painter = painterResource(id = screen.iconSelect),
                            contentDescription = stringResource(id = screen.resId),
                            modifier = Modifier.size(24.dp),
                            tint = color // 使用传递的颜色
                        )
                    },
                    label = {
                        // 只有当 resId 不是 0 时才显示标签
                        if (screen.resId != 0) {
                            Text(text = stringResource(id = screen.resId))
                        }
                    },
                    selected = screen == selectedScreen,
                    onClick = { onScreenSelected(screen) },
                    alwaysShowLabel = screen.isShowText // 根据 isShowText 决定是否显示标签
                )
            }
        }
    }
}



@SuppressLint("RememberReturnType")
@Composable
fun BottomNavigationItem(
    icon: @Composable (androidx.compose.ui.graphics.Color) -> Unit,
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    alwaysShowLabel: Boolean
) {
//    val contentColor = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
    // 背景颜色：选中时为黄色，未选中时黑
    val contentColor = if (selected) Color(0xFFFFD700) else Color(0xFF000000)

    Box(
        modifier = Modifier
            .clickable(onClick = onClick,
                indication = null, // 这里取消点击效果的指示器
                interactionSource = remember { MutableInteractionSource() } // 取消点击时的阴影效果
                )
//            .background(backgroundColor)

            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            icon(contentColor) // 显示图标
            if (alwaysShowLabel || selected) {
                label() // 显示标签
            }
        }
    }
}

@Composable
fun BottomNavigation(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.primary)
            .background(androidx.compose.ui.graphics.Color.Transparent)
            .navigationBarsPadding()//调整底部导航栏在虚拟操作栏上方


    ) {

        content()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainContent() {
    AndoirdSecondHandTradingSystemTheme {
        MainContent()
    }
}




//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MyApp() {
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("My App") })
//        },
//        bottomBar = {
//            BottomAppBar {
//                Text("Bottom Bar")
//            }
//        },
//        content = { paddingValues ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Hello World!")
//            }
//        }
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun Preview() {
//    AndoirdSecondHandTradingSystemTheme {
//        MyApp()
//    }
//}