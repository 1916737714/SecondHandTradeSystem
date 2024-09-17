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
}
// 通用的对象类，用于表示 `imageUrlList` 等
//data class ImageUrl(val placeholder: Any)
//// 其他数据类型
//data class DataString(
//    @SerializedName("appKey") val appKey: String,
//    @SerializedName("content") val content: String,
//    @SerializedName("createTime") val createTime: Long,
//    @SerializedName("id") val id: Long,
//    @SerializedName("status") val status: Int,
//    @SerializedName("toUserId") val toUserId: Long,
//    @SerializedName("userId") val userId: Long
//) : Data()
//
//data class DataObject(val obj: Any) : Data()
//
//data class DataList(val list: List<MessageListData>) : Data()
//
//data class UserDetails(
//    @SerializedName("addr") val addr: String,
//    @SerializedName("avatar") val avatar: String,
//    @SerializedName("content") val content: String,
//    @SerializedName("createTime") val createTime: Long,
//    @SerializedName("id") val id: Long,
//    @SerializedName("imageCode") val imageCode: Int,
//    @SerializedName("imageUrlList") val imageUrlList: List<ImageUrl>,
//    @SerializedName("price") val price: Int,
//    @SerializedName("status") val status: Int,
//    @SerializedName("tUserId") val tUserId: Long,
//    @SerializedName("tuserId") val tuserId: Long,
//    @SerializedName("typeId") val typeId: Int,
//    @SerializedName("typeName") val typeName: String,
//    @SerializedName("username") val username: String
//) : Data()
//
//// 消息数据
//data class PaginatedData<T>(
//    @SerializedName("current") val current: Int,
//    @SerializedName("records") val records: List<T>,
//    @SerializedName("size") val size: Int,
//    @SerializedName("total") val total: Int
//) : Data()
//
//data class RevenueData(
//    @SerializedName("TotalRevenue") val totalRevenue: Int,
//    @SerializedName("TotalSpending") val totalSpending: Int
//) : Data()
//
//data class TransactionRecord(
//    @SerializedName("buyerAvatar") val buyerAvatar: String,
//    @SerializedName("buyerId") val buyerId: Long,
//    @SerializedName("buyerName") val buyerName: String,
//    @SerializedName("createTime") val createTime: Long,
//    @SerializedName("goodsDescription") val goodsDescription: String,
//    @SerializedName("goodsId") val goodsId: Long,
//    @SerializedName("id") val id: Long,
//    @SerializedName("imageUrlList") val imageUrlList: List<ImageUrl>,
//    @SerializedName("price") val price: Int,
//    @SerializedName("sellerAvatar") val sellerAvatar: String,
//    @SerializedName("sellerId") val sellerId: Long,
//    @SerializedName("sellerName") val sellerName: String
//) : Data()