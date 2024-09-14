package com.design.appproject.utils

import android.app.Activity
import com.alibaba.android.arouter.launcher.ARouter
import com.design.appproject.base.CommonArouteApi
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes

/**
 * 界面跳转工具类
 */
object ArouterUtils {

    fun startFragment(path: String, map: Map<String, Any>? = null, activity: Activity? = null, requestCode: Int = 0) {
        val postcard = ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_CONTAINER)
            .withString("mPath", path)
        if (!map.isNullOrEmpty()) {
            postcard.withObject("mParams", map)
        }
        (activity == null).yes {
            postcard.navigation()
        }.otherwise {
            postcard.navigation(activity, requestCode)
        }
    }
}