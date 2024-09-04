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

        // 检查是否存在 `type` 字段
        val typeElement = jsonObject.get("type")

        return if (typeElement != null && typeElement.isJsonPrimitive) {
            when (typeElement.asString) {
                "DataString" -> context.deserialize(jsonObject, Data.DataString::class.java)
                "UserDetails" -> context.deserialize(jsonObject, Data.UserDetails::class.java)
                "PaginatedData" -> context.deserialize(jsonObject, Data.PaginatedData::class.java)
                "RevenueData" -> context.deserialize(jsonObject, Data.RevenueData::class.java)
                "TransactionRecord" -> context.deserialize(jsonObject, Data.TransactionRecord::class.java)
                "User" -> context.deserialize(jsonObject, Data.User::class.java)
                else -> throw JsonParseException("Unknown element type: ${typeElement.asString}")
            }
        } else {
            // 如果 `type` 字段不存在或不是有效的字符串，则返回 null
            null
        }
    }
}
