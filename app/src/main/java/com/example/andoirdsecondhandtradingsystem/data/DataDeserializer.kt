package com.example.andoirdsecondhandtradingsystem.data

import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
//gson反序列化，匹配Apisponse的data字段
class DataDeserializer : JsonDeserializer<Data?> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Data? {

        return when {
            json.isJsonArray -> {
                // 处理 JsonArray 格式的数据
                val messageList = context.deserialize<List<Data.MessageListData>>(
                    json,
                    object : TypeToken<List<Data.MessageListData>>() {}.type
                )
                Data.DataWrapper(messageList)
            }

            json.isJsonObject -> {
                val jsonObject = json.asJsonObject

                if (jsonObject.has("records")) {
                    // 如果包含 "records" 字段，则反序列化为 MessageDatail
                    context.deserialize<Data.MessageDatail>(jsonObject, Data.MessageDatail::class.java)
                } else {
                    // 否则，假设是 User 类型
                    context.deserialize<Data.User>(jsonObject, Data.User::class.java)
                }
            }

            else -> null
        }
    }
}



//        return when {
//            jsonObject.has("appKey") && jsonObject.has("username") -> {
//                context.deserialize(jsonObject, Data.User::class.java)
//            }
//            jsonObject.has("totalRevenue") && jsonObject.has("totalSpending") -> {
//                context.deserialize(jsonObject, Data.RevenueData::class.java)
//            }
//            jsonObject.has("buyerAvatar") && jsonObject.has("sellerAvatar") -> {
//                context.deserialize(jsonObject, Data.TransactionRecord::class.java)
//            }
//            jsonObject.has("current") && jsonObject.has("total") -> {
//                context.deserialize(jsonObject, Data.PaginatedData::class.java)
//            }
//            jsonObject.has("content") -> {
//                context.deserialize(jsonObject, Data.DataString::class.java)
//            }
//            jsonObject.has("fromUserId") && jsonObject.has("username") && jsonObject.has("unReadNum") -> {
//                context.deserialize(jsonObject, Data.MessageListData::class.java)
//            }
//            jsonObject.has("data") && jsonObject.get("data").isJsonArray -> {
//                context.deserialize(jsonObject, Data.MessageListDataArray::class.java)
//            }
//            else -> null
//        }
