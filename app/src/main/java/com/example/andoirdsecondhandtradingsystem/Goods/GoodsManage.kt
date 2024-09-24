package com.example.andoirdsecondhandtradingsystem.Goods


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.andoirdsecondhandtradingsystem.data.Data

@Composable
fun GoodsManage(user: Data.User) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabSelectedContentColor = Color.Black // Selected tab content color
    val tabIndicatorColor = Color(0xFFFF5722) // Indicator color

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow( selectedTabIndex = selectedTab,
            contentColor = tabSelectedContentColor,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = tabIndicatorColor
                )
            }) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "发布商品",
                        style = TextStyle(
                            color = if (selectedTab == 0) Color.Red else Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "待发布商品",
                        style = TextStyle(
                            color = if (selectedTab == 1) Color.Red else Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        }
        when (selectedTab) {
            0 -> PublishProductScreen(user)
            1 -> PendingProductScreen()
        }
    }
}



