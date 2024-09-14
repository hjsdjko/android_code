package com.design.appproject.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import com.qmuiteam.qmui.widget.QMUIRadiusImageView2
import com.union.union_basic.ext.cssdp
import com.union.union_basic.ext.dp
import com.union.union_basic.ext.isNotNullOrEmpty
import com.union.union_basic.ext.yes
import com.union.union_basic.logger.LoggerManager

/*
自定义解析css属性值的ImageView
* */
class MyImageView : QMUIRadiusImageView2,CssInterface {
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

    override fun onDraw(canvas: Canvas?) {
        canvas?.let { mCssParseHelper.drwableShadow(it) }
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.let { mCssParseHelper.dispatchDraw(it) }
    }

    override fun getPostion()=mCssParseHelper.mCssPosition
}