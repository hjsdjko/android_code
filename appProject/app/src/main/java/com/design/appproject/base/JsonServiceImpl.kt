package com.design.appproject.base

import android.content.Context
import android.util.LruCache
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.SerializationService
import java.lang.reflect.Type
import java.util.*

/**
 * classname：JsonServiceImpl
 * desc:arouter传递自定义对象里时的序列与反序列化
 */
@Route(path = "/service/json")
class JsonServiceImpl : SerializationService {
    private val cacheInstance by lazy {
        val maxMemory = Runtime.getRuntime().maxMemory()
        val cacheSize = (maxMemory / 8).toInt()
        LruCache<String, Any>(cacheSize)
    }

    override fun init(context: Context) {

    }

    override fun object2Json(instance: Any): String {
        val uuid = UUID.randomUUID().toString()
        cacheInstance.put(uuid, instance)
        return uuid
    }

    override fun <T : Any> json2Object(input: String, clazz: Class<T>): T? {
        return parseObject(input, clazz)
    }

    override fun <T : Any> parseObject(uuid: String?, clazz: Type): T? {
        if (uuid.isNullOrEmpty()) {
            return null
        }
        val obj = cacheInstance.get(uuid)
        cacheInstance.remove(uuid)
        return obj as T?
    }
}