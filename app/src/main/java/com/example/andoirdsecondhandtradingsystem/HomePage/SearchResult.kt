package com.example.andoirdsecondhandtradingsystem.HomePage

import android.app.appsearch.SearchResult
import android.os.NetworkOnMainThreadException
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.andoirdsecondhandtradingsystem.Home.ApiResponse
import com.example.andoirdsecondhandtradingsystem.Home.GoodsItem
import com.example.andoirdsecondhandtradingsystem.Home.GoodsItemView
import com.example.andoirdsecondhandtradingsystem.Home.GoodsResponse
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

@Composable
fun SearchResult(user: Data.User,navController: NavController,searchText:MutableState<String>){


//    var products by remember { mutableStateOf(listOf<Product>()) }
//    var errorMessage by remember { mutableStateOf("") }
//
////    LaunchedEffect(Unit) {
////        getGoods(user,1,{
////                list ->
////            products=list
////        },{
////                error ->
////            errorMessage=error
////            Log.e("GoodsFetchError",error)
////        })
////    }
////    getAllProducts(user, onSuccess = {
////            list->
////        products=list
////    },
////        onError = {
////                error->
////            errorMessage=error
////            Log.e("GoodsFetchError",error)
////        })
//
//    val products2= isStringPresent(searchText,products)
//    if(products2.size==0) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = 50.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            SearchPage(navController)
//            Box(modifier = Modifier.fillMaxSize()
//                .padding(top = 50.dp),
//                contentAlignment = Alignment.TopCenter
//                ) {
//            Text(
//                text = "没有找到搜寻结果",
//                fontSize = 16.sp
//            )
//        }
//    }
//    }else {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = 50.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            SearchPage(navController)
//            GoodsList(navController = navController, products = products2)
//        }
//    }
//}
//
//
//fun isStringPresent(searchText: String,products:List<Product>):List<Product> {
//    val products2= mutableListOf<Product>()
//    for (i in 0 until products.size step 1) {
//        val isSubStringPresent = products[i].content.contains(searchText)
//        if (isSubStringPresent) {
//            products2.add(products[i])
//        }
//    }
//    return products2
    var goodsItems by rememberSaveable { mutableStateOf(listOf<GoodsItem>()) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    var currentPage by rememberSaveable { mutableStateOf(1) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var hasMore by rememberSaveable { mutableStateOf(true) }

    val swipeRefreshState = remember { SwipeRefreshState(isRefreshing) }
    val gridState = rememberLazyGridState()


//    // 获取 SharedPreferences 实例
//    val sharedPreferences = LocalContext.current.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//
//// 恢复选中的分类
//    val savedCategory = sharedPreferences.getString("selectedCategory", "") ?: ""
//
//    selectedCategory.value=savedCategory


    // 获取商品数据
    fun fetchGoods(page: Int,searchText: String) {
        if (isLoading || !hasMore) return

        isLoading = true
        fetchGoods(user, page, searchText,{ list, total ->
            if (page == 1) {
                goodsItems = list
            } else {
                goodsItems = goodsItems + list
            }
            hasMore = goodsItems.size < total
            isRefreshing = false
            isLoading = false
        }, { error ->
            errorMessage = error
            isRefreshing = false
            isLoading = false
        })
    }


    // 在Composable中启动网络请求
    LaunchedEffect(searchText.value) {
        currentPage=1
        hasMore=true
        goodsItems= listOf()
        fetchGoods(1,searchText.value)
    }

    // 监听列表滚动事件以实现分页加载
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .map { it.visibleItemsInfo }
            .filter { it.isNotEmpty() }
            .map { it.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == goodsItems.lastIndex && !isLoading && hasMore) {
                    currentPage += 1
                    fetchGoods(currentPage,searchText.value)
                }
            }
    }
    Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchPage2(searchText)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                currentPage = 1
                hasMore = true
                fetchGoods(1,searchText.value)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F0F0))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
//            Text(
//                text = "商品列表",
//                style = MaterialTheme.typography.titleLarge.copy(
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF333333)
//                ),
//                modifier = Modifier.padding(bottom = 16.dp)
//            )

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red)
                } else {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(goodsItems) { goodsItem ->
                            GoodsItemView(goodsItem, navController)
                        }
                        item {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// 获取商品数据的网络请求
fun fetchGoods(
    user: Data.User,
    page: Int,
    searchText: String,
    onSuccess: (List<GoodsItem>, Int) -> Unit,
    onError: (String) -> Unit
) {
    val gson = Gson()
    val url = "https://api-store.openguet.cn/api/member/tran/goods/all?userId=${user.id}&current=$page&size=100&keyword=$searchText" // 修改URL参数

    val headers = Headers.Builder()
        .add("appId", "1c92edcbfd42414e8bfee284c6801259")
        .add("appSecret", "80042819ade5505a74c4cb196258423ed4bb0")
        .add("Accept", "application/json, text/plain, */*")
        .build()

    val request = Request.Builder()
        .url(url)
        .headers(headers)
        .get()
        .build()

    try {
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onError("请求失败: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val body = responseBody.string()
                    val jsonType: Type = object : TypeToken<ApiResponse<GoodsResponse>>() {}.type
                    val dataResponseBody: ApiResponse<GoodsResponse> = gson.fromJson(body, jsonType)

                    if (dataResponseBody.code == 200) {
                        onSuccess(dataResponseBody.data.records, dataResponseBody.data.total)
                    } else {
                        onError("请求失败: ${dataResponseBody.msg}")
                    }
                } ?: onError("请求失败: 没有响应体")
            }
        })
    } catch (ex: NetworkOnMainThreadException) {
        ex.printStackTrace()
        onError("请求失败: ${ex.message}")
    }
}
