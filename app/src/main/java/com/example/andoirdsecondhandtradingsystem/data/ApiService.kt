package com.example.andoirdsecondhandtradingsystem.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

//api  接口
interface ApiService {
    //注册
    @POST("api/member/tran/user/register")
    fun registerUser(@Body registerUser: RegisterRequest): Call<ApiResponse>
    //登录

    @POST("api/member/tran/user/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<ApiResponse>

    //获取消息列表
    @GET("api/member/tran/chat/user")
    fun getMessageList(@Query("userId") userId: Int): Call<ApiResponse>

    //获取消息详情
    @GET("api/member/tran/chat/message")
    fun getMessageDetail(@Query("fromUserId") fromUserId: Int, @Query("userId") userId: Int, @Query("current") page: Int): Call<ApiResponse>

    //发送消息
    @POST("api/member/tran/chat")
    fun sendMessage(@Body SendMessageRequest: SendMessageRequest ): Call<ApiResponse>

    //将消息标记为已读
    @GET("api/member/tran/chat/change")
    fun markMessageAsRead(@Query("chatId") chatId: Int): Call<ApiResponse>
}

data class RegisterRequest(
    val username: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class SendMessageRequest(
    val userId: Int,
    val toUserId: Int,
    val content: String
)
