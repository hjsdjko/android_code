package com.design.appproject.logic.repository

import com.design.appproject.base.NetRetrofitClient
import com.design.appproject.logic.api.UserApi
import com.union.union_basic.network.BaseRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**用户及登录相关*/
object UserRepository : BaseRepository() {

    val userService by lazy { NetRetrofitClient.create<UserApi>() }

    fun login(userName: String, password: String) = fireX {
        userService.login(username = userName, password = password).await()
    }
    fun faceLogin(face: String) = fireX {
        userService.faceLogin(face = face).await()
    }

    fun register(tableName: String, body: Any) = fireX {
        userService.register(tableName, body = body).await()
    }

    fun registerEmail(tableName: String, emailcode: String, body: Any) = fireX {
        userService.registerEmail(tableName, emailcode = emailcode, body = body).await()
    }

    fun registerSms(tableName: String, smscode: String, body: Any) = fireX {
        userService.registerSms(tableName, smscode = smscode, body = body).await()
    }

    fun option(
        tableName: String,
        columnName: String,
        conditionColumn: String?,
        conditionValue: String?,
        tag: String,
        isMultiSelect: Boolean
    ) = fireX(callBack = Pair(tag, isMultiSelect)) {
        userService.option(tableName, columnName, conditionColumn, conditionValue).await()
    }

    fun sendemail(tableName: String, email: String) = fireX {
        userService.sendemail(tableName, email).await()
    }

    fun sendsms(tableName: String, mobile: String) = fireX {
        userService.sendsms(tableName, mobile).await()
    }

    inline fun <reified T> session() = fireY<T> {
        userService.session().await()
    }

    inline fun <reified T> security(tableName: String, userName: String) = fireY<T> {
        userService.security(tableName = tableName,username=userName).await()
    }

    fun update(tableName: String,body: Any, tag: String="") = fireX(callBack = tag) {
        userService.update(tableName = tableName,body=body).await()
    }

    fun upload(file: File, tag: String) = fireX(callBack = tag) {
        val requestFile = file.asRequestBody("file/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        userService.upload(body).await()
    }
}