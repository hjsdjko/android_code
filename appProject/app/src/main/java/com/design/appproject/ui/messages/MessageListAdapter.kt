package com.design.appproject.ui.messages

import android.text.Html
import android.widget.ImageView
import androidx.core.view.isVisible
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.messages.MessagesItemBean
import com.design.appproject.ext.load
import com.design.appproject.widget.LoadMoreAdapter
import com.union.union_basic.ext.isNotNullOrEmpty

/**
 * 留言板列表适配器
 */
class MessageListAdapter: LoadMoreAdapter<MessagesItemBean>(R.layout.item_messages_list_layout) {

    override fun convert(holder: BaseViewHolder, item: MessagesItemBean) {
        holder.getView<ImageView>(R.id.avatar_qriv).load(context,item.avatarurl)
        holder.setText(R.id.username_tv,item.username)
        holder.setText(R.id.content_tv,Html.fromHtml(item.content))
        holder.setText(R.id.time_tv,item.addtime)
        holder.setText(R.id.reply_tv,Html.fromHtml(item.reply))
        holder.setGone(R.id.reply_tv,item.reply.isNullOrEmpty())
        holder.getView<ImageView>(R.id.content_qriv)?.let {
            it.load(context,item.cpicture)
            it.isVisible = item.cpicture.isNotNullOrEmpty()
        }
        holder.getView<ImageView>(R.id.reply_qriv)?.let {
            it.load(context,item.rpicture)
            it.isVisible = item.rpicture.isNotNullOrEmpty()
        }
    }
}