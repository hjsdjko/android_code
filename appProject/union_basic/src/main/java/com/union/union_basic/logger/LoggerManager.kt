package com.union.union_basic.logger

import android.util.Log
import com.union.union_basic.ext.yes

/**
 * classname：LoggerManager
 * desc: 日志管理类
 */
object LoggerManager {

    private const val LOG_TAG = "BASIC_LOGGER"

    var isShow = true

    fun initLogger(isShow: Boolean) {
        this.isShow = isShow
    }

    fun d(content: String, tag: String = LOG_TAG) {
        isShow.yes {
            Log.d(tag, content)
        }
    }

    fun e(content: String, tag: String = LOG_TAG) {
        isShow.yes {
            Log.e(tag, content)
        }
    }

    fun i(content: String, tag: String = LOG_TAG) {
        isShow.yes {
            Log.i(tag, content)
        }
    }

}