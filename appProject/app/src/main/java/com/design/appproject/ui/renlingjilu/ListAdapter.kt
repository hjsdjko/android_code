package com.design.appproject.ui.renlingjilu
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.RenlingjiluItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 认领记录适配器列表
 */
class ListAdapter : LoadMoreAdapter<RenlingjiluItemBean>(R.layout.renlingjilu_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: RenlingjiluItemBean) {
        holder.setText(R.id.wupinmingcheng_tv,"物品名称:"+ item.wupinmingcheng.toString())
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("renlingjilu","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("renlingjilu","删除"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("renlingjilu","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("renlingjilu","删除"))
        }
    }
}