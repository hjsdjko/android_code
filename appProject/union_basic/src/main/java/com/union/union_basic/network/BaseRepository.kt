package com.union.union_basic.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.union.union_basic.ext.*
import com.union.union_basic.logger.LoggerManager
import kotlinx.coroutines.Dispatchers
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException
import java.text.ParseException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * classname：BaseRepository
 * desc: 网络基础仓库类，统一处理请求回调相关
 */
open class BaseRepository {

    /**
     *利用协程统一进行try-catch处理
     * @param context CoroutineContext
     * @param block SuspendFunction0<Result<T>>
     * @return LiveData<Result<T>>
     */
    fun <T> fireX(
        context: CoroutineContext = Dispatchers.IO, callBack: Any? = null,
        block: suspend () -> T
    ): LiveData<Result<T>> = liveData(context) {
        val result = try {
            val T = block()
            if (T is BaseResultBean<*> && (T.code == NETWORK_SUCCESS_CODE || T.data != null)) {
                T.callBackData = callBack
                Result.success(T)
            } else {
                throw BaseException(
                    (T is BaseResultBean<*>).yes { (T as BaseResultBean<*>).code }
                        .otherwise { NETWORK_ERROR_UNKNOWN },
                    (T is BaseResultBean<*>).yes { (T as BaseResultBean<*>).msg ?: "" }
                        .otherwise { "请求出错了" })
            }
        } catch (e: BaseException) {
            Result.failure<T>(e)
        }
        emit(result)
    }

    /**
     *利用协程统一进行try-catch处理
     * @param context CoroutineContext
     * @param block SuspendFunction0<Result<T>>
     * @return LiveData<Result<T>>
     */
    inline fun <reified T> fireY(
        context: CoroutineContext = Dispatchers.IO, callBack: Any? = null,
        crossinline block: suspend () -> Any
    ): LiveData<Result<BaseResultBean<T>>> = liveData(context) {
        val result = try {
            val resultData = block()
            if (resultData is BaseResultBean<*> && (resultData.code == NETWORK_SUCCESS_CODE || resultData.data != null)) {
                resultData.callBackData = callBack
                val jsonString = GsonBuilder().enableComplexMapKeySerialization().create()
                    .toJson(resultData.data)
                var newResult: T = Gson().fromJson(jsonString,T::class.java)
                Result.success(BaseResultBean<T>(
                    code = resultData.code,
                    data = newResult,
                    msg = resultData.msg,
                    callBackData = callBack,
                    token = resultData.token,
                    file = resultData.file,
                ))//
            } else {
                throw BaseException(
                    (resultData is BaseResultBean<*>).yes { (resultData as BaseResultBean<*>).code }
                        .otherwise { NETWORK_ERROR_UNKNOWN },
                    (resultData is BaseResultBean<*>).yes {
                        (resultData as BaseResultBean<*>).msg ?: ""
                    }.otherwise { "请求出错了" })
            }
        } catch (e: BaseException) {
            Result.failure(e)
        }
        emit(result)
    }
    /**
     *利用协程统一进行try-catch处理
     * @param context CoroutineContext
     * @param block SuspendFunction0<Result<T>>
     * @return LiveData<Result<T>>
     */
    fun <T> fireData(
        context: CoroutineContext = Dispatchers.IO, block: suspend () -> T
    ): LiveData<Result<T>> = liveData(context) {
        val result = try {
            val T = block()
            Result.success(T)
        } catch (e: BaseException) {
            Result.failure<T>(e)
        }
        emit(result)
    }
    /**
     *利用协程统一进行try-catch处理
     * @param context CoroutineContext
     * @param block SuspendFunction0<Result<T>>
     * @return LiveData<Result<T>>
     */
    inline fun <reified T> fireList(
        context: CoroutineContext = Dispatchers.IO, callBack: Any? = null,
        crossinline block: suspend () -> Any
    ): LiveData<Result<BaseResultBean<PageResultBean<T>>>> = liveData(context) {
        val result = try {
            val resultData = block()
            if (resultData is BaseResultBean<*> && (resultData.code == NETWORK_SUCCESS_CODE || resultData.data != null)) {
                val gsonBuilder = GsonBuilder().enableComplexMapKeySerialization().create()
                val jsonString = gsonBuilder.toJson(resultData.data)
                var resultList: PageResultBean<T> = Gson().fromJson(
                    jsonString,
                    object : TypeToken<PageResultBean<T>>() {}.type
                )
                val listJson = gsonBuilder.toJson(resultList.list)
                var newList = getDataList(listJson, T::class.java)
                Result.success(
                    BaseResultBean<PageResultBean<T>>(
                        code = resultData.code,
                        data = resultList.apply { list = newList },
                        msg = resultData.msg,
                        callBackData = callBack,
                        token = resultData.token,
                        file = resultData.file,
                    )
                )
            } else {
                throw BaseException(
                    (resultData is BaseResultBean<*>).yes { (resultData as BaseResultBean<*>).code }
                        .otherwise { NETWORK_ERROR_UNKNOWN },
                    (resultData is BaseResultBean<*>).yes {
                        (resultData as BaseResultBean<*>).msg ?: ""
                    }
                        .otherwise { "请求出错了" })
            }
        } catch (e: BaseException) {
            Result.failure(e)
        }
        emit(result)
    }

