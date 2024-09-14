package com.design.appproject.widget

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.RegexUtils
import com.design.appproject.R
import com.union.union_basic.ext.*
import com.union.union_basic.logger.LoggerManager
import java.util.*
import kotlin.concurrent.timerTask

/**
 * classname：VButton
 * author：ZWQ
 * date: 2021/12/1 11:05
 * desc: 获取验证码的自定义button
 */
class VButton : androidx.appcompat.widget.AppCompatButton {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private var mSecond = 60 //剩余秒数
    private val mTimer by lazy {
        Timer()
    }
    var mTimerTask: TimerTask? = null


    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        isSelected = true
        gravity = Gravity.CENTER
        text = "获取验证码"
        textSize = 14f
        attrs?.let { mCssParseHelper.init(context, it,this) }
    }

    private fun getTask() = timerTask {
        mSecond--
        (context as Activity).runOnUiThread {
            text = "$mSecond 秒后重试"
        }
        if (mSecond <= 0) {
            cancel()
            (context as Activity).runOnUiThread {
                text = "获取验证码"
                isSelected = true
                isClickable = true
            }
        }
    }

    //点击获取验证码
    fun setOnGetVClickListener(
        phoneEt: EditText,
        type: Int = 0,
        block: (phoneNumber: String) -> Unit
    ) {
        setOnClickListener { //点击
            isSelected.yes { //当前可以被选择，回调block，并且设置为不可点击且开始倒计时
                val right = when (type) {
                    0 -> {
                        val tem = RegexUtils.isMobileExact(phoneEt.text.toString())
                        tem.no {
                            "请输入正确手机号".showToast()
                        }
                        tem
                    }
                    else -> {
                        val tem = RegexUtils.isEmail(phoneEt.text.toString())
                        tem.no {
                            "请输入正确的邮箱".showToast()
                        }
                        tem
                    }
                }
                right.yes {
                    this@VButton.isClickable = false
                    this@VButton.isSelected = false
                    block(phoneEt.text.toString())
                    mSecond = 60
                    mTimerTask = getTask()
                    mTimer.schedule(mTimerTask, 0, 1000)
                }
            }.otherwise {
                "请输入正确号码".showToast()
            }
        }
    }

    override fun onDetachedFromWindow() {
        mTimerTask?.cancel()
        mTimer.cancel()
        super.onDetachedFromWindow()
    }

    private val mCssParseHelper by lazy {
        CssParseHelper()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.let { mCssParseHelper.dispatchDraw(it) }
    }
}