package com.example.androidsecondhandtradingsystem

import android.os.Build.VERSION_CODES.P
import android.os.NetworkOnMainThreadException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Type
import com.example.andoirdsecondhandtradingsystem.Home.GoodsManage
import com.example.andoirdsecondhandtradingsystem.Home.ProductManage

// Merchandise数据类
data class Merchandise(
    val id: String,
    val appKey: String,
    val tUserId: String,
    val imageCode: String,
    val content: String,
    val price: Int,
    val addr: String,
    val typeId: Int,
    val typeName: String,
    val status: Int,
    val createTime: String,
    val username: String,
    val avatar: String,
    val imageUrlList: List<String>,
    val appIsShare: Int
)

// MerchandiseResponse数据类
data class MerchandiseResponse(
    val records: List<Merchandise>,
    val total: Int,
    val size: Int,
    val current: Int
)

// MymerchandiseResponse类
data class MymerchandiseResponse<T>(
    val code: Int,
    val msg: String,
    val data: T
)

@Composable
fun AppNavigation1(navController: NavController, user: Data.User) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            MyMerchandise(navController, user)
        }
        composable("goodsManage/{goodsId}") { backStackEntry ->
            val goodsId = backStackEntry.arguments?.getString("goodsId")
            goodsId?.let {
                ProductManage(navController, user, it)
            }
        }
    }
}

@Composable
fun MyMerchandise(navController: NavController, user: Data.User) {
    var merchandiseList by remember { mutableStateOf(listOf<Merchandise>()) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    var hasMore by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()

    fun fetchMerchandise(page: Int) {
        if (isLoading || !hasMore) return

        isLoading = true
        fetchMerchandise(user, page, { list, total ->
            if (page == 1) {
                merchandiseList = list
            } else {
                merchandiseList = merchandiseList + list
            }
            hasMore = merchandiseList.size < total
            isLoading = false
        }, { error ->
            errorMessage = error
            isLoading = false
        })
    }

    LaunchedEffect(Unit) {
        fetchMerchandise(1)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { it.visibleItemsInfo }
            .filter { it.isNotEmpty() }
            .map { it.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == merchandiseList.lastIndex && !isLoading && hasMore) {
                    currentPage += 1
                    fetchMerchandise(currentPage)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "我的商品",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(merchandiseList) { merchandise ->
                    MerchandiseItem(merchandise, navController)
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

fun fetchMerchandise(
    user: Data.User,
    page: Int,
    onSuccess: (List<Merchandise>, Int) -> Unit,
    onError: (String) -> Unit
) {
    val gson = Gson()
    val url = "https://api-store.openguet.cn/api/member/tran/goods/myself?userId=${user.id}&current=$page&size=10"

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
                    val jsonType: Type = object : TypeToken<MymerchandiseResponse<MerchandiseResponse>>() {}.type
                    val dataResponseBody: MymerchandiseResponse<MerchandiseResponse> = gson.fromJson(body, jsonType)

                    if (dataResponseBody.code == 200) {
                        val merchandiseList = dataResponseBody.data.records
                        onSuccess(merchandiseList, dataResponseBody.data.total)
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

@Composable
fun MerchandiseItem(merchandise: Merchandise, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("goodsManage/${merchandise.id}") }, // 整个卡片可点击
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = merchandise.imageUrlList.firstOrNull(),
                contentDescription = merchandise.content,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = merchandise.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                )
                Text(
                    text = "卖家: ${merchandise.username}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF666666)
                    )
                )
                Text(
                    text = "价格: ${merchandise.price}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF03A9F4)
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { navController.navigate("goodsManage/${merchandise.id}") }
            ) {
                Text("详情")
            }
        }
    }
}