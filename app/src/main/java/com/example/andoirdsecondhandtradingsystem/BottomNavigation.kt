package com.example.andoirdsecondhandtradingsystem

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


//底部导航栏
@Composable
fun MainContent() {
    var selectedScreen by remember { mutableStateOf<ScreenPage>(ScreenPage.Home) }
    var topBarTitle by remember { mutableStateOf("Home") }  // 用于存储顶部标题

    Scaffold(
        topBar = {
            TopBar(title = topBarTitle) // 显示顶部导航栏
        },
        bottomBar = {
            BottomNavigationBar(selectedScreen) { screen ->
                selectedScreen = screen
                // 更新 topBarTitle
                topBarTitle = when (screen) {
                    ScreenPage.Home -> "首页"
                    ScreenPage.Love -> "收藏"
                    ScreenPage.Capture -> "Capture"
                    ScreenPage.Message -> "消息"
                    ScreenPage.Mine -> "我的信息"
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    TopAppBar(
        title = { Text(text = title) },
    )
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
                            contentDescription = if (screen.resId != 0) stringResource(id = screen.resId) else null,
                            modifier = if (screen.resId != 0) Modifier.size(24.dp) else Modifier.size(40.dp),
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
    icon: @Composable (Color) -> Unit,
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    alwaysShowLabel: Boolean
) {
    val contentColor = if (selected) Color(0xFFFFD700) else Color(0xFF000000)

    Box(
        modifier = Modifier
            .clickable(onClick = onClick,
                indication = null, // 这里取消点击效果的指示器
                interactionSource = remember { MutableInteractionSource() } // 取消点击时的阴影效果
            )
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
            .background(Color.Transparent)
            .navigationBarsPadding() //调整底部导航栏在虚拟操作栏上方
    ) {
        content()
    }
}


