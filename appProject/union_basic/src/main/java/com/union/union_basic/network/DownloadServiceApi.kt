package com.union.union_basic.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadServiceApi {

    /**
     * 下载文件
     * 如果下载大文件的一定要加上  @Streaming  注解
     * @param fileUrl 文件的路径
     * @return 请求call
     */
    @Streaming
    @GET
    fun downloadOtaFileLoad(@Url fileUrl: String): Call<ResponseBody>
}