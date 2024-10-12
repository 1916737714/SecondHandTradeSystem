package com.example.andoirdsecondhandtradingsystem.Home

import android.os.NetworkOnMainThreadException
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.andoirdsecondhandtradingsystem.Message.ChatScreen
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.lang.reflect.Type

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
fun AppNavigation9(navController: NavController, user: Data.User, tUserId: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            GoodsManage(navController, user, tUserId)
        }
        composable(
            route = "chatScreen/{fromUserId}/{fromUsername}/{userId}",
            arguments = listOf(
                navArgument("fromUserId") { type = NavType.StringType },
                navArgument("fromUsername") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val fromUserId = backStackEntry.arguments?.getString("fromUserId").orEmpty()
            val fromUsername = backStackEntry.arguments?.getString("fromUsername").orEmpty()
            val userId = backStackEntry.arguments?.getString("userId").orEmpty()
            ChatScreen(fromUserId, fromUsername, userId.toInt(), navController)
        }
    }
}



@Composable
fun GoodsManage(navController: NavHostController, user: Data.User, goodsId: String) {
    val userId = user.id
    var goodsDetails by remember { mutableStateOf<GoodsDetails?>(null) }
    var errorMessage by remember { mutableStateOf("") }

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


    var productInfo by remember { mutableStateOf<ProductInfo?>(null) }
    var errorText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    fun loadProductInfo() {
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
                    errorText = "请求失败: ${e.message}"
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { res ->
                        val body = res.body?.string()

                        if (res.isSuccessful && body != null) {
                            val gson = Gson()
                            val jsonType: Type = object : TypeToken<ProductInfoResponse>() {}.type
                            val dataResponse: ProductInfoResponse = gson.fromJson(body, jsonType)

                            if (dataResponse.code == 200) {
                                productInfo = dataResponse.data
                            } else {
                                errorText = dataResponse.msg
                            }
                        } else {
                            errorText = "请求失败: ${res.message}"
                        }
                    }
                }
            })
        } catch (ex: NetworkOnMainThreadException) {
            ex.printStackTrace()
            errorText = "请求失败: ${ex.message}"
        }
    }


    fun chatWithSeller() {
        productInfo?.let { info ->
            navController.navigate("chatScreen/${info.tUserId}/${info.username}/${user.id}")
        }
    }

    LaunchedEffect(Unit) {
        fetchGoodsDetails()
    }

    LaunchedEffect(Unit) {
        loadProductInfo()
    }

    GoodsManageView(goodsDetails = goodsDetails, errorMessage = errorMessage, user = user,onChatWithSeller = { chatWithSeller() })
}

@Composable
fun GoodsManageView(goodsDetails: GoodsDetails?, errorMessage: String, user: Data.User,onChatWithSeller: () -> Unit) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
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
                                .verticalScroll(rememberScrollState())
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
                                        .clip(CircleShape)
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
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "卖家地址: ${details.addr}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 20.sp,
                                        color = Color(0xFF666666)
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            AsyncImage(
                                model = details.imageUrlList.firstOrNull(),
                                contentDescription = details.content,
                                contentScale = ContentScale.Crop, // 使用自适应缩放
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f) // 设置宽高比
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Gray)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "价格: ￥${details.price}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF4500)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

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

                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = onChatWithSeller,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color.Black
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "聊一聊")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    purchaseItem(user.id.toString(), goodsDetails?.id ?: "", goodsDetails?.price.toString(), goodsDetails?.tUserId ?: "", context)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color.Black
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "购买")
            }
        }
    }
}

fun purchaseItem(buyerId: String, goodsId: String, price: String, sellerId: String, context: android.content.Context) {
    val gson = Gson()
    val url = "https://api-store.openguet.cn/api/member/tran/trading"

    val headers = Headers.Builder()
        .add("appId", "1c92edcbfd42414e8bfee284c6801259")
        .add("appSecret", "80042819ade5505a74c4cb196258423ed4bb0")
        .add("Accept", "application/json, text/plain, */*")
        .build()

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
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "购买失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            Log.d("info", responseBody ?: "No response body")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "购买成功", Toast.LENGTH_SHORT).show()
            }
        }
    })
}