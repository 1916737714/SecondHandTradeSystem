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

// Merchandise数据类
data class Merchandise(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val imageUrl: String // 假设有一个图片URL
)

// 接收服务器响应的泛型数据类
data class MerchandiseRecord(
    val id: String,
    val appKey: String,
    val tUserId: String,
    val imageCode: String,
    val content: String,
    val price: String,
    val addr: String,
    val typeId: Int,
    val typeName: String,
    val status: Int,
    val createTime: String,
    val username: String,
    val avatar: String,
    val imageUrlList: List<String>,
    val appIsShare: Int,
    val tuserId: String
)

data class MerchandiseResponse(
    val records: List<MerchandiseRecord>,
    val total: Int,
    val size: Int,
    val current: Int
)

data class ApiResponseBody<T>(
    val code: Int,
    val msg: String,
    val data: T
)

@Composable
fun MyMerchandise(navController: NavController, merchandiseList: List<Merchandise>, errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "我发布的商品",
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
                items(merchandiseList) { merchandise ->
                    MerchandiseItem(merchandise)
                }
            }
        }
    }
}
// 获取商品数据的网络请求
fun fetchMerchandise(
    user: Data.User,
    onSuccess: (List<Merchandise>) -> Unit,
    onError: (String) -> Unit
) {
    val gson = Gson()
    val url = "https://api-store.openguet.cn/api/member/tran/goods/myself?userId=${user.id}"

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
                    val jsonType: Type = object : TypeToken<ApiResponseBody<MerchandiseResponse>>() {}.type
                    val dataResponseBody: ApiResponseBody<MerchandiseResponse> = gson.fromJson(body, jsonType)

                    if (dataResponseBody.code == 200) {
                        val merchandiseList = dataResponseBody.data.records.map { record ->
                            Merchandise(
                                id = record.id,
                                name = record.content,
                                description = record.content,
                                price = record.price,
                                imageUrl = record.imageUrlList.firstOrNull() ?: ""
                            )
                        }
                        onSuccess(merchandiseList)
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
fun MerchandiseItem(merchandise: Merchandise) {
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
                model = merchandise.imageUrl,
                contentDescription = merchandise.name,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = merchandise.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                )
                Text(
                    text = merchandise.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF666666)
                    )
                )
                Text(
                    text = merchandise.price,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF03A9F4)
                    )
                )
            }
        }
    }
}