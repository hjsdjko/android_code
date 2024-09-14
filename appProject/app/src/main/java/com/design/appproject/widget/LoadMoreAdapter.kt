package com.design.appproject.widget

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
/***
 * 类名：LoadMoreAdapter
 * 文件描述：有翻页时的adapter
 */
abstract class LoadMoreAdapter<T>(layoutResId: Int, data: MutableList<T>? = null) :
    BaseQuickAdapter<T, BaseViewHolder>(layoutResId, data), LoadMoreModule {

    var mLoadPager = 1 //需要加载的页数

    var setEmptyView = true //是否要显示空视图

    //有翻页加载时的设置数据方法
    fun setListPageData(list: List<T>?, total: Int, isReload: Boolean = false) {
        if (isReload) {
            mLoadPager = 1
        }
        if (!data.isNullOrEmpty() && !list.isNullOrEmpty() && data[0].toString() == list[0].toString()) { //判断新数据的第一条数据是否跟原有数据的第一条数据相同，是则判断为重新加载，需要重设数据
            mLoadPager = 1
        }
        if (mLoadPager == 1) {
            setList(list)
            if (list.isNullOrEmpty() && setEmptyView) { //设置空视图
                headerWithEmptyEnable = true
                mEmptyView.isVisible = true
                setEmptyView(mEmptyView)
            } else {
                removeEmptyView()
            }
        } else {
            list?.let { addData(it) }
            loadMoreModule.loadMoreComplete()
        }
        if (list?.isEmpty() == true || total <= data.size) {
            loadMoreModule.loadMoreEnd(true)
        } else {
            mLoadPager++
        }
    }

    private val mEmptyView by lazy {
        TextView(context).apply {
            text = "没有数据"
            gravity = Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    fun addListEmptyView(layoutResId: Int) {
        recyclerView?.let {
            val view = LayoutInflater.from(context).inflate(layoutResId, it, false)
            addFooterView(view)
        }
    }

    //加载下一页回调
    fun pageLoadMoreListener(loadMoreListener: (nextPage: Int) -> Unit) {
        loadMoreModule.setOnLoadMoreListener {
            loadMoreListener(mLoadPager)
        }
    }

    override fun addData(@NonNull newData: Collection<T>) {
        this.data.addAll(newData)
        notifyDataSetChanged()
        compatibilityDataSizeChanged(newData.size)
    }
}