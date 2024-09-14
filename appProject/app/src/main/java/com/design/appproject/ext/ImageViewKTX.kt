package com.design.appproject.ext

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.blankj.utilcode.util.FileUtils
import com.design.appproject.ext.UrlPrefix.URL_PREFIX
import com.union.union_basic.image.loader.ImageLoaderHelper

object UrlPrefix {
    @JvmField
    open var URL_PREFIX = "" //图片地址前缀
}

fun ImageView.load(context: Context, @DrawableRes drawableRes: Int, radius: Int = 0) =
    run { ImageLoaderHelper.getInstance().load(context, this, drawableRes, radius) }

fun ImageView.load(context: Context, url: String?, radius: Int = 0,needPrefix:Boolean =true) = run {
    ImageLoaderHelper.getInstance()
        .load(context, this, if (FileUtils.isFileExists(url) || !needPrefix) url else URL_PREFIX + url, radius)
}
fun ImageView.load(context: Context, url: String?, radius: Int = 0,needPrefix:Boolean =true,@DrawableRes errorResId: Int = 0) = run {
    ImageLoaderHelper.getInstance()
        .load(context, this, if (FileUtils.isFileExists(url) || !needPrefix) url else URL_PREFIX + url, radius,errorResId=errorResId)
}
fun ImageView.load(context: Context, image: ByteArray?, radius: Int = 0) =
    run { ImageLoaderHelper.getInstance().load(context, this, image, radius) }


