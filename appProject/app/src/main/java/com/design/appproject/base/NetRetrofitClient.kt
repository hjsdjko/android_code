package com.design.appproject.base

import com.union.union_basic.network.BaseRetrofitClient
import okhttp3.Interceptor

/**
 * classname：NetRetrofitClient
 * desc: 基础请求client类，需要初始化域名及请求头
 */
object NetRetrofitClient : BaseRetrofitClient() {
    override lateinit var BASE_URL: String
    override lateinit var intercepts: MutableList<Interceptor>

    fun initClient(baseUrl: String, interceptors: MutableList<Interceptor> = mutableListOf(Interceptor { chain ->
        chain.proceed(chain.request().newBuilder().build()) })) {
        BASE_URL = baseUrl
        intercepts = interceptors
    }
}