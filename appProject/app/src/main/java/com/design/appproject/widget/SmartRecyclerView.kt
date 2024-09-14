package com.design.appproject.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.union.union_basic.ext.toConversion
import java.lang.Math.abs

/**
 * classname：SmartRecyclerView
 * desc:封装后的recyclerview
 */
class SmartRecyclerView<T> : SwipeRefreshLayout {

    val LIST_PAGE_SIZE = 20 //列表默认一页加载条数

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(resid)
    }

    val mRecyclerView by lazy {
        RecyclerView(context)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        addView(mRecyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mRecyclerView.layoutManager = LinearLayoutManager(context) //默认竖布局
    }

    fun setAdapter(adpter: LoadMoreAdapter<T>) {
        mRecyclerView.adapter = adpter
    }

    fun getAdapter() = mRecyclerView.adapter as LoadMoreAdapter<T>

    fun setPageIndex(page: Int) {
        mRecyclerView.adapter?.toConversion<LoadMoreAdapter<T>>()?.mLoadPager = page
    }

    var mReload = false

    fun setData(list: List<T>?, total: Int, isReLoad: Boolean = false) {
        (mRecyclerView.adapter as? LoadMoreAdapter<T>)?.let {
            it.setListPageData(list, total, isRefreshing || isReLoad || mReload)
        }
        mReload = false
        if (isRefreshing) {
            isRefreshing = false
        }
    }

    //    解决嵌套viewpager2太灵敏问题
    private var disallowIntercept = false
    private var startX = 0
    private var startY = 0
    var isDispatch: Boolean = true

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (isDispatch) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = ev.x.toInt()
                    startY = ev.y.toInt()
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    val endX = ev.x.toInt()
                    val endY = ev.y.toInt()
                    val disX = abs(endX - startX)
                    val disY = abs(endY - startY)
                    if (disX > disY) { //为了解决RecyclerView嵌套RecyclerView时横向滑动的问题
                        if (disallowIntercept) {
                            parent.requestDisallowInterceptTouchEvent(disallowIntercept)
                        } else {
                            parent.requestDisallowInterceptTouchEvent(canScrollHorizontally(startX - endX))
                        }
                    } else {
                        parent.requestDisallowInterceptTouchEvent(true)
                    } //LogUtils.d("是否滑动到顶部：${canChildScrollUp()}:disY:${endY - startY}")
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    disallowIntercept = false
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    fun reload() {
        isRefreshing = true
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        this.disallowIntercept = disallowIntercept //        LogUtils.d("disallowIntercept:$disallowIntercept")
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }
}