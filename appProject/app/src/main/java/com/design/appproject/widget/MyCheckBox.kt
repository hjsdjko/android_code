package com.design.appproject.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

/*
自定义解析css属性值的CheckBox
* */
class MyCheckBox : androidx.appcompat.widget.AppCompatCheckBox,CssInterface {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }
    private val mCssParseHelper by lazy {
        CssParseHelper()
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.let { mCssParseHelper.init(context, it,this) }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.let { mCssParseHelper.dispatchDraw(it) }
    }
    override fun onDraw(canvas: Canvas?) {
        canvas?.let { mCssParseHelper.drwableShadow(it) }
        super.onDraw(canvas)
    }

    override fun getPostion()=mCssParseHelper.mCssPosition
}