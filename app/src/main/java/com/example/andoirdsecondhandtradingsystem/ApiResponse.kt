package com.example.andoirdsecondhandtradingsystem

import com.google.gson.annotations.SerializedName

// 通用的 API 响应类
data class ApiResponse(
    @SerializedName("msg") val msg: String,
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: Data?
)

// 使用 sealed class 来表示不同类型的 "data"
sealed class Data {
    data class DataString(
        @SerializedName("appKey") val appKey: String,
        @SerializedName("content") val content: String,
        @SerializedName("createTime") val createTime: Long,
        @SerializedName("id") val id: Long,
        @SerializedName("status") val status: Int,
        @SerializedName("toUserId") val toUserId: Long,
        @SerializedName("userId") val userId: Long
    ) : Data()

    data class DataObject(val obj: Any) : Data()

    data class DataList(val list: List<Any>) : Data()

    data class UserDetails(
        @SerializedName("addr") val addr: String,
        @SerializedName("avatar") val avatar: String,
        @SerializedName("content") val content: String,
        @SerializedName("createTime") val createTime: Long,
        @SerializedName("id") val id: Long,
        @SerializedName("imageCode") val imageCode: Int,
        @SerializedName("imageUrlList") val imageUrlList: List<ImageUrl>,
        @SerializedName("price") val price: Int,
        @SerializedName("status") val status: Int,
        @SerializedName("tUserId") val tUserId: Long,
        @SerializedName("tuserId") val tuserId: Long,
        @SerializedName("typeId") val typeId: Int,
        @SerializedName("typeName") val typeName: String,
        @SerializedName("username") val username: String
    ) : Data()

    data class PaginatedData<T>(
        @SerializedName("current") val current: Int,
        @SerializedName("records") val records: List<T>,
        @SerializedName("size") val size: Int,
        @SerializedName("total") val total: Int
    ) : Data()

    data class RevenueData(
        @SerializedName("TotalRevenue") val totalRevenue: Int,
        @SerializedName("TotalSpending") val totalSpending: Int
    ) : Data()

    data class TransactionRecord(
        @SerializedName("buyerAvatar") val buyerAvatar: String,
        @SerializedName("buyerId") val buyerId: Long,
        @SerializedName("buyerName") val buyerName: String,
        @SerializedName("createTime") val createTime: Long,
        @SerializedName("goodsDescription") val goodsDescription: String,
        @SerializedName("goodsId") val goodsId: Long,
        @SerializedName("id") val id: Long,
        @SerializedName("imageUrlList") val imageUrlList: List<ImageUrl>,
        @SerializedName("price") val price: Int,
        @SerializedName("sellerAvatar") val sellerAvatar: String,
        @SerializedName("sellerId") val sellerId: Long,
        @SerializedName("sellerName") val sellerName: String
    ) : Data()

    data class User(
        @SerializedName("appKey") val appKey: String,
        @SerializedName("avatar") val avatar: String,
        @SerializedName("id") val id: Long,
        @SerializedName("money") val money: Int,
        @SerializedName("password") val password: String,
        @SerializedName("username") val username: String
    ) : Data()

    // 通用的对象类，用于表示 `imageUrlList` 等
    data class ImageUrl(val placeholder: Any)
}

