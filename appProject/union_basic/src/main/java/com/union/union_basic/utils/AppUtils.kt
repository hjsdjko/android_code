package com.union.union_basic.utils

import android.app.Application
import android.provider.Settings
import com.union.union_basic.ext.toConversion
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object AppUtils {

    var currentApplication: Application? = null

    @JvmStatic
    fun getApp(): Application {
        if (currentApplication == null) {
            currentApplication = Class.forName("android.app.ActivityThread").let {
                it.getMethod("getApplication")
                    .invoke(it.getMethod("currentActivityThread").invoke(null))
                    .toConversion<Application>()
            }
        }
        return currentApplication!!
    }

    fun getAndroidID(): String { //获取设备id
        val id = Settings.Secure.getString(getApp().contentResolver, Settings.Secure.ANDROID_ID)
        return if ("9774d56d682e549c" == id) "" else id ?: ""
    }

    fun getAppVersionName(): String { //获取版本名
        return getApp().packageManager.getPackageInfo(getApp().packageName, 0).versionName
    }

    fun toMD5(password: String): String { //MD5l加密
        try {
            val digest: ByteArray =
                MessageDigest.getInstance("MD5").digest(password.toByteArray()) //对字符串加密，返回字节数组
            var sb = StringBuffer()
            for (b in digest) {
                var i: Int = b.toInt() and 0xff //获取低八位有效值
                var hexString = Integer.toHexString(i) //将整数转化为16进制
                if (hexString.length < 2) {
                    hexString = "0$hexString" //如果是一位的话，补0
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}

