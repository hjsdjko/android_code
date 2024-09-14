package com.union.union_basic.ext

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.view.*
import com.blankj.utilcode.util.ToastUtils

//类型转换
inline fun <reified T> Any.toConversion(): T? = if (this is T) {
    this
} else null

//toast
fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    ToastUtils.showShort(this)
}

fun Int.showToast(duration: Int = Toast.LENGTH_SHORT) {
    this.toString().showToast(duration)
}

/**
 * desc:方便设置textview左上右下图片，location：0-左，1-上，2-右，3-下
 */
fun TextView.drawableImg(@DrawableRes id: Int, location: Int, @ColorInt colorRes: Int = 0) {
    val drawable = if (id == 0) null else resources.getDrawable(id)
    drawableImg(drawable, location, colorRes)
}

fun TextView.drawableImg(drawable: Drawable?, location: Int, @ColorInt colorRes: Int = 0) {
    if (drawable != null && colorRes != 0) {
        drawable.setTint(colorRes)
    }
    when (location) {
        0 -> setCompoundDrawablesWithIntrinsicBounds(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3])
        1 -> setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], drawable, compoundDrawables[2], compoundDrawables[3])
        2 -> setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], drawable, compoundDrawables[3])
        3 -> setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], drawable)
    }
}

//margin扩展
fun View.marginKTX(left: Int = marginLeft, top: Int = marginTop, right: Int = marginRight, bottom: Int = marginBottom) {
    layoutParams = layoutParams.toConversion<ViewGroup.MarginLayoutParams>()?.apply {
        setMargins(left, top, right, bottom)
    }
}