package com.design.appproject.widget

import android.content.Context
import android.view.Gravity
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import com.union.union_basic.ext.dp
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule
/**
 * classname：MagicIndexCommonNavigator
 * desc: 通用CommonNavigator
 */
class MagicIndexCommonNavigator(context: Context, list: List<String> = listOf(),
    block: ((index: Int) -> Unit)? = null) : CommonNavigator(context) {

    var mTitleList = list
    var mRedList = listOf<Int>() //是否要展示红点的列表
    var mBlock = block
    var mLineHight = 2.5f.dp
    var mLineWidth = 30f.dp
    var mYOffset = 0f.dp
    var mNormalSize = 18f //正常字体大小
    var mSelectedSize = 0f //选中字体大小
    var padding = 10.dp
    var mRoundRadius = -1f
    var mNormalBg = 0
    var mMargin = 0
    var mSelectedBg = 0
    var mIsSingleLine = true //是否单行
    var mIsBold = false
    var mSelectColorRes = R.color.common_colorPrimary
    var mNormalColorRes = R.color.common_title_color
    var mIsAdjustMode = false
    var mImageRes = 0 //有值代表是图片指示器
    var mLineColor = R.color.common_colorPrimary

    override fun onAttachToMagicIndicator() {
        super.onAttachToMagicIndicator()
        titleContainer.gravity = Gravity.CENTER
        isAdjustMode = mIsAdjustMode
        scrollPivotX = 0.9f
        adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return mTitleList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val badgePagerTitleView = BadgePagerTitleView(context)
                val colorTransitionPagerTitleView = IndexPagerTitleView(context)
                colorTransitionPagerTitleView.mNormalColorRes = this@MagicIndexCommonNavigator.mNormalColorRes
                colorTransitionPagerTitleView.mSelectColorRes = this@MagicIndexCommonNavigator.mSelectColorRes
                colorTransitionPagerTitleView.isSingleLine = mIsSingleLine
                colorTransitionPagerTitleView.isBold = mIsBold
                colorTransitionPagerTitleView.setPadding(padding, 0, padding, 0)
                colorTransitionPagerTitleView.mSelectedSize = mSelectedSize
                colorTransitionPagerTitleView.mNormalSize = mNormalSize
                colorTransitionPagerTitleView.mSelectedBg = mSelectedBg
                colorTransitionPagerTitleView.mNormalBg = mNormalBg
                colorTransitionPagerTitleView.setOnClickListener {
                    mBlock?.invoke(index)
                }
                if (!mRedList.isNullOrEmpty() && mRedList[index] > 0) {
                    badgePagerTitleView.badgeView = ImageFilterView(context).apply {
                        round = 3f.dp
                        minimumHeight = 6.dp
                        minimumWidth = 6.dp
                        setBackgroundResource(R.color.common_red)
                    }
                    badgePagerTitleView.xBadgeRule = BadgeRule(BadgeAnchor.CONTENT_RIGHT, 0)
                    badgePagerTitleView.yBadgeRule = BadgeRule(BadgeAnchor.CONTENT_TOP, 0)
                }
                badgePagerTitleView.innerPagerTitleView = colorTransitionPagerTitleView
                colorTransitionPagerTitleView.text = mTitleList[index]
                return badgePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                var indicator: IPagerIndicator?
                if (mImageRes > 0) {
                    indicator = ImageIndicator(context)
                    indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                    indicator.yOffset = mYOffset
                    indicator.setImageRes(mImageRes)
                    indicator.setColors(ColorUtils.getColor(mLineColor))
                } else {
                    indicator = LinePagerIndicator(context)
                    indicator.lineHeight = mLineHight
                    if (mLineWidth > 0) {
                        indicator.lineWidth = mLineWidth
                        indicator.mode = LinePagerIndicator.MODE_EXACTLY
                    } else {
                        indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                    }
                    indicator.yOffset = mYOffset
                    indicator.roundRadius = if (mRoundRadius >= 0) mRoundRadius else mLineHight / 2
                    indicator.setColors(ColorUtils.getColor(mLineColor))
                }
                return indicator
            }
        }
    }

}