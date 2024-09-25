package com.example.androidsecondhandtradingsystem

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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

@Composable
fun MyAmount(navController: NavController, user: Data.User, onBalanceChanged: (Int) -> Unit) {
    var tranMoney by remember { mutableStateOf(TextFieldValue("")) }
    var currentBalance by remember { mutableStateOf(user.money) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("当前余额: $currentBalance", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Text("充值金额", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // 输入框
        BasicTextField(
            value = tranMoney,
            onValueChange = { tranMoney = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp),
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Handle onDone event
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 充值按钮
        Button(
            onClick = {
                if (tranMoney.text.isNotEmpty()) {
                    val rechargeAmount = tranMoney.text.toInt()
                    recharge(user.id.toLong(), rechargeAmount, context) { success ->
                        if (success) {
                            currentBalance += rechargeAmount
                            onBalanceChanged(currentBalance) // 更新余额
                            navController.navigateUp() // 返回上一页
                        }
                    }
                } else {
                    Toast.makeText(context, "请输入充值金额", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("立即充值")
        }
    }
}

fun recharge(userId: Long, tranMoney: Int, context: Context, onResult: (Boolean) -> Unit) {
    val url = "https://api-store.openguet.cn/api/member/tran/goods/recharge?tranMoney=$tranMoney&userId=$userId"

    val headers = Headers.Builder()
        .add("appId", "1c92edcbfd42414e8bfee284c6801259")
        .add("appSecret", "80042819ade5505a74c4cb196258423ed4bb0")
        .add("Accept", "application/json, text/plain, */*")
        .build()

    val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

    val request = Request.Builder()
        .url(url)
        .headers(headers)
        .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
        .build()

    val client = OkHttpClient()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            // 在主线程上显示错误信息
            (context as Activity).runOnUiThread {
                Toast.makeText(context, "充值失败，请稍后重试", Toast.LENGTH_SHORT).show()
            }
            onResult(false)
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            Log.d("Recharge", body ?: "No response body")

            val gson = Gson()
            val jsonType: Type = object : TypeToken<ResponseBody<Any>>() {}.type
            val dataResponseBody: ResponseBody<Any> = gson.fromJson(body, jsonType)

            // 在主线程上显示成功信息
            (context as Activity).runOnUiThread {
                if (dataResponseBody.code == 200) {
                    Toast.makeText(context, "充值成功", Toast.LENGTH_SHORT).show()
                    onResult(true)
                } else {
                    Toast.makeText(context, "充值失败: ${dataResponseBody.msg}", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            }
        }
    })
}

data class ResponseBody<T>(
    val code: Int,
    val msg: String,
    val data: T?
)