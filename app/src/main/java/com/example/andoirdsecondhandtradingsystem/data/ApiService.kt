package com.example.andoirdsecondhandtradingsystem.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
//api  接口
interface ApiService {

    @POST("api/member/tran/user/register")
    fun registerUser(@Body registerUser: RegisterRequest): Call<ApiResponse>



    @POST("api/member/tran/user/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<ApiResponse>
}

data class RegisterRequest(
    val username: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

