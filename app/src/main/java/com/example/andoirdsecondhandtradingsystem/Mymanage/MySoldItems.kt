package com.example.androidsecondhandtradingsystem

import android.os.NetworkOnMainThreadException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.andoirdsecondhandtradingsystem.Home.AppNavigation4
import com.example.andoirdsecondhandtradingsystem.Home.AppNavigation5
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

// SoldItem数据类
data class SoldItem(
    val id: String,
    val goodsId: String,
    val sellerId: String?,
    val price: Int,
    val buyerId: String?,
    val createTime: Long,
    val sellerName: String?,
    val buyerName: String?,
    val sellerAvatar: String?,
    val buyerAvatar: String?,
    val goodsDescription: String,
    val imageUrlList: List<String>
)

// 接收服务器响应的数据类
data class SoldItemRecord(
    val id: String,
    val goodsId: String,
    val sellerId: String?,
    val price: Int,
    val buyerId: String?,
    val createTime: Long,
    val sellerName: String?,
    val buyerName: String?,
    val sellerAvatar: String?,
    val buyerAvatar: String?,
    val goodsDescription: String,
    val imageUrlList: List<String>
)

data class SoldItemResponse(
    val records: List<SoldItemRecord>,
    val total: Int,
    val size: Int,
    val current: Int
)

// 更改类名为 ServerResponse
data class ServerResponse<T>(
    val code: Int,
    val msg: String,
    val data: T
)

@Composable
fun AppNavigation3(navController: NavController, user: Data.User) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            MySoldItems(navController, user)
        }
        composable("appNavigation4/{goodsId}/{buyerId}/{buyerName}") { backStackEntry ->
            val goodsId = backStackEntry.arguments?.getString("goodsId")
            val buyerId = backStackEntry.arguments?.getString("buyerId")
            val buyerName = backStackEntry.arguments?.getString("buyerName")
            if (goodsId != null && buyerId != null && buyerName != null) {
                AppNavigation5(navController, user, goodsId, buyerId, buyerName)
            }
        }
    }
}

@Composable
fun MySoldItems(navController: NavController, user: Data.User) {
    var soldItemList by remember { mutableStateOf(listOf<SoldItem>()) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    var hasMore by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()

    fun fetchSoldItems(page: Int) {
        if (isLoading || !hasMore) return

        isLoading = true
        fetchSoldItems(user, page, { list, total ->
            if (page == 1) {
                soldItemList = list
            } else {
                soldItemList = soldItemList + list
            }
            hasMore = soldItemList.size < total
            isLoading = false
        }, { error ->
            errorMessage = error
            isLoading = false
        })
    }

    LaunchedEffect(Unit) {
        fetchSoldItems(1)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { it.visibleItemsInfo }
            .filter { it.isNotEmpty() }
            .map { it.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == soldItemList.lastIndex && !isLoading && hasMore) {
                    currentPage += 1
                    fetchSoldItems(currentPage)
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
            text = "我卖出的商品",
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
                items(soldItemList) { item ->
                    DisplaySoldItem(item, navController)
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

fun fetchSoldItems(
    user: Data.User,
    page: Int,
    onSuccess: (List<SoldItem>, Int) -> Unit,
    onError: (String) -> Unit
) {
    val gson = Gson()
    val url = "https://api-store.openguet.cn/api/member/tran/trading/sell?userId=${user.id}&current=$page&size=10"

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
                    val jsonType: Type = object : TypeToken<ServerResponse<SoldItemResponse>>() {}.type
                    val dataResponseBody: ServerResponse<SoldItemResponse> = gson.fromJson(body, jsonType)

                    if (dataResponseBody.code == 200) {
                        val soldItemList = dataResponseBody.data.records.map { record ->
                            SoldItem(
                                id = record.id,
                                goodsId = record.goodsId,
                                sellerId = record.sellerId,
                                price = record.price,
                                buyerId = record.buyerId,
                                createTime = record.createTime,
                                sellerName = record.sellerName,
                                buyerName = record.buyerName,
                                sellerAvatar = record.sellerAvatar,
                                buyerAvatar = record.buyerAvatar,
                                goodsDescription = record.goodsDescription,
                                imageUrlList = record.imageUrlList
                            )
                        }
                        onSuccess(soldItemList, dataResponseBody.data.total)
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
fun DisplaySoldItem(item: SoldItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("appNavigation4/${item.goodsId}/${item.buyerId ?: "unknown"}/${item.buyerName ?: "unknown"}")
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrlList.firstOrNull(),
                contentDescription = item.goodsDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Gray)
                    .fillMaxSize()
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = item.goodsDescription,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                )
                Text(
                    text = "买家: ${item.buyerName ?: "未指定"}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF666666)
                    )
                )
                Text(
                    text = "价格: ${item.price}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF03A9F4)
                    )
                )
            }
        }
    }
}