package com.design.appproject.logic.api

import com.design.appproject.base.CommonBean
import com.design.appproject.bean.FaceMathBean
import com.design.appproject.bean.RubbishBean

import com.union.union_basic.network.BaseResultBean
import com.union.union_basic.network.PageResultBean
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
interface HomeApi {

    /**查询 page*/
    @GET("{tableName}/page")
    fun page(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @QueryMap data: Map<String, String>
    ): Call<BaseResultBean<Any>>

    /**单条信息查询*/
    @GET("{tableName}/detail/{id}")
    fun info(
        @Path("tableName") tableName: String,
        @Path("id") id: Long
    ): Call<BaseResultBean<Any>>

    /**查询列表*/
    @GET("{tableName}/list")
    fun list(
        @Path("tableName") tableName: String,
        @QueryMap data: Map<String, String>
    ): Call<BaseResultBean<Any>>

    /**智能推荐*/
    @GET("{tableName}/autoSort")
    fun autoSort(
        @Path("tableName") tableName: String,
        @QueryMap data: Map<String, String>
    ): Call<BaseResultBean<Any>>

   /**智能推荐(按购买类型推荐)*/
    @GET("{tableName}/autoSort2")
    fun autoSort2(
        @Path("tableName") tableName: String,
        @QueryMap data: Map<String, String>
    ): Call<BaseResultBean<Any>>

    /**保存*/
    @POST("{tableName}/add")
    fun add(@Path("tableName") tableName: String, @Body body: Any): Call<BaseResultBean<Any>>

    /**保存*/
    @POST("{tableName}/save")
    fun save(@Path("tableName") tableName: String, @Body body: Any): Call<BaseResultBean<Any>>

    /**删除*/
    @POST("{tableName}/delete")
    fun delete(@Path("tableName") tableName: String, @Body body: RequestBody): Call<BaseResultBean<Any>>

    /**匹配*/
    @GET("matchFace")
    fun matchFace(@QueryMap params:Map<String, String>): Call<BaseResultBean<FaceMathBean>>

    /**更新浏览时间*/
    @POST("{tableName}/updateBrowseDuration/{id}")
    fun updateBrowseDuration(@Path("tableName") tableName: String,@Path("id") id: String, @Query("duration") duration: String): Call<BaseResultBean<Any>>

    /**垃圾识别*/
    @GET("{tableName}/aliyun/rubbish")
    fun rubbish(@Path("tableName") tableName: String,@Query("fileName") fileName: String): Call<BaseResultBean<RubbishBean>>

    /**百度识别（文字，动物，植物，菜品）*/
    @GET("{tableName}/baidu/{type}")
    fun baiduIdentify(@Path("tableName") tableName: String,@Path("type") type: String,@Query("fileName") fileName: String): Call<BaseResultBean<Any>>


}