package com.design.appproject.widget

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.union.union_basic.ext.toConversion

class MyFlexboxLayoutManager(context: Context?) : FlexboxLayoutManager(context) {

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams {
        return when (lp) {
            is RecyclerView.LayoutParams -> {
                FlexboxLayoutManager.LayoutParams(lp.toConversion<RecyclerView.LayoutParams>())
            }

            is ViewGroup.MarginLayoutParams -> {
                FlexboxLayoutManager.LayoutParams(lp.toConversion<ViewGroup.MarginLayoutParams>())
            }
            else -> {
                return FlexboxLayoutManager.LayoutParams(lp)
            }
        }
    }
}