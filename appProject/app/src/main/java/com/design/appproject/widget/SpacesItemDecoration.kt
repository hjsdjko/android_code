package com.design.appproject.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.union.union_basic.ext.toConversion

/**
 * classname：
 * author：ZWQ
 * date: 2021/12/23 13:11
 * desc:
 */
class SpacesItemDecoration(val space: Int,val headerCount:Int=0) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildLayoutPosition(view)>=headerCount){
            parent.layoutManager?.toConversion<GridLayoutManager>()?.let {
                outRect.top = space / 2
                outRect.bottom = space / 2
                //outRect.right = if (parent.getChildAdapterPosition(view) % it.spanCount == it.spanCount-1) 0 else space / 2
                //outRect.left = if (parent.getChildAdapterPosition(view) % it.spanCount == 0) 0 else space
                outRect.right = space / 2
                outRect.left = space / 2
            }
        }
    }
}
