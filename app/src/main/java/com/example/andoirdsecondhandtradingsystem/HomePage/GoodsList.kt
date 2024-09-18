package com.example.andoirdsecondhandtradingsystem.HomePage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson

data class Product(
    val imageUrl:Int,
    val description:String,
    val price:Double,
    val publisher:String,
    val category:String,
    val intx: Int,
    val inty: Int
)

@Composable
fun GoodsList(navController: NavController,products: List<Product>) {
    Box(modifier = Modifier
        .verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)) {
            // 只渲染偶数索引的产品
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(0.5f)
            ) {
                for (i in 0 until products.size step 2) {
                    ProductItem(product = products[i], onClick ={
                        val productJson=Gson().toJson(products[i])
                        navController.navigate("goodsDetail/$productJson")
                    })
                }
            }
            // 只渲染奇数索引的产品（注意这里是从1开始，但索引是从0开始的，所以实际上是i+1为奇数）
            Column (modifier = Modifier
                .padding(4.dp)
            ){
                for (i in 1 until products.size step 2) {
                    ProductItem(product = products[i], onClick = {
                        val productJson=Gson().toJson(products[i])
                        navController.navigate("goodsDetail/$productJson")
                    })
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product,onClick:()->Unit) {
    Column(
        modifier = Modifier
            .clickable (onClick=onClick)
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(2.dp, Color.White), RoundedCornerShape(16.dp))
            .fillMaxWidth() // 填充单元格宽度
            .padding(4.dp)
            .background(Color(0xFFF0F0F0))
    ) {
        // 假设你有一个加载网络图片的函数，这里使用本地资源作为示例
        // Image(painter = rememberImagePainter(data = product.imageUrl), ...)
        RoundedImage(product = product)
        Text(
            text = product.description,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center,
            maxLines = 1, // 限制描述文字的行数
            //overflow = TextOverflow.Ellipsis // 超出部分显示省略号
        )
        Text(text = "￥ ${product.price}",color= Color.Red)
        Text(text = "${product.publisher}",color=Color.Black)
    }
}

@Composable
fun RoundedImage(product: Product){
    Box(
        contentAlignment = Alignment.Center,
        modifier=Modifier.clip(shape = RoundedCornerShape(16.dp))
    ){
        Image(
            painter = painterResource(id= product.imageUrl), // 使用本地资源
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(product.intx.dp, product.inty.dp) ,
            // 设置图片大小
        )
    }
}

@Composable
fun DetailImage(product: Product){
    Box(
        contentAlignment = Alignment.Center,
        modifier=Modifier.clip(shape = RoundedCornerShape(16.dp))
    ){
        Image(
            painter = painterResource(id= product.imageUrl), // 使用本地资源
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()

            // 设置图片大小
        )
    }
}




