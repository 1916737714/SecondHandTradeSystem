package com.example.andoirdsecondhandtradingsystem.HomePage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.andoirdsecondhandtradingsystem.R

@Composable
fun CategoryTransform(navController: NavController){
    val selectedCategory = remember {
        mutableStateOf("推荐")
    }

    CategoryList(categories = listOf("推荐","美食","电器","服饰","家具","生活","科技","旅行"),
        onCategorySelected={
            category->
            selectedCategory.value=category
        },"推荐")

    val productsByCategory= mapOf(

    "推荐" to listOf(
        Product(R.drawable.goods1,"好吃好吃好吃好吃好吃好吃hahhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh",88.0,"史玉程","美食",200,240),
        Product(R.drawable.goods2,"更好吃",99.0,"郭佳灵","科技",200,160),
    ),
    "美食" to listOf(
        Product(R.drawable.goods1,"好吃好吃好吃好吃好吃好吃",88.0,"史玉程","美食",200,240),
        Product(R.drawable.goods2,"更好吃",99.0,"郭佳灵","科技",200,160),
        Product(R.drawable.image5,"太好吃",100.0,"cxk","旅游",200,240),
    ),
        "电器" to listOf(
            Product(R.drawable.goods1,"好吃好吃好吃好吃好吃好吃",88.0,"史玉程","美食",200,240),
            Product(R.drawable.goods2,"更好吃",99.0,"郭佳灵","科技",200,160),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","旅游",200,240),
            Product(R.drawable.image5,"byd太好吃",100.0,"cxk","生活",200,240),
        ),
        "服饰" to listOf(
            Product(R.drawable.goods1,"好吃好吃好吃好吃好吃好吃",88.0,"史玉程","美食",200,240),
            Product(R.drawable.goods2,"更好吃",99.0,"郭佳灵","科技",200,160),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","旅游",200,240),
            Product(R.drawable.image5,"byd太好吃",100.0,"cxk","生活",200,240),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","家电",200,240),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","服饰",200,240)
        ),
        "家具" to listOf(
            Product(R.drawable.goods1,"好吃好吃好吃好吃好吃好吃",88.0,"史玉程","美食",200,240),
            Product(R.drawable.goods2,"更好吃",99.0,"郭佳灵","科技",200,160),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","旅游",200,240),
            Product(R.drawable.image5,"byd太好吃",100.0,"cxk","生活",200,240),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","家电",200,240),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","服饰",200,240)
        ),
        "生活" to listOf(
            Product(R.drawable.goods1,"好吃好吃好吃好吃好吃好吃",88.0,"史玉程","美食",200,240),
            Product(R.drawable.goods2,"更好吃",99.0,"郭佳灵","科技",200,160),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","旅游",200,240),
            Product(R.drawable.image5,"byd太好吃",100.0,"cxk","生活",200,240),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","家电",200,240),
        ),
        "科技" to listOf(
            Product(R.drawable.goods1,"好吃好吃好吃好吃好吃好吃",88.0,"史玉程","美食",200,240),
            Product(R.drawable.goods2,"更好吃",99.0,"郭佳灵","科技",200,160),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","家电",200,240),
        ),
        "旅行" to listOf(
            Product(R.drawable.goods1,"好吃好吃好吃好吃好吃好吃",88.0,"史玉程","美食",200,240),
            Product(R.drawable.goods2,"更好吃",99.0,"郭佳灵","科技",200,160),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","旅游",200,240),
            Product(R.drawable.image5,"byd太好吃",100.0,"cxk","生活",200,240),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","家电",200,240),
            Product(R.drawable.image5,"太好吃",100.0,"cxk","服饰",200,240)
        )
    )


//    PageTransform(navController, products = productsByCategory[selectedCategory.value]?: emptyList())
    GoodsList(navController,products = productsByCategory[selectedCategory.value]?: emptyList())

//    GoodsList(productsByCategory[selectedCategory.value]?: emptyList(), navController )

}

