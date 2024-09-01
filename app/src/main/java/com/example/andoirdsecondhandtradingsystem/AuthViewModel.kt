package com.example.andoirdsecondhandtradingsystem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    fun registerUser(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.registerUser(RegisterRequest(username, password)).execute()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.code == 0) {
                        onSuccess()
                    } else {
                        onError(apiResponse?.msg ?: "注册失败")
                    }
                } else {
                    onError("注册失败")
                }
            } catch (e: HttpException) {
                onError("请求失败: ${e.message}")
            } catch (e: Exception) {
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

