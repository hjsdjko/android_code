package com.design.appproject.ui.storeup

import com.alibaba.android.arouter.launcher.ARouter
import android.widget.*
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.design.appproject.ext.load
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.blankj.utilcode.util.ColorUtils
import androidx.core.view.children
import com.design.appproject.R
import com.design.appproject.widget.ListFilterdialog
import android.view.ViewGroup
import android.view.Gravity
import androidx.core.view.setPadding
import androidx.core.view.setMargins
import com.design.appproject.widget.SpacesItemDecoration
import com.union.union_basic.ext.*
import net.lucode.hackware.magicindicator.MagicIndicator
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.BaseBindingFragment
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.StoreupcommonListLayoutBinding
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.logic.viewmodel.storeup.StoreUpListModel
import com.design.appproject.utils.ArouterUtils
import com.design.appproject.utils.Utils
import com.design.appproject.widget.MagicIndexCommonNavigator
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition

/**
 * ${comments}列表页
 */
@Route(path = CommonArouteApi.PATH_FRAGMENT_LIST_STOREUP)
class StoreUpListFragment : BaseBindingFragment<StoreupcommonListLayoutBinding>() {

    @JvmField
    @Autowired
    var mSearch: String = "" /*搜索*/
    @JvmField
    @Autowired
    var menuJump: String = "1" /*列表识别字段*/
    private val mListAdapter by lazy {StoreUpListAdapter()}

    private val mListModel by viewModels<StoreUpListModel>()


    private val params by lazy { /*请求参数*/
        mutableMapOf(
            "page" to "1",
            "limit" to "20",
            "type" to menuJump
        )
    }
    val queryIndex = Pair<String,String>("名称","name")/*查询索引*/

    override fun initEvent() {
        setBarTitle(
            when(menuJump){
                "31"-> "我的竞拍列表"
                "41"->"我的关注列表"
                else->"我的收藏列表"
            })
        binding.apply {
            initSearch()
            srv.setOnRefreshListener {
                loadData(1)
            }
            srv.mRecyclerView.layoutManager = GridLayoutManager(requireActivity(),2)
            srv.mRecyclerView.addItemDecoration(SpacesItemDecoration(15.dp,mListAdapter.headerLayoutCount))
            srv.setAdapter(mListAdapter.apply {
                pageLoadMoreListener {
                    loadData(it)
                }
                setOnItemClickListener { adapter, view, position ->
                    ArouterUtils.startFragment("/ui/fragment/${data[position].tablename}/details", map = mapOf("mId" to data[position].refid))
                }
            })
        }
    }
    private fun StoreupcommonListLayoutBinding.initSearch() {
        searchValueEt.hint = queryIndex.first
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
            params.put("name","%" + binding.searchValueEt.text.toString() + "%" )
        }.otherwise {
            params.remove("name")
        }
        Utils.isLogin().yes {
            mListModel.page("storeup", params)
        }.otherwise {
            mListModel.list("storeup", params)
        }
    }
}