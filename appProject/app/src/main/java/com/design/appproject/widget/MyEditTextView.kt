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
import com.design.appproject.R
import com.qmuiteam.qmui.layout.IQMUILayout
import com.qmuiteam.qmui.layout.QMUILayoutHelper
import com.union.union_basic.ext.cssdp
import com.union.union_basic.ext.dp
import com.union.union_basic.ext.isNotNullOrEmpty
import com.union.union_basic.ext.toConversion
import com.union.union_basic.ext.yes
import com.union.union_basic.logger.LoggerManager

class MyEditTextView : androidx.appcompat.widget.AppCompatEditText, IQMUILayout,CssInterface {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private lateinit var mLayoutHelper: QMUILayoutHelper

    private val mCssParseHelper by lazy {
        CssParseHelper()
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.let { mCssParseHelper.init(context, it,this) }
        mLayoutHelper = QMUILayoutHelper(context, attrs, defStyleAttr, this)
        isFocusableInTouchMode = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec1 = mLayoutHelper.getMeasuredWidthSpec(widthMeasureSpec)
        var heightMeasureSpec1 = mLayoutHelper.getMeasuredHeightSpec(heightMeasureSpec)
        super.onMeasure(widthMeasureSpec1, heightMeasureSpec1)
        val minW = mLayoutHelper.handleMiniWidth(widthMeasureSpec1, measuredWidth)
        val minH = mLayoutHelper.handleMiniHeight(heightMeasureSpec1, measuredHeight)
        if (widthMeasureSpec1 != minW || heightMeasureSpec1 != minH) {
            super.onMeasure(minW, minH)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let { mCssParseHelper.drwableShadow(it) }
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        mLayoutHelper.drawDividers(canvas, width, height)
        mLayoutHelper.dispatchRoundBorderDraw(canvas)
        canvas?.let { mCssParseHelper.dispatchDraw(it) }
    }

    override fun setWidthLimit(widthLimit: Int): Boolean {
        if (mLayoutHelper.setWidthLimit(widthLimit)) {
            requestLayout()
            invalidate()
        }
        return true
    }

    override fun setHeightLimit(heightLimit: Int): Boolean {
        if (mLayoutHelper.setHeightLimit(heightLimit)) {
            requestLayout()
            invalidate()
        }
        return true
    }

    override fun setUseThemeGeneralShadowElevation() {
        mLayoutHelper.setUseThemeGeneralShadowElevation()
    }

    override fun setOutlineExcludePadding(outlineExcludePadding: Boolean) {
        mLayoutHelper.setOutlineExcludePadding(outlineExcludePadding)
    }

    override fun setShadowElevation(elevation: Int) {
        mLayoutHelper.shadowElevation = elevation
    }

    override fun getShadowElevation(): Int {
        return mLayoutHelper.shadowElevation
    }

    override fun setShadowAlpha(shadowAlpha: Float) {
        mLayoutHelper.shadowAlpha = shadowAlpha
    }

    override fun getShadowAlpha(): Float {
        return mLayoutHelper.shadowAlpha
    }

    override fun setShadowColor(shadowColor: Int) {
        mLayoutHelper.shadowColor = shadowColor
    }

    override fun setRadius(radius: Int) {
        mLayoutHelper.radius = radius
    }

    override fun setRadius(radius: Int, hideRadiusSide: Int) {
        mLayoutHelper.setRadius(radius, hideRadiusSide)
    }

    override fun getRadius(): Int {
        return mLayoutHelper.radius
    }

    override fun setOutlineInset(left: Int, top: Int, right: Int, bottom: Int) {
        mLayoutHelper.setOutlineInset(left, top, right, bottom)
    }

    override fun setShowBorderOnlyBeforeL(showBorderOnlyBeforeL: Boolean) {
        mLayoutHelper.setShowBorderOnlyBeforeL(showBorderOnlyBeforeL)
        invalidate()
    }

    override fun setHideRadiusSide(hideRadiusSide: Int) {
        mLayoutHelper.hideRadiusSide = hideRadiusSide
    }

    override fun getHideRadiusSide(): Int {
        return mLayoutHelper.hideRadiusSide
    }

    override fun setRadiusAndShadow(radius: Int, shadowElevation: Int, shadowAlpha: Float) {
        mLayoutHelper.setRadiusAndShadow(radius, shadowElevation, shadowAlpha)
    }

    override fun setRadiusAndShadow(
        radius: Int,
        hideRadiusSide: Int,
        shadowElevation: Int,
        shadowAlpha: Float
    ) {
        mLayoutHelper.setRadiusAndShadow(radius, hideRadiusSide, shadowElevation, shadowAlpha)
    }

    override fun setRadiusAndShadow(
        radius: Int,
        hideRadiusSide: Int,
        shadowElevation: Int,
        shadowColor: Int,
        shadowAlpha: Float
    ) {
        mLayoutHelper.setRadiusAndShadow(
            radius,
            hideRadiusSide,
            shadowElevation,
            shadowColor,
            shadowAlpha
        )
    }

    override fun setBorderColor(borderColor: Int) {
        mLayoutHelper.setBorderColor(borderColor)
        invalidate()
    }

    override fun setBorderWidth(borderWidth: Int) {
        mLayoutHelper.setBorderWidth(borderWidth)
        invalidate()
    }

    override fun updateTopDivider(
        topInsetLeft: Int,
        topInsetRight: Int,
        topDividerHeight: Int,
        topDividerColor: Int
    ) {
        mLayoutHelper.updateTopDivider(
            topInsetLeft,
            topInsetRight,
            topDividerHeight,
            topDividerColor
        )
        invalidate()
    }

    override fun updateBottomDivider(
        bottomInsetLeft: Int,
        bottomInsetRight: Int,
        bottomDividerHeight: Int,
        bottomDividerColor: Int
    ) {
        mLayoutHelper.updateBottomDivider(
            bottomInsetLeft,
            bottomInsetRight,
            bottomDividerHeight,
            bottomDividerColor
        )
        invalidate()
    }

    override fun updateLeftDivider(
        leftInsetTop: Int,
        leftInsetBottom: Int,
        leftDividerWidth: Int,
        leftDividerColor: Int
    ) {
        mLayoutHelper.updateLeftDivider(
            leftInsetTop,
            leftInsetBottom,
            leftDividerWidth,
            leftDividerColor
        )
        invalidate()
    }

    override fun updateRightDivider(
        rightInsetTop: Int,
        rightInsetBottom: Int,
        rightDividerWidth: Int,
        rightDividerColor: Int
    ) {
        mLayoutHelper.updateRightDivider(
            rightInsetTop,
            rightInsetBottom,
            rightDividerWidth,
            rightDividerColor
        )
        invalidate()
    }

    override fun onlyShowTopDivider(
        topInsetLeft: Int,
        topInsetRight: Int,
        topDividerHeight: Int,
        topDividerColor: Int
    ) {
        mLayoutHelper.onlyShowTopDivider(
            topInsetLeft,
            topInsetRight,
            topDividerHeight,
            topDividerColor
        )
        invalidate()
    }

    override fun onlyShowBottomDivider(
        bottomInsetLeft: Int,
        bottomInsetRight: Int,
        bottomDividerHeight: Int,
        bottomDividerColor: Int
    ) {
        mLayoutHelper.onlyShowBottomDivider(
            bottomInsetLeft,
            bottomInsetRight,
            bottomDividerHeight,
            bottomDividerColor
        )
        invalidate()
    }

    override fun onlyShowLeftDivider(
        leftInsetTop: Int,
        leftInsetBottom: Int,
        leftDividerWidth: Int,
        leftDividerColor: Int
    ) {
        mLayoutHelper.onlyShowLeftDivider(
            leftInsetTop,
            leftInsetBottom,
            leftDividerWidth,
            leftDividerColor
        )
        invalidate()
    }

    override fun onlyShowRightDivider(
        rightInsetTop: Int,
        rightInsetBottom: Int,
        rightDividerWidth: Int,
        rightDividerColor: Int
    ) {
        mLayoutHelper.onlyShowRightDivider(
            rightInsetTop,
            rightInsetBottom,
            rightDividerWidth,
            rightDividerColor
        )
        invalidate()
    }

    override fun setTopDividerAlpha(dividerAlpha: Int) {
        mLayoutHelper.setTopDividerAlpha(dividerAlpha)
        invalidate()
    }

    override fun setBottomDividerAlpha(dividerAlpha: Int) {
        mLayoutHelper.setBottomDividerAlpha(dividerAlpha)
        invalidate()
    }

    override fun setLeftDividerAlpha(dividerAlpha: Int) {
        mLayoutHelper.setLeftDividerAlpha(dividerAlpha)
        invalidate()
    }

    override fun setRightDividerAlpha(dividerAlpha: Int) {
        mLayoutHelper.setRightDividerAlpha(dividerAlpha)
        invalidate()
    }

    override fun setOuterNormalColor(color: Int) {
        mLayoutHelper.setOuterNormalColor(color)
    }

    override fun updateLeftSeparatorColor(color: Int) {
        mLayoutHelper.updateLeftSeparatorColor(color)
    }

    override fun updateRightSeparatorColor(color: Int) {
        mLayoutHelper.updateRightSeparatorColor(color)
    }

    override fun updateTopSeparatorColor(color: Int) {
        mLayoutHelper.updateTopSeparatorColor(color)
    }

    override fun updateBottomSeparatorColor(color: Int) {
        mLayoutHelper.updateBottomSeparatorColor(color)
    }

    override fun hasTopSeparator(): Boolean {
        return mLayoutHelper.hasTopSeparator()
    }

    override fun hasRightSeparator(): Boolean {
        return mLayoutHelper.hasRightSeparator()
    }

    override fun hasLeftSeparator(): Boolean {
        return mLayoutHelper.hasLeftSeparator()
    }

    override fun hasBottomSeparator(): Boolean {
        return mLayoutHelper.hasBottomSeparator()
    }

    override fun hasBorder(): Boolean {
        return mLayoutHelper.hasBorder()
    }
    override fun getPostion()=mCssParseHelper.mCssPosition

}