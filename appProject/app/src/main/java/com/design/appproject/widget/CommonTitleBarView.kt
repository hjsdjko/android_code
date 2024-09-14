package com.design.appproject.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import com.union.union_basic.ext.no
import com.union.union_basic.ext.toConversion
import kotlin.properties.Delegates

/**
 * classname：CommonTitleBarView
 * desc: 自定义基础头部控件
 */
class CommonTitleBarView : ConstraintLayout {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (isInEditMode) return
        init(context, attrs, defStyleAttr)
    }

    val mTitleTv by lazy { //标题
        findViewById<TextView>(R.id.title_tv)
    }

    val mLeftTv by lazy { //左边文字
        findViewById<TextView>(R.id.left_tv)
    }

    val mLeftImage by lazy { //左边图片
        findViewById<ImageFilterView>(R.id.left_image)
    }

    val mSmallTitle by lazy { //小标题
        findViewById<TextView>(R.id.small_title_tv)
    }

    val mBackIbtn by lazy { //左边返回图标
        findViewById<ImageButton>(R.id.back_ibtn)
    }

    val mRightIbtn2 by lazy { //右边图标2
        findViewById<ImageButton>(R.id.right_ibtn2)
    }
    val mRightIbtn3 by lazy { //右边图标3
        findViewById<ImageButton>(R.id.right_ibtn3)
    }
    val mRightIbtn4 by lazy { //右边图标3
        findViewById<ImageButton>(R.id.right_ibtn4)
    }
    val mRightIbtn by lazy { //右边图标
        findViewById<ImageButton>(R.id.right_ibtn)
    }

    val mRightTextView by lazy { //右边文字
        findViewById<TextView>(R.id.right_tv)
    }

    val mDividerView by lazy { //下划线
        findViewById<View>(R.id.divider_view)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        isClickable = true
        inflate(getContext(), R.layout.common_layout_base_toolbar, this)
        initAttrs(attrs)
        mBackIbtn.setOnClickListener {
            context.toConversion<Activity>()?.finish()
        }
    }

    private var mIsTranslucent = false //是否沉侵式

    private var mRightColor by Delegates.notNull<Int>()

    private fun initAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBarView)
        val title = typedArray.getString(R.styleable.CommonTitleBarView_common_title)
        val smallTitle = typedArray.getString(R.styleable.CommonTitleBarView_common_smalltitle)
        val rightText = typedArray.getString(R.styleable.CommonTitleBarView_common_rightText)
        val leftVisible =
            typedArray.getBoolean(R.styleable.CommonTitleBarView_common_leftVisible, true)
        val rightSrc = typedArray.getResourceId(R.styleable.CommonTitleBarView_common_rightSrc, 0)
        mRightColor = typedArray.getColor(
            R.styleable.CommonTitleBarView_common_rightTextColor,
            ColorUtils.getColor(R.color.common_colorPrimary)
        )
        val titleColor = typedArray.getColor(
            R.styleable.CommonTitleBarView_common_titleColor,
            ColorUtils.getColor(R.color.common_title_color)
        )
        val leftSrc = typedArray.getResourceId(R.styleable.CommonTitleBarView_common_leftSrc, 0)
        mIsTranslucent =
            typedArray.getBoolean(R.styleable.CommonTitleBarView_common_isTranslucent, false)
        if (rightSrc != 0) {
            mRightIbtn.visibility = View.VISIBLE
            mRightIbtn.setImageResource(rightSrc)
            mRightIbtn.setColorFilter(titleColor)
        }
        if (leftSrc != 0) {
            mBackIbtn.setImageResource(leftSrc)
        }
        mBackIbtn.isVisible = leftVisible
        setBackColor(R.color.navigation_color)
        rightText.isNullOrEmpty().no {
            mRightTextView.visibility = VISIBLE
            mRightTextView.text = rightText
            mRightTextView.setTextColor(mRightColor)
        }
        val lineVisible =
            typedArray.getBoolean(R.styleable.CommonTitleBarView_common_lineVisible, false)
        mTitleTv.setTextColor(titleColor)
        title.isNullOrEmpty().no {
            mTitleTv.text = title
        }
        smallTitle.isNullOrEmpty().no {
            mSmallTitle.isVisible = true
            mSmallTitle.text = smallTitle
        }
        mDividerView.visibility = if (lineVisible) VISIBLE else ConstraintLayout.GONE
    }

    fun set(elevation: Float) {
        this.elevation = elevation
    }

    fun setLineVisible(visible: Boolean) {
        mDividerView.visibility = if (visible) VISIBLE else ConstraintLayout.GONE
    }

    fun setLeftSrcImageResource(resId: Int) {
        mBackIbtn.setImageResource(resId)
    }

    fun setTitle(title: String) {
        mTitleTv.text = title
    }

    fun setSmallTitle(smalltitle: String) {
        smalltitle.isNullOrEmpty().no {
            mSmallTitle.isVisible = true
            mSmallTitle.text = smalltitle
        }
    }

    fun setLeftTitle(leftTitle: String) {
        leftTitle.isNullOrEmpty().no {
            mLeftTv.isVisible = true
            mLeftTv.text = leftTitle
        }
    }

    fun setTitleColor(color: Int) {
        mTitleTv.setTextColor(color)
    }

    fun setBackColor(color: Int) {
        mBackIbtn.setColorFilter(color)
    }

    fun setRightSrcImageResource(resId: Int) {
        mRightIbtn.isVisible = true
        mRightIbtn.setImageResource(resId)
    }

    fun setRightScrVisible(isVisible: Boolean) {
        mRightIbtn.isVisible = isVisible
    }

    fun setRightSrcImageResource2(resId: Int) {
        mRightIbtn2.isVisible = true
        mRightIbtn2.setImageResource(resId)
    }

    fun setRightSrcImageResource3(resId: Int) {
        mRightIbtn3.isVisible = true
        mRightIbtn3.setImageResource(resId)
    }

    fun setRightSrcImageResource4(resId: Int?) {
        mRightIbtn4.isVisible = resId != null
        resId?.let { mRightIbtn4.setImageResource(it) }
    }

    fun setRightText(content: String) {
        mRightTextView.visibility = View.VISIBLE
        mRightTextView.text = content
    }

    fun setOnLeftImageClickListener(block: () -> Unit) {
        mBackIbtn.setOnClickListener {
            block()
        }
    }

    fun setOnRightTextViewClickListener(block: () -> Unit) {
        mRightTextView.setOnClickListener {
            block()
        }
    }

    fun setOnRightSrcViewClickListener(block: () -> Unit) {
        mRightIbtn.setOnClickListener {
            block()
        }
    }

    fun setOnRightSrcViewClickListener2(block: () -> Unit) {
        mRightIbtn2.setOnClickListener {
            block()
        }
    }

    fun setOnRightSrcViewClickListener3(block: () -> Unit) {
        mRightIbtn3.setOnClickListener {
            block()
        }
    }
}
