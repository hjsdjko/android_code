package com.union.union_basic

import com.tencent.mmkv.MMKV
import com.union.union_basic.image.loader.GlideLoader
import com.union.union_basic.image.loader.ImageLoaderHelper
import com.union.union_basic.utils.AppUtils
/**
 * classname：BasicInit
 * desc:基础库初始化
 */
object BasicInit {

    //基础库需要初始化的一些代码
    fun init(){
        MMKV.initialize(AppUtils.getApp())
        ImageLoaderHelper.initLoader(GlideLoader)
    }
}