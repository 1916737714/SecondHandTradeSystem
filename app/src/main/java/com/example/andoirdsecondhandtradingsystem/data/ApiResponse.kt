package com.example.andoirdsecondhandtradingsystem.data

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

// 通用的 API 响应类
data class ApiResponse(
    @SerializedName("msg") val msg: String,
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: Data?
)

// 使用 sealed class 来表示不同类型的 "data"
sealed class Data {

    // 获取用户信息
    data class User(
        @SerializedName("appKey") val appKey: String,
        @SerializedName("avatar") var avatar: String,
        @SerializedName("id") val id: Int,
        @SerializedName("money") var money: Int,
        @SerializedName("password") var password: String,
        @SerializedName("username") val username: String
    ) : Data()


    //获取消息列表
    // 消息列表数据
    data class MessageListData(
        @SerializedName("fromUserId") val fromUserId: String,
        @SerializedName("username") val username: String,
        @SerializedName("unReadNum") val unReadNum: Int
    ) : Data()

    // 封装类用于在反序列化时保持一致性，避免直接使用额外的封装类
    data class DataWrapper(
        val messageList: List<MessageListData>
    ) : Data()


    //消息列表详情
    // 消息记录
    data class MessageRecord(
        @SerializedName("id") val id: String,
        @SerializedName("fromUserId") val fromUserId: String,
        @SerializedName("fromUsername") val fromUsername: String,
        @SerializedName("content") val content: String,
        @SerializedName("userId") val userId: String,
        @SerializedName("username") val username: String,
        @SerializedName("status") val status: Boolean,
        @SerializedName("createTime") val createTime: String
    )

    // 用于封装消息记录列表
    data class MessageDatail(
        @SerializedName("records") val records: List<MessageRecord>,
        @SerializedName("total") val total: Int,
        @SerializedName("size") val size: Int,
        @SerializedName("current") val current: Int
    ) : Data()

    //商品类型列表
    data class goodsType(
        @SerializedName("id") val id: Int,
        @SerializedName("type") val type: String
    )
    data class goodsTypeList(
       val GoodsTypeList: List<goodsType>
    ) : Data()

    //上传文件
    data class upLoadFile(
        @SerializedName("imageCode") val imageCode:String,
        @SerializedName("imageUrlList") val imageUrlList: List<String>
    ) : Data()
}