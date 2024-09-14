package com.design.appproject.ui.news

import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.design.appproject.R
import com.design.appproject.base.BaseBindingFragment
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.NewsListItemLayoutBinding
import com.design.appproject.databinding.NewsListLayoutBinding
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.logic.viewmodel.news.NewsListModel
import com.design.appproject.utils.ArouterUtils
import com.design.appproject.utils.Utils
import com.design.appproject.widget.ListFilterdialog
import com.design.appproject.widget.MagicIndexCommonNavigator
import com.design.appproject.widget.SpacesItemDecoration
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.union.union_basic.ext.*
import net.lucode.hackware.magicindicator.MagicIndicator

@Route(path = CommonArouteApi.PATH_FRAGMENT_LIST_NEWS)
class NewsListFragment:BaseBindingFragment<NewsListLayoutBinding>() {

    val queryIndex = Pair<String,String>("标题","title")/*查询索引*/

    @JvmField
    @Autowired
    var mSearch: String = "" /*搜索*/

    @JvmField
    @Autowired
    var mHasBack: Boolean = true /*是否有返回按钮*/

    private val mListAdapter by lazy {NewsListAdapter()}

    private val mListModel by viewModels<NewsListModel>()

    private val params by lazy { /*请求参数*/
        mutableMapOf(
            "page" to "1",
            "limit" to "20",
        )
    }


    override fun initEvent() {
        setBarTitle("公告资讯",mHasBack)
        binding.apply {
            initSearch()
            srv.setOnRefreshListener {
                loadData(1)
            }
            srv.mRecyclerView.addItemDecoration(SpacesItemDecoration(15.dp,mListAdapter.headerLayoutCount))
            srv.setAdapter(mListAdapter.apply {
                pageLoadMoreListener {
                    loadData(it)
                }
                setOnItemClickListener { adapter, view, position ->
                    ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_NEWS,
                    mapOf("mId" to data[position].id))
                }
            })
        }
    }

    private fun NewsListLayoutBinding.initSearch() {
        searchValueEt.setText(mSearch)
        searchBtn.setOnClickListener {
            loadData(1)
        }
    }


    override fun initData() {
        super.initData()
        loadData(1)
        mListModel.pageLiveData.observeKt {
            it.getOrNull()?.let {
                binding.srv.setData(it.data.list,it.data.total)
            }
        }

        mListModel.listLiveData.observeKt {
            it.getOrNull()?.let {
                binding.srv.setData(it.data.list,it.data.total)
            }
        }
    }

    private fun loadData(page:Int){
        if (page==1){
            binding.srv.reload()
        }
        params.put("page",page.toString())
        binding.searchValueEt.text.toString().isNotNullOrEmpty().yes {
            params.put("title","%" + binding.searchValueEt.text.toString() + "%" )
        }.otherwise {
            params.remove("title")
        }
        Utils.isLogin().yes {
            mListModel.page("news", params)
        }.otherwise {
            mListModel.list("news", params)
        }
    }
}