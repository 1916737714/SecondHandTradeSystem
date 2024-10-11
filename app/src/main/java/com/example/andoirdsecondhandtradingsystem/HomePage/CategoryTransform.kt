package com.example.andoirdsecondhandtradingsystem.HomePage

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.andoirdsecondhandtradingsystem.R
import com.example.andoirdsecondhandtradingsystem.data.AuthViewModel
import com.example.andoirdsecondhandtradingsystem.data.Data

@Composable
fun CategoryTransform(navController: NavController,viewModel: AuthViewModel = viewModel(),user:Data.User){

    var goodsTypeList by remember { mutableStateOf<List<Data.goodsType>>(emptyList()) }
    viewModel.getAllGoodsType(
        onSuccess = { list ->
            goodsTypeList = list
        },
        onError = {
            // 处理错误逻辑
            Log.e("GoodsMangeError", it)
        }
    )
    val categoryList=listOf("推荐")+goodsTypeList.map{it.type}

    val selectedCategory= remember {
        mutableStateOf(categoryList.firstOrNull()?:"")
    }


    CategoryList(categoryList,{selectedCategory.value=it},"推荐")

    var products by remember { mutableStateOf(listOf<Product>())}
    var errorMessage by remember { mutableStateOf("")}

    LaunchedEffect(Unit) {
        getGoods(user,{
            list ->
            products=list
        },{
            error ->
            errorMessage=error
            Log.e("GoodsFetchError",error)
        })
    }

    val productsByCategory = mapOf(
        "推荐" to products,
        "手机" to getList(products,1),
        "奢品" to getList(products,2),
        "潮品" to getList(products,3),
        "美妆" to getList(products,4),
        "数码" to getList(products,5),
        "潮玩" to getList(products,6),
        "游戏" to getList(products,7),
        "图书" to getList(products,8),
        "美食" to getList(products,9),
        "文玩" to getList(products,10),
        "母婴" to getList(products,11),
        "家居" to getList(products,12),
        "乐器" to getList(products,13),
        "其他" to getList(products,14),
    )

    GoodsList(
        navController,
         productsByCategory[selectedCategory.value] ?: emptyList()
    )


}


fun getList(products:List<Product>,typeId:Int):List<Product>{
    val filteredProducts = products.filter {it.typeId == typeId}
    return filteredProducts
}

