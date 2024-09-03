package com.example.andoirdsecondhandtradingsystem

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class AuthViewModel : ViewModel() {
    private val apiService: ApiService by lazy {
        RetrofitClient.instance.create(ApiService::class.java)
    }

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
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.code == 0) {
                        Log.d("AuthViewModel", "Registration succeeded")
                        onSuccess()
                    } else {
                        Log.d("AuthViewModel", "Registration failed with message: ${apiResponse?.msg}")
                        onError(apiResponse?.msg ?: "注册失败")
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

    fun loginUser(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.loginUser(LoginRequest(username, password)).execute()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.code == 0) {
                        onSuccess()
                    } else {
                        onError(apiResponse?.msg ?: "登录失败")
                    }
                } else {
                    onError("登录失败")
                }
            } catch (e: HttpException) {
                onError("请求失败: ${e.message()}")
            } catch (e: Exception) {
                onError("请求失败: ${e.message}")
            }
        }
    }
}

