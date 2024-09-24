package com.example.andoirdsecondhandtradingsystem.Goods

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import com.example.andoirdsecondhandtradingsystem.R

//待发布商品界面
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalWearMaterialApi::class
)
@Composable
fun PendingProductScreen() {
    var products by remember {
        mutableStateOf(
            List(10) { index ->
                Product(
                    name = "商品名称 $index",
                    price = "￥${index * 10 + 0.99}",
                    imageRes = R.drawable.image5 // 替换为实际的图片资源ID
                )
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(products, key = { product -> product.id }) { product ->
            val dismissState = rememberSwipeToDismissBoxState()

            if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                products = products.filter { it.id != product.id }
            }

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false,// 仅允许左滑删除
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .animateItemPlacement(),
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                            .padding(10.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(painter = painterResource(id = R.drawable.delete),
                            contentDescription = "删除",
                            tint = Color.White,
                            modifier = Modifier
                                .size(48.dp)
                        )
                    }
                },
                content = {
                    ProductCard(product = product)
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
//            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = product.imageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(product.name, style = MaterialTheme.typography.titleLarge)
                        Text(product.price, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

data class Product(
    val name: String,
    val price: String,
    val imageRes: Int,
    val id: Int = name.hashCode() // 确保每个产品有唯一的 ID
)