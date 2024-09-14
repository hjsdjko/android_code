package com.design.appproject.logic.repository

import com.design.appproject.base.NetRetrofitClient
import com.design.appproject.logic.api.HomeApi
import com.design.appproject.utils.Utils
import com.google.gson.Gson
import com.union.union_basic.network.BaseRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Query

/**首页相关api*/
object HomeRepository : BaseRepository() {

    val homeService by lazy { NetRetrofitClient.create<HomeApi>() }

    inline fun <reified T> page(tableName: String, map: Map<String, String>) = fireList<T> {
        homeService.page(tableName = tableName, map).await()
    }

    inline fun <reified T> info(tableName: String, id: Long) = fireY<T> {
        homeService.info(tableName = tableName, id).await()
    }

    inline fun <reified T> add(tableName: String, body: Any) = fireY<T> {
        homeService.add(tableName = tableName, body).await()
    }

    inline fun <reified T> save(tableName: String, body: Any) = fireY<T> {
        homeService.save(tableName = tableName, body).await()
    }
     inline fun <reified T> delete(tableName: String, body: Any,callback:Int?=null) = fireY<T>(callBack = callback) {
        val body = Gson().toJson(body).toRequestBody("application/json; charset=utf-8".toMediaType())
        homeService.delete(tableName = tableName, body).await()
    }

    inline fun <reified T> list(tableName: String, map: Map<String, String>) = fireList<T> {
        homeService.list(tableName = tableName, map).await()
    }

    inline fun <reified T> autoSort(tableName: String, map: Map<String, String>) = fireList<T> {
        homeService.autoSort(tableName = tableName, map).await()
    }

    inline fun <reified T> autoSort2(tableName: String, map: Map<String, String>) = fireList<T> {
        homeService.autoSort2(tableName = tableName, map).await()
    }

     fun updateBrowseDuration(tableName: String,id:String, duration: String) = fireX {
        homeService.updateBrowseDuration(tableName = tableName, id,duration).await()
    }

    fun matchFace(params:Map<String, String>) = fireX {
        homeService.matchFace(params).await()
    }

    fun rubbish(tableName: String,fileName: String) = fireX {
        homeService.rubbish(tableName,fileName).await()
    }

    inline fun <reified T> baiduIdentify(tableName: String,type:String,fileName: String) = fireY<T> {
        homeService.baiduIdentify(tableName,type,fileName).await()
    }


}