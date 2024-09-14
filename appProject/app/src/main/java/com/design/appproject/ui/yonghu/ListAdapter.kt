package com.design.appproject.ui.yonghu
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.YonghuItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 用户适配器列表
 */
class ListAdapter : LoadMoreAdapter<YonghuItemBean>(R.layout.yonghu_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: YonghuItemBean) {
        val img = item.touxiang.split(",")[0]
        holder.getView<ImageView>(R.id.picture_iv).load(context,img, needPrefix = !img.startsWith("http"))
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("yonghu","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("yonghu","删除"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("yonghu","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("yonghu","删除"))
        }
    }
}