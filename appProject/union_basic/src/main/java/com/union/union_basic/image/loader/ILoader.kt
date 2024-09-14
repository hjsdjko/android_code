package com.union.union_basic.image.loader

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes

interface ILoader {
    fun load(context: Context, imageView: ImageView, @DrawableRes drawableRes: Int, radius: Int = 0, @DrawableRes errorResId: Int = 0,
        @DrawableRes placeholderResId: Int = 0)

    fun load(context: Context, imageView: ImageView, url: String?, radius: Int = 0, @DrawableRes errorResId: Int = 0, @DrawableRes placeholderResId: Int = 0)

    fun load(context: Context, imageView: ImageView, image: ByteArray?, radius: Int = 0, @DrawableRes errorResId: Int = 0,
        @DrawableRes placeholderResId: Int = 0)
}