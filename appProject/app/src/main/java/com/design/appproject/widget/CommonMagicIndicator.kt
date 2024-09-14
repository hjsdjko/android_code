package com.design.appproject.widget

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager2.widget.ViewPager2
import com.design.appproject.ext.bind
import net.lucode.hackware.magicindicator.MagicIndicator

/**
 * classname：CommonMagicIndicator
 * desc: 通用Indicator
 */
class CommonMagicIndicator : MagicIndicator {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    private var mCustomNavigator: MagicIndexCommonNavigator? = null

    fun init(viewpager2: ViewPager2, list: List<String>, customNavigator: MagicIndexCommonNavigator? = null,
        onPageSelectedCall: ((position: Int) -> Unit)? = null) {
        mCustomNavigator = customNavigator
        navigator = creatNavigator(viewpager2, list, customNavigator)
        bind(viewpager2, onPageSelectedCall)
    }

    private fun creatNavigator(viewpager2: ViewPager2, list: List<String>,
        customNavigator: MagicIndexCommonNavigator? = null) =
        (customNavigator ?: MagicIndexCommonNavigator(context)).apply {
            mTitleList = list
            mBlock = { index ->
                viewpager2.currentItem = index
            }
        }
}