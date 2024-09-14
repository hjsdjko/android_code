package com.design.appproject.ui.storeup
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.StoreupItemBean
import com.design.appproject.ext.load
import com.design.appproject.widget.LoadMoreAdapter

/**
 * ${comments}适配器列表
 */
class StoreUpListAdapter : LoadMoreAdapter<StoreupItemBean>(R.layout.storeup_list_item_layout) {

    override fun convert(holder: BaseViewHolder, item: StoreupItemBean) {
        holder.getView<ImageView>(R.id.picture_iv).load(context,item.picture)
        holder.setText(R.id.title_tv,item.name)
    }
}