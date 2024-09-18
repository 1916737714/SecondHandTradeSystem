package com.example.andoirdsecondhandtradingsystem.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

// 创建一个 Interceptor 来添加请求头
class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Accept", "application/json, text/plain, */*")
            .header("Content-Type", "application/json")
            .header("appId", "e1bdf675e771485192e10e1e4396f1ec")
            .header("appSecret", "172804bfe98ab0c6649e2938c8370cd6e010a")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

object RetrofitClient {
    private const val BASE_URL = "https://api-store.openguet.cn/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //控制访问时间
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(null)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(HeaderInterceptor()) // 添加自定义拦截器
            .build()
    }
    //反序列中注册
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Data::class.java, DataDeserializer())
        .create()


    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }
}
