package com.design.appproject.base

import com.union.union_basic.ext.isNotNullOrEmpty
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/*
多域名拦截器
* */
class MoreBaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        //获取原始的originalRequest
        val originalRequest = chain.request()
        //获取老的url
        val oldUrl = originalRequest.url
        //获取originalRequest的创建者builder
        val builder = originalRequest.newBuilder()
        //获取头信息的集合如：manage,mdffx
        val newUrl = originalRequest.headers("NewUrl")
        if (!newUrl.isNullOrEmpty()) {
            //删除原有配置中的值,就是namesAndValues集合里的值
            builder.removeHeader("newUrl")
            val url = newUrl[0]
            //根据头信息中配置的value,来匹配新的base_url地址
            var baseURL: HttpUrl? = if (url.isNotNullOrEmpty()) {
                url.toHttpUrlOrNull()
            } else {
                NetRetrofitClient.BASE_URL.toHttpUrlOrNull()
            }
            //重建新的HttpUrl，需要重新设置的url部分
            val newHttpUrl = oldUrl.newBuilder()
                .scheme(baseURL!!.scheme)//http协议如：http或者https
                .host(baseURL.host)//主机地址
                .port(baseURL.port)//端口
                .removePathSegment(0)
                .build();
            //获取处理后的新newRequest
            val newRequest = builder.url(newHttpUrl).build()
            return chain.proceed(newRequest)
        } else {
            return chain.proceed(originalRequest)
        }
    }
}