package com.design.appproject.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import net.lucode.hackware.magicindicator.MagicIndicator

/*
自定义解析css属性值的GridLayout
* */
class MyIndicator : MagicIndicator,CssInterface {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init(context, attrs)
    }
    private val mCssParseHelper by lazy {
        CssParseHelper()
    }

    private fun init(context: Context, attrs: AttributeSet?) {
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