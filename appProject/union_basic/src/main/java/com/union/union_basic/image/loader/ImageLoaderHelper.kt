package com.union.union_basic.image.loader

/**
 * classname：ImageLoaderHelper
 * desc: 统一图片加载库
 */
object ImageLoaderHelper {

    private lateinit var mImageLoader: ILoader

    fun initLoader(iLoader: ILoader) {
        mImageLoader = iLoader
    }

    fun getInstance(): ILoader {
        if (mImageLoader ==null) {
            throw Exception("需要初始化ImageLoader!!!")
        }
        return mImageLoader
    }

}