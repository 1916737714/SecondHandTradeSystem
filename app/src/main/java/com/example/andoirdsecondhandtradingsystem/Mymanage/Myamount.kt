package com.example.androidsecondhandtradingsystem

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.lang.reflect.Type

val gson = Gson()

fun rechargeAmount(
    userId: String,
    amount: Int,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val url = "https://api-store.openguet.cn/api/member/tran/goods/recharge"

    val headers = Headers.Builder()
        .add("appId", "1c92edcbfd42414e8bfee284c6801259")
        .add("appSecret", "80042819ade5505a74c4cb196258423ed4bb0")
        .add("Accept", "application/json, text/plain, */*")
        .build()

    val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

    val requestBody = """
        {
            "userId": "$userId",
            "tranMoney": $amount
        }
    """.trimIndent()

    Log.d("RECHARGE", "Request Body: $requestBody")

    val request = Request.Builder()
        .url(url)
        .headers(headers)
        .post(RequestBody.create(MEDIA_TYPE_JSON, requestBody))
        .build()

    val client = OkHttpClient()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            onError("请求失败: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.let { responseBody ->
                val body = responseBody.string()
                Log.d("RECHARGE", "Response Body: $body")
                val jsonType: Type = object : TypeToken<ApiResponseBody<Any>>() {}.type
                val dataResponseBody: ApiResponseBody<Any> = gson.fromJson(body, jsonType)

                if (dataResponseBody.code == 200) {
                    onSuccess("充值成功")
                } else {
                    onError("充值失败: ${dataResponseBody.msg}")
                }
            } ?: onError("充值失败: 没有响应体")
        }
    })
}

@Composable
fun MyAmount(navController: NavController, user: Data.User) {
    var balance by remember { mutableStateOf(user.money) } // 使用用户的实际余额
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var customAmount by remember { mutableStateOf("") }
    val rechargeOptions = listOf(20, 50, 100, 200, 328, 648)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "用户ID: ${user.id}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "账户余额: ￥${balance.toInt()}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
        }

        if (successMessage.isNotEmpty()) {
            Text(text = successMessage, color = Color.Green, modifier = Modifier.padding(bottom = 8.dp))
        }

        Text(
            text = "充值金额:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            rechargeOptions.forEach { amount ->
                Button(
                    onClick = { customAmount = amount.toString() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "￥$amount", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customAmount,
                onValueChange = { customAmount = it },
                label = { Text("自定义金额") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    customAmount.toIntOrNull()?.let { amount ->
                        if (amount > 0) {
                            rechargeAmount(
                                userId = user.id.toString(),
                                amount = amount,
                                onSuccess = { message ->
                                    successMessage = message
                                    errorMessage = ""
                                    balance += amount
                                    customAmount = ""
                                },
                                onError = { message ->
                                    errorMessage = message
                                    successMessage = ""
                                }
                            )
                        } else {
                            errorMessage = "请输入有效的金额！"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "确认充值", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}