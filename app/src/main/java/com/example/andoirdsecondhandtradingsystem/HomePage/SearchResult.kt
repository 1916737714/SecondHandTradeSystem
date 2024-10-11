package com.example.andoirdsecondhandtradingsystem.HomePage

import android.app.appsearch.SearchResult
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.andoirdsecondhandtradingsystem.data.Data

@Composable
fun SearchResult(user: Data.User,navController: NavController,searchText:String){


    var products by remember { mutableStateOf(listOf<Product>()) }
    var errorMessage by remember { mutableStateOf("") }

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
    val products2= isStringPresent(searchText,products)
    if(products2.size==0) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchPage(navController)
            Box(modifier = Modifier.fillMaxSize()
                .padding(top = 50.dp),
                contentAlignment = Alignment.TopCenter
                ) {
            Text(
                text = "没有找到搜寻结果",
                fontSize = 16.sp
            )
        }
    }
    }else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchPage(navController)
            GoodsList(navController = navController, products = products2)
        }
    }
}


fun isStringPresent(searchText: String,products:List<Product>):List<Product> {
    val products2= mutableListOf<Product>()
    for (i in 0 until products.size step 1) {
        val isSubStringPresent = products[i].content.contains(searchText)
        if (isSubStringPresent) {
            products2.add(products[i])
        }
    }
    return products2
}