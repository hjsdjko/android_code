package com.union.union_basic.network

import android.util.Log.INFO
import com.google.gson.GsonBuilder
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.union.union_basic.logger.LoggerManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * classname：BaseRetrofitClient
 * desc:
 */
abstract class BaseRetrofitClient {

    abstract var BASE_URL: String //域名地址
    abstract var intercepts: MutableList<Interceptor> //

    private val httpClientBuilder by lazy {
        val hcb = OkHttpClient.Builder().protocols(Collections.singletonList(Protocol.HTTP_1_1))
        intercepts.forEach {
            hcb.addInterceptor(it)
        }
        hcb.addInterceptor(
            LoggingInterceptor.Builder()
                .setLevel(if (LoggerManager.isShow) Level.BASIC else Level.NONE).log(INFO)
                .request("Request").response("Response").build()
        ).build()
    }

    private val retrofit by lazy {
        Retrofit.Builder().client(httpClientBuilder).addConverterFactory(
            GsonConverterFactory.create(GsonBuilder().setDateFormat("HH:mm").create())
        )
            .baseUrl(BASE_URL).build()
    }

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}
