package com.union.union_basic.utils

import com.tencent.mmkv.MMKV
import com.union.union_basic.ext.showToast

/**
 * classname：StorageUtil
 * desc: 权限工具类
 */
object StorageUtil {

    private val mmkv by lazy { MMKV.defaultMMKV() }

    fun decodeBool(key: String, defaultValue: Boolean = false) = mmkv.decodeBool(key, defaultValue)
    fun decodeInt(key: String, defaultValue: Int = 0) = mmkv.decodeInt(key, defaultValue)
    fun decodeString(key: String, defaultValue: String = "") = mmkv.decodeString(key, defaultValue)
    fun decodeDouble(key: String, defaultValue: Double = 0.00) = mmkv.decodeDouble(key, defaultValue)
    fun decodeLong(key: String, defaultValue: Long = 0) = mmkv.decodeLong(key, defaultValue)
    fun decodeFloat(key: String, defaultValue: Float = 0f) = mmkv.decodeFloat(key, defaultValue)

    fun encode(key: String, valueType: Any) = when (valueType) {
        is Int -> mmkv.encode(key, valueType)
        is Boolean -> mmkv.encode(key, valueType)
        is String -> mmkv.encode(key, valueType)
        is Double -> mmkv.encode(key, valueType)
        is Long -> mmkv.encode(key, valueType)
        is Float -> mmkv.encode(key, valueType)
        else -> {
            "不支持的存储类型".showToast()
        }
    }
}