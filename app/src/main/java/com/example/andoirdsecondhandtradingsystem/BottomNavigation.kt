package com.example.andoirdsecondhandtradingsystem

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.andoirdsecondhandtradingsystem.Goods.GoodsManage
import com.example.andoirdsecondhandtradingsystem.Message.AppNavigation
import com.example.andoirdsecondhandtradingsystem.data.Data

@Composable
fun MainContent(user: Data.User, navController1: NavController) {
    var selectedScreen by remember { mutableStateOf<ScreenPage>(ScreenPage.Home) }
    var topBarTitle by remember { mutableStateOf("Home") }
    val navController = rememberNavController()
    var showBars by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            if (showBars) {
                TopBar(title = topBarTitle)
            }
        },
        bottomBar = {
            if (showBars) {
                BottomNavigationBar(selectedScreen) { screen ->
                    selectedScreen = screen
                    topBarTitle = when (screen) {
                        ScreenPage.Home -> "首页"
                        ScreenPage.Love -> "收藏"
                        ScreenPage.Capture -> "商品管理"
                        ScreenPage.Message -> "消息"
                        ScreenPage.Mine -> "我的信息"
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (selectedScreen) {
                    is ScreenPage.Home -> com.example.andoirdsecondhandtradingsystem.Home.AppNavigation(navController = navController, user = user)
                    is ScreenPage.Love -> Text(text = "Love Screen")
                    is ScreenPage.Capture -> GoodsManage(user)
                    is ScreenPage.Message -> AppNavigation(navController, selectedScreen, user) { showBars = it }
                    is ScreenPage.Mine -> MineScreen(navController = navController, user = user) { showBars = it }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    TopAppBar(
        title = { Text(text = title) }
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
                    icon = { color ->
                        Icon(
                            painter = painterResource(id = screen.iconSelect),
                            contentDescription = if (screen.resId != 0) stringResource(id = screen.resId) else null,
                            modifier = if (screen.resId != 0) Modifier.size(24.dp) else Modifier.size(40.dp),
                            tint = color
                        )
                    },
                    label = {
                        if (screen.resId != 0) {
                            Text(text = stringResource(id = screen.resId))
                        }
                    },
                    selected = screen == selectedScreen,
                    onClick = { onScreenSelected(screen) },
                    alwaysShowLabel = screen.isShowText
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
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            icon(contentColor)
            if (alwaysShowLabel || selected) {
                label()
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
            .navigationBarsPadding()
    ) {
        content()
    }
}