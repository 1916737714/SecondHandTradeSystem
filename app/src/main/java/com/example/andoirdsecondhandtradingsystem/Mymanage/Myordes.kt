package com.example.androidsecondhandtradingsystem

import android.os.NetworkOnMainThreadException
import androidx.compose.foundation.background
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

// Order数据类
data class Order(
    val id: String,
    val goodsId: String,
    val sellerId: String,
    val price: Int,
    val buyerId: String?,
    val createTime: String,
    val sellerName: String,
    val buyerName: String?,
    val sellerAvatar: String?,
    val buyerAvatar: String?,
    val goodsDescription: String,
    val imageUrlList: List<String>
)

// 接收服务器响应的泛型数据类
data class OrderRecord(
    val id: String,
    val goodsId: String,
    val sellerId: String,
    val price: Int,
    val buyerId: String?,
    val createTime: String,
    val sellerName: String,
    val buyerName: String?,
    val sellerAvatar: String?,
    val buyerAvatar: String?,
    val goodsDescription: String,
    val imageUrlList: List<String>
)

data class OrderResponse(
    val records: List<OrderRecord>,
    val total: Int,
    val size: Int,
    val current: Int
)

// 更改类名为 ApiResponse
data class ApiResponse<T>(
    val code: Int,
    val msg: String,
    val data: T
)

@Composable
fun MyOrders(navController: NavController, user: Data.User) {
    // 使用 remember 保存订单列表的状态
    var orderList by remember { mutableStateOf(listOf<Order>()) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    var hasMore by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()

    // 获取订单数据
    fun fetchOrders(page: Int) {
        if (isLoading || !hasMore) return

        isLoading = true
        fetchOrders(user, page, { list, total ->
            if (page == 1) {
                orderList = list
            } else {
                orderList = orderList + list
            }
            hasMore = orderList.size < total
            isLoading = false
        }, { error ->
            errorMessage = error
            isLoading = false
        })
    }

    // 在Composable中启动网络请求
    LaunchedEffect(Unit) {
        fetchOrders(1)
    }

    // 监听列表滚动事件以实现分页加载
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { it.visibleItemsInfo }
            .filter { it.isNotEmpty() }
            .map { it.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == orderList.lastIndex && !isLoading && hasMore) {
                    currentPage += 1
                    fetchOrders(currentPage)
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
            text = "我的订单",
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
                items(orderList) { order ->
                    OrderItem(order)
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

// 获取订单数据的网络请求
fun fetchOrders(
    user: Data.User,
    page: Int,
    onSuccess: (List<Order>, Int) -> Unit,
    onError: (String) -> Unit
) {
    val gson = Gson()
    val url = "https://api-store.openguet.cn/api/member/tran/trading/buy?userId=${user.id}&current=$page&size=10" // 修改URL参数

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
                    val jsonType: Type = object : TypeToken<ApiResponse<OrderResponse>>() {}.type
                    val dataResponseBody: ApiResponse<OrderResponse> = gson.fromJson(body, jsonType)

                    if (dataResponseBody.code == 200) {
                        val orderList = dataResponseBody.data.records.map { record ->
                            Order(
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
                        onSuccess(orderList, dataResponseBody.data.total)
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
fun OrderItem(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = order.imageUrlList.firstOrNull(),
                contentDescription = order.goodsDescription,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = order.goodsDescription,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                )
                Text(
                    text = "卖家: ${order.sellerName}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF666666)
                    )
                )
                Text(
                    text = "价格: ${order.price}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF03A9F4)
                    )
                )
            }
        }
    }
}