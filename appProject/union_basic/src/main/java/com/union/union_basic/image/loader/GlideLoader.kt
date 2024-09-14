package com.union.union_basic.image.loader

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * classname：GlideLoader
 * desc: glide图片库
 */
object GlideLoader : ILoader {

    override fun load(context: Context, imageView: ImageView, drawableRes: Int, radius: Int, errorResId: Int,
        placeholderResId: Int) {
        Glide.with(context).load(drawableRes).apply(mRequestOptions).apply {
            if (radius != 0) {
                this.transform(RoundedCorners(radius))
            }
            if (errorResId != 0) {
                this.error(errorResId)
            }
            if (placeholderResId != 0) {
                this.placeholder(placeholderResId)
            }
        }.into(imageView)
    }

    override fun load(context: Context, imageView: ImageView, url: String?, radius: Int, errorResId: Int,
        placeholderResId: Int) {
        Glide.with(context).load(url).apply(mRequestOptions).apply {
            if (radius != 0) {
                this.transform(RoundedCorners(radius))
            }
            if (errorResId != 0) {
                this.error(errorResId)
            }
            if (placeholderResId != 0) {
                this.placeholder(placeholderResId)
            }
        }.into(imageView)
    }

    override fun load(context: Context, imageView: ImageView, image: ByteArray?, radius: Int, errorResId: Int,
        placeholderResId: Int) {
        Glide.with(context).load(image).apply(mRequestOptions).apply {
            if (radius != 0) {
                this.transform(RoundedCorners(radius))
            }
            if (errorResId != 0) {
                this.error(errorResId)
            }
            if (placeholderResId != 0) {
                this.placeholder(placeholderResId)
            }
        }.into(imageView)
    }

    private val mRequestOptions: RequestOptions = RequestOptions() //glide的配置内容

    fun setRequestOptions(errorResId: Int, placeholderResId: Int) {
        mRequestOptions.error(errorResId).placeholder(placeholderResId)
            .override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL) //关键代码，加载原始大小
            .format(DecodeFormat.PREFER_RGB_565) //设置为这种格式去掉透明度通道，可以减少内存占有
    }

}