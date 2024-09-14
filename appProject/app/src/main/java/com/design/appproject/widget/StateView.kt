package com.design.appproject.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.DrawableRes
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ScreenUtils
import com.design.appproject.R
import com.union.union_basic.ext.dp
import com.union.union_basic.ext.drawableImg

/**
 * classname：StateView
 * author：ZWQ
 * date: 2021/12/7 11:40
 * desc:情感图控件
 */
class StateView : androidx.appcompat.widget.AppCompatTextView {

    companion object {
        const val STATE_EMPTY = 1
        const val STATE_NET_ERROR = 2
        const val STATE_SERVICE_ERROR = 3
        const val STATE_NO_FIND = 4
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        textSize = 20f
        gravity = Gravity.CENTER
        compoundDrawablePadding = 5.dp
        setTextColor(ColorUtils.getColor(R.color.common_color_gray))
        setBackgroundResource(R.color.common_bg_color)
    }

    val emptyIcon: Drawable? = null
    var netWorkErrorIcon: Drawable? = null
    var serviceErrorIcon: Int = 0
    var noFindIcon: Drawable? = null

    fun setSate(
        state: Int,
        iconRes: Int = 0,
        message: String = "",
        refreshLoadCall: (() -> Unit)? = null
    ) {
        when (state) {
            STATE_EMPTY -> chageState(emptyIcon, "空空如也")
            STATE_NET_ERROR -> chageState(netWorkErrorIcon, "网络错误，请检查网络")
            STATE_SERVICE_ERROR -> chageState(
                if (iconRes > 0) iconRes else serviceErrorIcon,
                if (message.isNullOrEmpty()) "服务器开小差了~请稍后重试" else message
            )

            STATE_NO_FIND -> chageState(noFindIcon, "页面跑丢了，去看看别的吧")
        }
        setOnClickListener { refreshLoadCall?.invoke() }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (h >= ScreenUtils.getScreenHeight() / 2) {
            setPadding(
                paddingLeft,
                h / 2 - compoundDrawables[1].intrinsicHeight - compoundDrawablePadding / 2,
                paddingRight,
                h / 2 - compoundDrawables[1].intrinsicHeight - compoundDrawablePadding / 2
            )
        }
    }

    private fun chageState(@DrawableRes resId: Int, message: String) {
        text = message
        drawableImg(resId, 1)
    }

    private fun chageState(drawable: Drawable?, message: String) {
        text = message
        drawable?.let { drawableImg(it, 1) }
    }

}