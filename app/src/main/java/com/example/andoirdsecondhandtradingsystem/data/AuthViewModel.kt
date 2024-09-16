package com.example.andoirdsecondhandtradingsystem.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response


class AuthViewModel : ViewModel() {


   val apiService: ApiService by lazy {
        RetrofitClient.instance.create(ApiService::class.java)
    }

    /**
     * 注册用户
     */
    fun registerUser(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Starting network request")

                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.registerUser(RegisterRequest(username, password)).execute()
                }

                Log.d("AuthViewModel", "Network request completed")

                if (response.isSuccessful) {
                    val registerresponse = response.body()
                    if (registerresponse != null && registerresponse.code == 200) {

                        Log.d("AuthViewModel", "Registration succeeded")
                        onSuccess()
                    } else {
                        Log.d("AuthViewModel", "Registration failed with message: ${registerresponse?.msg}")
                        onError(registerresponse?.msg ?: "注册失败")
                    }
                } else {
                    Log.d("AuthViewModel", "Response was not successful")
                    onError("注册失败")
                }
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "HTTP exception", e)
                onError("请求失败: ${e.message}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "General exception", e)
                onError("请求失败: ${e.message}")
            }
        }
    }

    /**
     * 登录用户
     */
    fun loginUser(
        username: String,
        password: String,
        onSuccess: (Data.User) -> Unit, // 传递用户信息
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Starting login request for user: $username")
                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.loginUser(LoginRequest(username, password)).execute()
                }

                if (response.isSuccessful) {
                    Log.d("getLoginMessage", "API response: $response")
                    //获取登录响应体
                    val loginresponse = response.body()
                    Log.d("getLoginMessagebody", "API response: $loginresponse")

                    if (loginresponse != null) {
                        Log.d("AuthViewModel", "API response code: ${loginresponse.code}")
                        Log.d("AuthViewModel", "API response message: ${loginresponse.msg}")
                        Log.d("AuthViewModel", "API response data: ${loginresponse.data}")

                        if (loginresponse.code == 200) {
                            val user = loginresponse.data as Data.User?

                            // 设置默认头像和密码
                            if (user != null) {
                                user.avatar = "https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2024/09/11/f95a65cc-afa1-4383-be5f-193b7ef64eeb.jpg"
                                user.password = password
                            }

                            if (user != null) {
                                Log.d("AuthViewModel", "Login successful with user: $user")
                                onSuccess(user) // 传递用户信息
                            } else {
                                Log.e("AuthViewModel", "Login failed: data is null")
                                onError("登录失败: 用户数据为空")

                            }
                        } else {
                            Log.e("AuthViewModel", "Login failed: ${loginresponse.msg}")
                            onError(loginresponse.msg ?: "登录失败")
                        }
                    } else {
                        Log.e("AuthViewModel", "Login failed: response body is null")
                        onError("登录失败: 响应体为空")
                    }
                } else {
                    Log.e("AuthViewModel", "Login failed with response code: ${response.code()}")
                    onError("登录失败")
                }
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "HTTP exception during login", e)
                onError("请求失败: ${e.message()}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during login", e)
                onError("请求失败: ${e.message}")
            }
        }
    }

    /**
     * 获取消息列表
     */
    fun getMessageList(
        userId: Int,
        onSuccess: (List<Data.MessageListData>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("getMessageList", "Requesting messages for userId: $userId")

                // 每次请求都创建新的 Call 实例
                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.getMessageList(userId).execute()
                }

                Log.d("getMessageList", "Response: ${response.body()}")

                if (response.isSuccessful) {
                    val messageListResponse = response.body()
                    Log.d("getMessageList", "MessageListResponse: $messageListResponse")

                    if (messageListResponse != null && messageListResponse.code == 200) {
                        val data = messageListResponse.data
                        if (data is Data.DataWrapper) {
                            Log.d("成功", "Message list request succeeded ${data}")
                            onSuccess(data.messageList)
                        } else {
                            onError("Unexpected data type")
                        }
                    } else {
                        onError(messageListResponse?.msg ?: "请求失败")
                    }
                } else {
                    onError("请求失败: ${response.code()}")
                }
            } catch (e: HttpException) {
                onError("请求http失败: ${e.message}")
            } catch (e: Exception) {
                onError("请求异常，导致失败: ${e.message}")
            }
        }
    }

    /**
     * 获取消息详情
     */
    fun getMessageDetail(
        fromUserId: Int,
        userId: Int,
        onSuccess: (Data.MessageDatail) -> Unit,
        onError: (String) -> Unit
    ) {

        viewModelScope.launch {
            try {
                Log.d("getMessageList", "Requesting messages for userId: $userId")

                // 每次请求都创建新的 Call 实例
                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.getMessageDetail(fromUserId, userId).execute()
                }

                Log.d("getMessageList", "Response: ${response.body()}")

                if (response.isSuccessful) {
                    val messageDetailResponse = response.body()
                    Log.d("getMessageList", "MessageListResponse: $messageDetailResponse")

                    if (messageDetailResponse != null && messageDetailResponse.code == 200) {
                        val data = messageDetailResponse.data

                        onSuccess(data as Data.MessageDatail)

                    } else {
                        onError(messageDetailResponse?.msg ?: "请求失败")
                    }
                } else {
                    onError("请求失败: ${response.code()}")
                }
            } catch (e: HttpException) {
                onError("请求http失败: ${e.message}")
            } catch (e: Exception) {
                onError("请求异常，导致失败: ${e.message}")
            }
        }
    }
}

