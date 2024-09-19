package com.example.andoirdsecondhandtradingsystem.HomePage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.andoirdsecondhandtradingsystem.MainContent
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.google.gson.Gson

@Composable
fun MyApp(user: Data.User?){
    val navController= rememberNavController()
    NavHost(navController = navController, startDestination = "mainContent"){
        composable("mainContent"){
            if (user != null) {
                MainContent(user,navController)
            }
        }
        composable("goodsDetail/{productJson}"){
            backStackEntry->
            val productJson=backStackEntry.arguments?.getString("productJson")
            val product=Gson().fromJson(productJson,Product::class.java)
            GoodsDetail(navController,product = product)
        }
    }
}


//@Composable
//fun PageTransform(navController: NavHostController, startDestination:String="goodsList", products: List<Product>){
//
//    NavHost(navController = navController, startDestination="goodList"){
//        composable("goodsList"){ GoodsList(navController,products) }
//        composable("goodsDetail/{productJson}"){
//                backStackEntry->
//            val productJson=backStackEntry.arguments?.getString("productJson")
//            val product= Gson().fromJson(productJson,Product::class.java)
//            GoodsDetail(product=product)
//        }
//    }
//}