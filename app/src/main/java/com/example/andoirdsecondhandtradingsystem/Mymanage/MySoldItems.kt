package com.example.androidsecondhandtradingsystem

import android.os.NetworkOnMainThreadException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

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
fun MySoldItems(navController: NavController, user: Data.User) {
    // 使用remember保存订单列表的状态
    var soldItemList by remember { mutableStateOf(listOf<SoldItem>()) }
    var errorMessage by remember { mutableStateOf("") }

    // 在Composable中启动网络请求
    LaunchedEffect(Unit) {
        fetchSoldItems(user, { list ->
            soldItemList = list
        }, { error ->
            errorMessage = error
        })
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
                modifier = Modifier.fillMaxSize()
            ) {
                items(soldItemList) { item ->
                    DisplaySoldItem(item)
                }
            }
        }
    }
}

// 获取已售商品数据的网络请求
fun fetchSoldItems(
    user: Data.User,
    onSuccess: (List<SoldItem>) -> Unit,
    onError: (String) -> Unit
) {
    val gson = Gson()
    val url = "https://api-store.openguet.cn/api/member/tran/trading/sell?userId=${user.id}"

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
                        onSuccess(soldItemList)
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
fun DisplaySoldItem(item: SoldItem) {
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
                model = item.imageUrlList.firstOrNull(),
                contentDescription = item.goodsDescription,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Gray)
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