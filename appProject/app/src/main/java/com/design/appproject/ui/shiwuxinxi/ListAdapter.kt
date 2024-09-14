package com.design.appproject.ui.shiwuxinxi
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.ShiwuxinxiItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 失物信息适配器列表
 */
class ListAdapter : LoadMoreAdapter<ShiwuxinxiItemBean>(R.layout.shiwuxinxi_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: ShiwuxinxiItemBean) {
        holder.setText(R.id.wupinmingcheng_tv, item.wupinmingcheng.toString())
        val img = item.tupian.split(",")[0]
        holder.getView<ImageView>(R.id.picture_iv).load(context,img, needPrefix = !img.startsWith("http"))
        holder.setText(R.id.diushididian_tv,"丢失地点:"+ item.diushididian.toString())
        holder.setText(R.id.wupinzhuangtai_tv,"物品状态:"+ item.wupinzhuangtai.toString())
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("shiwuxinxi","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("shiwuxinxi","删除"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("shiwuxinxi","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("shiwuxinxi","删除"))
        }
    }
}