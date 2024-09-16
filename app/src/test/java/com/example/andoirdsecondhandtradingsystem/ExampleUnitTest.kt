package com.example.andoirdsecondhandtradingsystem

import com.example.andoirdsecondhandtradingsystem.data.Data
import com.example.andoirdsecondhandtradingsystem.data.DataDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Data::class.java, DataDeserializer())
        .create()

    @Test
    fun testDeserializeMessageListData() {
        val json = """
            {
                "fromUserId": "123",
                "username": "john_doe",
                "unReadNum": 5
            }
        """
        val data = gson.fromJson(json, Data::class.java)
        assertTrue(data is Data.MessageListData)
        val messageData = data as Data.MessageListData
        assertEquals("123", messageData.fromUserId)
        assertEquals("john_doe", messageData.username)
        assertEquals(5, messageData.unReadNum)
    }

    @Test
    fun testDeserializeMessageListDataArray() {
        val json = """
            {
                "data": [
                    {
                        "fromUserId": "123",
                        "username": "john_doe",
                        "unReadNum": 5
                    },
                    {
                        "fromUserId": "456",
                        "username": "john",
                        "unReadNum": 6
                    }
                ]
            }
        """
        val data = gson.fromJson(json, Data::class.java)
        assertTrue(data is Data.MessageListDataArray)
        val messageListData = data as Data.MessageListDataArray
        assertEquals(2, messageListData.messages.size)
        val messageData = messageListData.messages[1]
        assertEquals("456", messageData.fromUserId)
        assertEquals("john", messageData.username)
        assertEquals(6, messageData.unReadNum)
    }
}