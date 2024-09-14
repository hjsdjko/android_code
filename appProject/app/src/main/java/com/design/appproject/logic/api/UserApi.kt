package com.design.appproject.logic.api

import com.design.appproject.base.CommonBean
import com.union.union_basic.network.BaseResultBean
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
interface UserApi {
    /**登录*/
    @GET("{tableName}/login")
    fun login(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @Query("username") username: String,
        @Query("password") password: String,
    ): Call<BaseResultBean<Any?>>

    /**注册*/
    @POST("{tableName}/register")
    fun register(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @Body body: Any,
    ): Call<BaseResultBean<Any?>>

    /**邮箱注册*/
    @POST("{tableName}/register")
    fun registerEmail(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @Query("emailcode") emailcode: String,
        @Body body: Any,
    ): Call<BaseResultBean<Any?>>

    /**短信注册*/
    @POST("{tableName}/register")
    fun registerSms(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @Query("smscode") smscode: String,
        @Body body: Any,
    ): Call<BaseResultBean<Any?>>

    /**联动查询*/
    @GET("option/{tableName}/{columnName}")
    fun option(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @Path("columnName") columnName: String,
        @Query("conditionColumn") conditionColumn: String? = null,
        @Query("conditionValue") conditionValue: String? = null,
    ): Call<BaseResultBean<List<String>>>



    /**发送邮箱验证码*/
    @GET("{tableName}/sendemail")
    fun sendemail(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @Query("email") email: String,
    ): Call<BaseResultBean<Any>>

    /**发送短信证码*/
    @GET("{tableName}/sendsms")
    fun sendsms(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @Query("mobile") mobile: String,
    ): Call<BaseResultBean<Any>>

    /**上传文件、图片*/
    @Multipart
    @POST("file/upload")
    fun upload(@Part image: MultipartBody.Part): Call<BaseResultBean<Any?>>

    /**获取登陆用户信息*/
    @GET("{tableName}/session")
    fun session(@Path("tableName") tableName: String = CommonBean.tableName): Call<BaseResultBean<Any>>

    /**获取未登陆用户信息*/
    @GET("{tableName}/security")
    fun security(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @Query("username") username: String
    ): Call<BaseResultBean<Any>>

    /**更新账户信息*/
    @POST("{tableName}/update")
    fun update(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @Body body: Any
    ): Call<BaseResultBean<Any>>

    /**人脸识别登录*/
    @GET("{tableName}/faceLogin")
    fun faceLogin(
        @Path("tableName") tableName: String = CommonBean.tableName,
        @Query("face") face: String
    ): Call<BaseResultBean<Any>>

}