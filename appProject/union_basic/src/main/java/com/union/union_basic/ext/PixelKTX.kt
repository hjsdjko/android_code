package com.union.union_basic.ext

import android.content.res.Resources
import android.util.TypedValue

/**
 * 把dp值转成px值
 */
val Float.dp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

/**
 * 把dp值转成px值
 */
val Int.dp
    get() = this.toFloat().dp.toInt()

/**
 * 将从css上的值单位转为px值
 */
val String.cssdp
    get() =if (this.endsWith("rpx")){
        (this.replace("rpx","").toFloat()/2).dp
    }else if (this.endsWith("px")){
        (this.replace("px","").toFloat()).dp
    }else if (this.endsWith("dp")){
        (this.replace("dp","").toFloat()).dp
    } else if(this.isNullOrEmpty()){
        0f.dp
    }else{
        (this.toFloat()).dp
    }