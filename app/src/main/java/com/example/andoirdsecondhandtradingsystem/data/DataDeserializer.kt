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
                val jsonArray = json.asJsonArray
                if (jsonArray.size() > 0 && jsonArray[0].isJsonObject && jsonArray[0].asJsonObject.has("type")) {
                    // 处理 GoodsType 格式的数据
                    val goodsTypeList = context.deserialize<List<Data.goodsType>>(
                        jsonArray,
                        object : TypeToken<List<Data.goodsType>>() {}.type
                    )
                    Data.goodsTypeList(goodsTypeList)
                } else {
                    // 默认处理 MessageListData 格式的数据
                    val messageList = context.deserialize<List<Data.MessageListData>>(
                        jsonArray,
                        object : TypeToken<List<Data.MessageListData>>() {}.type
                    )
                    Data.DataWrapper(messageList)
                }
            }

            json.isJsonObject -> {
                val jsonObject = json.asJsonObject

                if (jsonObject.has("records")) {
                    //判断record是否有tUserId字段
                    val recordsArray = jsonObject.getAsJsonArray("records")
                    var containsTUserId = false

                    for (element in recordsArray) {
                        val recordObject = element.asJsonObject
                        if (recordObject.has("tUserId")) {
                            containsTUserId = true
                            break
                        }
                    }

                    if (containsTUserId) {
                        //反序列化成已保存商品信息
                        context.deserialize<Data.saveGoodsList>(jsonObject, Data.saveGoodsList::class.java)
                    } else {
                        //反序列成消息详情
                        context.deserialize<Data.MessageDatail>(jsonObject, Data.MessageDatail::class.java)
                    }


                }else if (jsonObject.has("imageUrlList")){
                    //反序列成上传文件
                    context.deserialize<Data.upLoadFile>(jsonObject, Data.upLoadFile::class.java)
                }
                else {
                    // 否则，假设是 User 类型
                    context.deserialize<Data.User>(jsonObject, Data.User::class.java)
                }
            }

            else -> null
        }
    }
}

