package com.union.union_basic.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
/**
 * classname：CenterTextView
 * desc: drawable居中的textview
 */
class CenterTextView:AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val rect: Rect by lazy { Rect() }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (gravity != Gravity.CENTER) {
            return
        }

        // 计算文字内容的宽高
        paint.getTextBounds(text.toString(), 0, text.toString().length, rect)
        val textWidth: Int = rect.width()
        val textHeight: Int = rect.height()

        // final Drawable[] mShowing = new Drawable[4];
        val drawableLeft = compoundDrawables[0]
        val drawableTop = compoundDrawables[1]
        val drawableRight = compoundDrawables[2]
        val drawableBottom = compoundDrawables[3]

        // 移动距离 Gravity.CENTER 主要是计算左右drawable需要移动的距离，移除padding，移除内容，移除左右drawable的width，最后移除drawablePadding。
        val translationX =
            (width - (drawableLeft?.intrinsicWidth ?: 0) - (drawableRight?.intrinsicWidth
                ?: 0) - paddingLeft - paddingRight - textWidth) / 2 - compoundDrawablePadding
        val translationY =
            (height - (drawableTop?.intrinsicHeight ?: 0) - (drawableBottom?.intrinsicHeight
                ?: 0) - paddingTop - paddingBottom - textHeight) / 2 - compoundDrawablePadding

        // 设置左侧drawable 如果左侧存在
        drawableLeft?.apply {
            setBounds(translationX, 0, translationX + intrinsicWidth, intrinsicHeight)
        }
        // 设置右侧drawable 如果右侧存在
        drawableRight?.apply {
            // 右侧的drawable坐标轴原点（0，0）在右侧
            setBounds(-translationX, 0, -translationX + intrinsicWidth, intrinsicHeight)
        }

        // 设置上侧drawable 如果上侧存在
        drawableTop?.apply {
            setBounds(0, translationY, intrinsicWidth, translationY + intrinsicHeight)
        }
        // 设置下侧drawable 如果下侧存在
        drawableBottom?.apply {
            // 下侧的drawable坐标轴原点（0，0）在下侧
            setBounds(0, -translationY, intrinsicWidth, -translationY + intrinsicHeight)
        }
    }

}