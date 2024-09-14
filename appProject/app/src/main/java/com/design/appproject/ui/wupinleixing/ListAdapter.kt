package com.design.appproject.ui.wupinleixing
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.WupinleixingItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 物品类型适配器列表
 */
class ListAdapter : LoadMoreAdapter<WupinleixingItemBean>(R.layout.wupinleixing_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: WupinleixingItemBean) {
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("wupinleixing","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("wupinleixing","删除"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("wupinleixing","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("wupinleixing","删除"))
        }
    }
}