package com.design.appproject.ui.shiquxinxi
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.ShiquxinxiItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 拾取信息适配器列表
 */
class ListAdapter : LoadMoreAdapter<ShiquxinxiItemBean>(R.layout.shiquxinxi_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: ShiquxinxiItemBean) {
        holder.setText(R.id.wupinmingcheng_tv,"物品名称:"+ item.wupinmingcheng.toString())
        val img = item.tupian.split(",")[0]
        holder.getView<ImageView>(R.id.picture_iv).load(context,img, needPrefix = !img.startsWith("http"))
        holder.setText(R.id.wupinzhuangtai_tv,"物品状态:"+ item.wupinzhuangtai.toString())
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("shiquxinxi","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("shiquxinxi","删除"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("shiquxinxi","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("shiquxinxi","删除"))
        }
    }
}