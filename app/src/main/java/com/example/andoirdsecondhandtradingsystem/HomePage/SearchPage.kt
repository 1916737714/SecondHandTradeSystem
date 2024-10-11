package com.example.andoirdsecondhandtradingsystem.HomePage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SearchPage(navController: NavController) {
    var searchText by remember {
        mutableStateOf("")
    }
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {searchText=it},
//            label = { Text(text="请输入查询内容") },
            shape = RoundedCornerShape(36.dp),
            modifier = Modifier
                .height(56.dp)
                .padding(6.dp)
        )
        Button("搜索",modifier=Modifier.fillMaxWidth(), onClick = {
            navController.navigate("searchResult?query=$searchText")
        })

    }
}


@Composable
fun Button(string: String,modifier: Modifier,onClick:()->Unit){
    val gradientBrush = Brush.horizontalGradient(colors = listOf(Color(0xFFFFEB3B), Color(0xFFFF9800))) // 渐变背景
    Box(
        modifier = Modifier
            .height(50.dp)
            .clip(RoundedCornerShape(50)) // 圆角
            .background(gradientBrush) // 渐变背景
    ) {
        Button(onClick = onClick,
            modifier = modifier,
            contentPadding = PaddingValues(8.dp),
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Text(string, color = Color.White, fontSize = 16.sp) // 使用字符串资源
        }
    }
}