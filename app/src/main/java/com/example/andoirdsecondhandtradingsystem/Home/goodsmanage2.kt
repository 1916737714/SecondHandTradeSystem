package com.example.andoirdsecondhandtradingsystem.Home

import android.os.NetworkOnMainThreadException
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.lang.reflect.Type

data class ProductDetails(
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

data class ProductDetailsResponse(
    val code: Int,
    val msg: String,
    val data: ProductDetails
)

@Composable
fun ProductManage(navController: NavHostController, user: Data.User, productId: String ) {
    val userId = user.id
    var productDetails by remember { mutableStateOf<ProductDetails?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun fetchProductDetails() {
        val url = "https://api-store.openguet.cn/api/member/tran/goods/details?goodsId=$productId"

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
                            val jsonType: Type = object : TypeToken<ProductDetailsResponse>() {}.type
                            val dataResponse: ProductDetailsResponse = gson.fromJson(body, jsonType)

                            if (dataResponse.code == 200) {
                                productDetails = dataResponse.data
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

    fun takeDownProduct() {
        val url = "https://api-store.openguet.cn/api/member/tran/goods/delete?goodsId=$productId&userId=$userId"

        val headers = Headers.Builder()
            .add("appId", "1c92edcbfd42414e8bfee284c6801259")
            .add("appSecret", "80042819ade5505a74c4cb196258423ed4bb0")
            .add("Accept", "application/json, text/plain, */*")
            .build()

        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .post(RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), ""))
            .build()

        try {
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    errorMessage = "下架请求失败: ${e.message}"
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { res ->
                        if (res.isSuccessful) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("商品下架成功")
                                navController.popBackStack() // 返回上一步
                            }
                        } else {
                            errorMessage = "下架请求失败: ${res.message}"
                        }
                    }
                }
            })
        } catch (ex: NetworkOnMainThreadException) {
            ex.printStackTrace()
            errorMessage = "下架请求失败: ${ex.message}"
        }
    }

    LaunchedEffect(Unit) {
        fetchProductDetails()
    }

    ProductManageView(
        productDetails = productDetails,
        errorMessage = errorMessage,
        user = user,
        onTakeDownProduct = { takeDownProduct() },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ProductManageView(
    productDetails: ProductDetails?,
    errorMessage: String,
    user: Data.User,
    onTakeDownProduct: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // 留出底部按钮的空间
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
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
                    productDetails?.let { details ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .background(Color.White)
                                .clip(RoundedCornerShape(8.dp))
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
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
                            }
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


            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onTakeDownProduct,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336), // 红色
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "下架商品")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}