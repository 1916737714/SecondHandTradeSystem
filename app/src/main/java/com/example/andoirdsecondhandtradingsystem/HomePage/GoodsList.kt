package com.example.andoirdsecondhandtradingsystem.HomePage

import android.os.NetworkOnMainThreadException
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil.compose.rememberImagePainter
import com.example.andoirdsecondhandtradingsystem.R
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.example.androidsecondhandtradingsystem.ApiResponse
import com.example.androidsecondhandtradingsystem.Order
import com.example.androidsecondhandtradingsystem.OrderResponse
import com.example.androidsecondhandtradingsystem.fetchOrders
import java.lang.reflect.Type

import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.json.JSONObject.NULL


data class GoodsResponse(
    val current:Int,
    val records:List<Product>,
    val size:Int,
    val total:Int
)

data class Product(
    val addr:String,
    val appIsShare:Int,
    val appKey:String,
    val avatar:String,
    val content:String,
    val createTime:Long,
    val id:Long,
    val imageCode:Long,
    val imageUrlList:List<Any>,
    val price:Int,
    val status:Int,
    val tUserId:Long,
    val tuserId:Long,
    val typeId:Int,
    val typeName:String,
    val username:String
)

data class ApiResponseBody<T>(
    val code:Int,
    val msg:String,
    val data:T
)

fun getGoods(user: Data.User,
                     onSuccess: (List<Product>) -> Unit,
                     onError: (String) -> Unit){
    val gson=Gson()
    val url="https://api-store.openguet.cn/api/member/tran/goods/all?userId=${user.id}"

    val headers=Headers.Builder()
        .add("Accept","application/json, text/plain, */*")
        .add("appId","1c92edcbfd42414e8bfee284c6801259")
        .add("appSecret","80042819ade5505a74c4cb196258423ed4bb0")
        .build()

    val request=Request.Builder()
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
                    val jsonType: Type = object : TypeToken<ApiResponseBody<GoodsResponse>>() {}.type
                    val dataResponseBody: ApiResponseBody<GoodsResponse> = gson.fromJson(body, jsonType)

                    if (dataResponseBody.code == 200) {
                        val productList = dataResponseBody.data.records.map{ record ->
                            Product(
                                addr=record.addr,
                                appIsShare = record.appIsShare,
                                appKey = record.appKey,
                                avatar = record.avatar,
                                content = record.content,
                                createTime =record.createTime,
                                id=record.id,
                                imageCode=record.imageCode,
                                imageUrlList=record.imageUrlList,
                                price=record.price,
                                status=record.status,
                                tUserId=record.tUserId,
                                tuserId=record.tuserId,
                                typeId=record.typeId,
                                typeName=record.typeName,
                                username=record.username
                            )
                        }
                        onSuccess(productList)
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
fun GoodsList(navController: NavController,products:List<Product>) {
    Box(modifier = Modifier
        .verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)) {
            // 只渲染偶数索引的产品
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(0.5f)
            ) {
                for (i in 0 until products.size step 2) {

                        ProductItem(product = products[i], onClick = {
                            val productJson = Gson().toJson(products[i])
                            navController.navigate("goodsDetail/$productJson")
                        }, 200, 240)


                }
            }
            // 只渲染奇数索引的产品（注意这里是从1开始，但索引是从0开始的，所以实际上是i+1为奇数）
            Column (modifier = Modifier
                .padding(4.dp)
            ){
                for (i in 1 until products.size step 2) {
                    if (i == 1) {

                            ProductItem(product = products[i], onClick = {
                                val productJson = Gson().toJson(products[i])
                                navController.navigate("goodsDetail/$productJson")
                            }, 200, 160)
                    } else {

                            ProductItem(product = products[i], onClick = {
                                val productJson = Gson().toJson(products[i])
                                navController.navigate("goodsDetail/$productJson")
                            }, 200, 240)

                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product,onClick:()->Unit,x:Int,y:Int) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(2.dp, Color.White), RoundedCornerShape(16.dp))
            .fillMaxWidth() // 填充单元格宽度
            .padding(4.dp)
            .background(Color(0xFFF0F0F0))
    ) {
        // 假设你有一个加载网络图片的函数，这里使用本地资源作为示例
        // Image(painter = rememberImagePainter(data = product.imageUrl), ...)
        RoundedImage(product = product,x,y)
        Text(
            text = product.content,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center,
            maxLines = 1, // 限制描述文字的行数
            //overflow = TextOverflow.Ellipsis // 超出部分显示省略号
        )
        Text(text = "￥ ${product.price}",color= Color.Red)
        Text(text = "${product.username}",color=Color.Black)
    }
}

@Composable
fun RoundedImage(product: Product,x:Int,y:Int){
    Box(
        contentAlignment = Alignment.Center,
        modifier=Modifier.clip(shape = RoundedCornerShape(16.dp))
    ){
        if(product.imageUrlList.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(product.imageUrlList[0].toString()),// 使用本地资源
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(x.dp, y.dp)
                // 设置图片大小
            )
        }else{
            Image(
                painter = painterResource(id= R.drawable.baseline_crop_original_24),// 使用本地资源
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(x.dp, y.dp)
            )
        }
    }
}






