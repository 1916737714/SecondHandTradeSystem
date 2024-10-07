package com.example.andoirdsecondhandtradingsystem.Home

import android.os.NetworkOnMainThreadException
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.lang.reflect.Type

// 数据类，用于解析商品详情响应
data class GoodsDetails(
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
    val appIsShare: Int,
    val tuserId: String
)

data class GoodsDetailsResponse(
    val code: Int,
    val msg: String,
    val data: GoodsDetails
)

@Composable
fun GoodsManage(navController: NavHostController, user: Data.User, goodsId: String) {
    // 获取当前登录用户的ID
    val userId = user.id

    // 定义商品详情的状态
    var goodsDetails by remember { mutableStateOf<GoodsDetails?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    // 发起网络请求获取商品详情
    fun fetchGoodsDetails() {
        val url = "https://api-store.openguet.cn/api/member/tran/goods/details?goodsId=$goodsId"

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
                    errorMessage = "请求失败: ${e.message}"
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { res ->
                        val body = res.body?.string()

                        if (res.isSuccessful && body != null) {
                            val gson = Gson()
                            val jsonType: Type = object : TypeToken<GoodsDetailsResponse>() {}.type
                            val dataResponse: GoodsDetailsResponse = gson.fromJson(body, jsonType)

                            if (dataResponse.code == 200) {
                                goodsDetails = dataResponse.data
                            } else {
                                errorMessage = dataResponse.msg
                            }
                        } else {
                            errorMessage = "请求失败: ${res.message}"
                        }
                    }
                }
            })
        } catch (ex: NetworkOnMainThreadException) {
            ex.printStackTrace()
            errorMessage = "请求失败: ${ex.message}"
        }
    }

    // 在Composable中启动网络请求
    LaunchedEffect(Unit) {
        fetchGoodsDetails()
    }

    // 调用GoodsManageView来显示界面
    GoodsManageView(goodsDetails = goodsDetails, errorMessage = errorMessage, user = user)
}

@Composable
fun GoodsManageView(goodsDetails: GoodsDetails?, errorMessage: String, user: Data.User) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "商品详情",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 显示当前登录用户的ID
        Text(
            text = "当前用户ID: ${user.id}",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        } else {
            goodsDetails?.let { details ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = details.avatar,
                                contentDescription = "用户头像",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)  // 将头像变为圆角
                                    .background(Color.Gray)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = details.username,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 新增一行显示商品类型和地址
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "商品类别: ${details.typeName}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 20.sp,
                                    color = Color(0xFF666666)
                                ),
                                modifier = Modifier.weight(1f)  // 使用weight分配剩余空间
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "卖家地址: ${details.addr}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 20.sp,
                                    color = Color(0xFF666666)
                                ),
                                modifier = Modifier.weight(1f)  // 使用weight分配剩余空间
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        AsyncImage(
                            model = details.imageUrlList.firstOrNull(),
                            contentDescription = details.content,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(Color.Gray)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 增加 "商品描述" 标题
                        Text(
                            text = "商品描述：",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = details.content,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Spacer(modifier = Modifier.weight(1f))  // 占据剩余空间，推价格到底部

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)  // 增加底部间距
                        ) {
                            Text(
                                text = "￥: ${details.price}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 35.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF4500) // 橙红色
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { /*TODO: 聊一聊按钮点击事件*/ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700), // 加深的淡黄色
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "聊一聊")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    // 触发购买功能
                                    purchaseItem(user.id.toString(), details.id, details.price.toString(), details.tUserId, context)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700), // 加深的淡黄色
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "购买")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun purchaseItem(buyerId: String, goodsId: String, price: String, sellerId: String, context: android.content.Context) {
    val gson = Gson()
    val url = "https://api-store.openguet.cn/api/member/tran/trading"

    // 请求头
    val headers = Headers.Builder()
        .add("appId", "1c92edcbfd42414e8bfee284c6801259")
        .add("appSecret", "80042819ade5505a74c4cb196258423ed4bb0")
        .add("Accept", "application/json, text/plain, */*")
        .build()

    // 请求体
    val bodyMap = HashMap<String, Any>()
    bodyMap["buyerId"] = buyerId
    bodyMap["goodsId"] = goodsId
    bodyMap["price"] = price
    bodyMap["sellerId"] = sellerId
    val body = gson.toJson(bodyMap)

    val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

    val request = Request.Builder()
        .url(url)
        .headers(headers)
        .post(RequestBody.create(MEDIA_TYPE_JSON, body))
        .build()

    val client = OkHttpClient()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            // 显示失败的Toast
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "购买失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            Log.d("info", responseBody ?: "No response body")
            // 显示成功的Toast
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "购买成功", Toast.LENGTH_SHORT).show()
            }
        }
    })
}