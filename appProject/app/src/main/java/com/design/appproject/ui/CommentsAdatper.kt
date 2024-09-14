package com.design.appproject.ui

import androidx.constraintlayout.utils.widget.ImageFilterView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.CommentsBean
import com.design.appproject.ext.load
import androidx.core.view.isVisible
import android.widget.TextView
import com.union.union_basic.ext.isNotNullOrEmpty
import com.design.appproject.widget.LoadMoreAdapter

/**
 * 评论适配器
 */
class CommentsAdatper: LoadMoreAdapter<CommentsBean>(R.layout.item_comment_layout) {

    override fun convert(holder: BaseViewHolder, item: CommentsBean) {
        holder.getView<ImageFilterView>(R.id.avatar_ifv).load(context,item.avatarurl)
        holder.setText(R.id.name_tv,item.nickname)
        holder.setText(R.id.content_tv,item.content)
        holder.setText(R.id.time_tv,item.addtime)
        holder.getView<TextView>(R.id.reply_tv).let {
            it.text = "回复："+item.reply
            it.isVisible = item.reply.isNotNullOrEmpty()
        }
    }
}