    open fun <T> getDataList(listJson: String, cls: Class<T>): List<T> { //这里是Class<T>
        val list: MutableList<T> = ArrayList()
        val jsonArray = JsonParser().parse(listJson).asJsonArray
        val gson = Gson()
        for (jsonElement in jsonArray) {
            list.add(gson.fromJson(jsonElement, cls))
        }
        return list
    }

    /**
     * 通过挂起函数封装，省去了繁琐的接口回调
     * @receiver Call<T>
     * @return T
     */
    suspend fun <T> Call<T>.await(showErrorMessage: Boolean = true): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        if (body is BaseResultBean<*>) { //处理统一错误码
                            if (body.code != NETWORK_SUCCESS_CODE) {
                                LoggerManager.d(body.msg ?: "")
                                if (showErrorMessage && body.msg.isNotNullOrEmpty()) {
                                    body.msg?.showToast()
                                }
                                continuation.resumeWithException(
                                    BaseException(
                                        body.code,
                                        body.msg ?: ""
                                    )
                                )
                            } else {
                                continuation.resume(body)
                            }
                        } else {
                            continuation.resume(body)
                        }
                    } else continuation.resumeWithException(
                        BaseException(
                            NETWORK_ERROR_UNKNOWN,
                            ""
                        )
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    LoggerManager.d(t.message + ":", NETWORK_ERROR_TAG)
                    when (t) {
                        is HttpException, is UnknownHostException -> continuation.resumeWithException(
                            BaseException(NETWORK_ERROR_NET, "网络错误，请检查网络")
                        ) //网络错误
                        is JsonParseException, is JSONException, is ParseException -> continuation.resumeWithException(
                            BaseException(NETWORK_ERROR_JSON, "解析错误，请稍后重试")
                        )
                        else -> continuation.resumeWithException(
                            BaseException(
                                NETWORK_ERROR_UNKNOWN,
                                "请求出错了，请稍后重试"
                            )
                        )
                    }
                }
            })
        }
    }

    companion object {
        //0 ,401 未登录  404未找到界面
        private const val NETWORK_ERROR_TAG = "NETWORK_ERROR_TAG"
        const val NETWORK_SUCCESS_CODE = 0 //成功
        const val NETWORK_ERROR_NEED_LOGIN = 401 //未登录
        const val NETWORK_ERROR_NOT_FIND = 404 //未找到界面
        const val NETWORK_ERROR_NET = 4004 //网络错误
        const val NETWORK_ERROR_UNKNOWN = 5000 //未知错误
        const val NETWORK_ERROR_JSON = 4005 //解析错误
    }
}