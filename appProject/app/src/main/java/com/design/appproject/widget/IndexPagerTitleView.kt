package com.design.appproject.widget

import android.content.Context
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

/**
 * classname：IndexPagerTitleView
 * desc: 自定义pagertitleview
 */
class IndexPagerTitleView : ColorTransitionPagerTitleView {


    constructor(context: Context) : super(context)

    var mNormalBg: Int = 0
    var mSelectedBg: Int = 0
    var isBold: Boolean = false //是否选中时加粗
    var mSelectedSize = 0f //选中时字体大小
    var mNormalSize = 0f //正常字体大小

    var mNormalColorRes = R.color.common_title_color //正常字体颜色
        set(value) {
            field = value
            normalColor = ColorUtils.getColor(mNormalColorRes)
        }
    var mSelectColorRes = R.color.common_colorPrimary //选中字体颜色
        set(value) {
            field = value
            selectedColor = ColorUtils.getColor(mSelectColorRes)
        }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        super.onLeave(index, totalCount, leavePercent, leftToRight)
        paint.isFakeBoldText = false
        textSize = mNormalSize
        if (mNormalBg > 0) {
            setBackgroundResource(mNormalBg)
        }
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        super.onEnter(index, totalCount, enterPercent, leftToRight)
        paint.isFakeBoldText = isBold
        textSize = if (mSelectedSize != 0f) mSelectedSize else mNormalSize
        if (mSelectedBg > 0) {
            setBackgroundResource(mSelectedBg)
        }
    }
}