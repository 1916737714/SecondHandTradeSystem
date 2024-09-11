package com.example.andoirdsecondhandtradingsystem.data

import com.google.gson.*
import java.lang.reflect.Type
//gson反序列化，匹配Apisponse的data字段
class DataDeserializer : JsonDeserializer<Data?> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Data? {
        if (!json.isJsonObject) {
            return null
        }

        val jsonObject = json.asJsonObject

        // 尝试匹配不同的 Data 子类
        return when {
            // 根据具体字段来确定类型
            jsonObject.has("appKey") && jsonObject.has("username") -> context.deserialize(jsonObject, Data.User::class.java)
            jsonObject.has("totalRevenue") && jsonObject.has("totalSpending") -> context.deserialize(jsonObject, Data.RevenueData::class.java)
            jsonObject.has("buyerAvatar") && jsonObject.has("sellerAvatar") -> context.deserialize(jsonObject, Data.TransactionRecord::class.java)
            jsonObject.has("current") && jsonObject.has("total") -> context.deserialize(jsonObject, Data.PaginatedData::class.java)
            jsonObject.has("content") -> context.deserialize(jsonObject, Data.DataString::class.java)
            jsonObject.has("fromUserId") && jsonObject.has("username") && jsonObject.has("unReadNum") -> context.deserialize(jsonObject, Data.MessageListData::class.java)
            // 其他类型的判断...
            else -> null
        }
    }
}
