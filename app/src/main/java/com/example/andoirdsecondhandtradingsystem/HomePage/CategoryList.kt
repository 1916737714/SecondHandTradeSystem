package com.example.andoirdsecondhandtradingsystem.HomePage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener


//@Composable
//fun CategoryList(categories: List<String>,onCategorySelected:(String)->Unit) {
//
//    LazyRow(
//        contentPadding = PaddingValues(horizontal = 8.dp), // 设置水平方向的内边距
//        horizontalArrangement = Arrangement.spacedBy(16.dp), // 设置子项之间的间距
//        verticalAlignment = Alignment.CenterVertically // 设置子项在垂直方向上的对齐方式
//    ) {
//        items(categories) { category ->
//            CategoryItem(category = category,
//                onClick = {})
//        }
//    }
//}


@Composable
fun CategoryList(categories: List<String>,onCategorySelected:(String)->Unit) {
    val selectedCategory=remember {
        mutableStateOf<String?>(null)
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp), // 设置水平方向的内边距
        horizontalArrangement = Arrangement.spacedBy(16.dp), // 设置子项之间的间距
        verticalAlignment = Alignment.CenterVertically // 设置子项在垂直方向上的对齐方式
    ) {
        items(categories) { category ->
            val isSelected=category==selectedCategory.value
            Text(
                text = category,
                fontSize = 24.sp,
                color = if(isSelected)Color.Red else Color.Black,
                modifier = Modifier
                    .height(48.dp)
                    .clickable{
                        selectedCategory.value=category
                        onCategorySelected(category)
                    }
            )
        }
    }
}


//@Composable
//fun CategoryItem(category: String,onClick:()->Unit) {
//    // 你可以在这里定义每个分类项的布局
//    // 这里我们简单地使用Text来显示分类名称
//    Box(contentAlignment = Alignment.Center,
//        modifier = Modifier
//            .background(Color.Transparent)
//            .clickable(onClick = onClick)
//            .padding(8.dp)){
//        Text(
//            text = category,
//            fontSize=18.sp,
//            modifier = Modifier
//                // 设置Text的内边距
//                .height(36.dp) // 设置固定高度（可选）
//                .fillMaxWidth() // 填充剩余空间（可选，但在这个场景中可能不是必需的）
//        )
//    }
//}