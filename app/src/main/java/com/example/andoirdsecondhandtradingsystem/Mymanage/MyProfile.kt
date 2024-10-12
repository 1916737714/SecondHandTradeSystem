package com.example.andoirdsecondhandtradingsystem.Mymanage

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

data class UploadResponse(
    val code: Int,
    val msg: String,
    val data: UploadData?
)

data class UploadData(
    val imageCode: String,
    val imageUrlList: List<String>
)

data class ResponseBody(
    val code: Int,
    val msg: String,
    val data: Any?
)

@Composable
fun MyProfile(navController: NavController, user: Data.User) {
    var uploadResult by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "上传图片",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Gray)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            selectedImageUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: Text(
                text = "点击选择图片",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        selectedImageUri?.let {
            Button(
                onClick = {
                    isLoading = true
                    uploadImage(context, user, it) { result ->
                        uploadResult = result
                        isLoading = false
                        if (result.contains("成功")) {
                            showDialog = true
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "修改头像")
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        uploadResult?.let {
            Text(
                text = it,
                color = if (it.contains("成功")) Color.Green else Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("确定")
                    }
                },
                title = { Text("提示") },
                text = { Text("重新登入后头像更换成功") }
            )
        }
    }
}

fun uploadImage(context: Context, user: Data.User, imageUri: Uri, onResult: (String) -> Unit) {
    val gson = Gson()
    val uploadUrl = "https://api-store.openguet.cn/api/member/tran/image/upload"

    val headers = Headers.Builder()
        .add("appId", "1c92edcbfd42414e8bfee284c6801259")
        .add("appSecret", "80042819ade5505a74c4cb196258423ed4bb0")
        .add("Accept", "application/json, text/plain, */*")
        .build()

    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(imageUri)
    val file = File(context.cacheDir, "upload_image.jpg")
    inputStream?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    if (!file.exists() || file.length() == 0L) {
        Log.e("UploadImage", "文件不存在或为空")
        onResult("上传失败: 文件不存在或为空")
        return
    }

    val MEDIA_TYPE_JPG = "image/jpeg".toMediaTypeOrNull()
    val requestFile = file.asRequestBody(MEDIA_TYPE_JPG)

    val multipartBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("fileList", file.name, requestFile)
        .build()

    val request = Request.Builder()
        .url(uploadUrl)
        .headers(headers)
        .post(multipartBody)
        .build()

    val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            Log.e("UploadImage", "上传失败: ${e.message}")
            onResult("上传失败: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            Log.d("UploadResponse", "Response code: ${response.code}")
            Log.d("UploadResponse", "Response headers: ${response.headers}")
            Log.d("UploadResponse", "Response body: $responseBody") // 打印原始响应体

            if (responseBody != null) {
                try {
                    val jsonType: Type = object : TypeToken<UploadResponse>() {}.type
                    val uploadResponse: UploadResponse = gson.fromJson(responseBody, jsonType)

                    Log.d("UploadResponse", "Parsed response: $uploadResponse")

                    if (uploadResponse.code == 200) {
                        val imageUrlList = uploadResponse.data?.imageUrlList
                        if (!imageUrlList.isNullOrEmpty()) {
                            updateAvatar(user.id.toString(), imageUrlList[0], onResult)
                        } else {
                            onResult("上传失败: 没有获取到图片URL")
                        }
                    } else {
                        onResult("上传失败: ${uploadResponse.msg} (code: ${uploadResponse.code})")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("UploadImage", "解析响应体时出错: ${e.message}")
                    onResult("上传失败: 解析响应体时出错")
                }
            } else {
                Log.e("UploadImage", "上传失败: 没有响应体")
                onResult("上传失败: 没有响应体")
            }
        }
    })
}

fun updateAvatar(userId: String, imageUrl: String, onResult: (String) -> Unit) {
    val gson = Gson()
    val updateUrl = "https://api-store.openguet.cn/api/member/tran/user/update"

    val headers = Headers.Builder()
        .add("appId", "1c92edcbfd42414e8bfee284c6801259")
        .add("appSecret", "80042819ade5505a74c4cb196258423ed4bb0")
        .add("Accept", "application/json, text/plain, */*")
        .build()

    val bodyMap = mapOf(
        "avatar" to imageUrl,
        "userId" to userId
    )
    val body = gson.toJson(bodyMap)

    val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    val requestBody = RequestBody.create(MEDIA_TYPE_JSON, body)

    val request = Request.Builder()
        .url(updateUrl)
        .headers(headers)
        .post(requestBody)
        .build()

    val client = OkHttpClient()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            Log.e("UpdateAvatar", "更换头像失败: ${e.message}")
            onResult("更换头像失败: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            Log.d("UpdateAvatar", "Response code: ${response.code}")
            Log.d("UpdateAvatar", "Response headers: ${response.headers}")
            Log.d("UpdateAvatar", "Response body: $responseBody")

            if (responseBody != null) {
                try {
                    val jsonType: Type = object : TypeToken<ResponseBody>() {}.type
                    val updateResponse: ResponseBody = gson.fromJson(responseBody, jsonType)

                    if (updateResponse.code == 200) {
                        onResult("更换头像成功")
                    } else {
                        onResult("更换头像失败: ${updateResponse.msg} (code: ${updateResponse.code})")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("UpdateAvatar", "解析响应体时出错: ${e.message}")
                    onResult("更换头像失败: 解析响应体时出错")
                }
            } else {
                Log.e("UpdateAvatar", "更换头像失败: 没有响应体")
                onResult("更换头像失败: 没有响应体")
            }
        }
    })
}