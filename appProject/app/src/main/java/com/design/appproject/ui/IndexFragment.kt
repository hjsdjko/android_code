package com.design.appproject.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import com.design.appproject.base.BaseBindingFragment
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.base.CommonBean
import com.design.appproject.bean.*
import com.design.appproject.bean.config.*
import com.design.appproject.bean.news.*
import com.design.appproject.databinding.FragmentIndexLayoutBinding
import com.design.appproject.ext.load
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.logic.viewmodel.HomeViewModel
import com.design.appproject.utils.ArouterUtils
import com.design.appproject.utils.Utils
import com.design.appproject.widget.BottomSpinner
import com.youth.banner.indicator.CircleIndicator
import android.text.Html
import androidx.core.view.isVisible
import android.view.LayoutInflater
import com.design.appproject.widget.MyFlexBoxLayout
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.union.union_basic.ext.*
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.Banner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.graphics.Color
import com.design.appproject.base.EventBus
import com.design.appproject.ext.observeEvent
import com.lxj.xpopup.XPopup
/**
 * 首页fragment
 * */
@Route(path = CommonArouteApi.PATH_FRAGMENT_INDEX)
class IndexFragment : BaseBindingFragment<FragmentIndexLayoutBinding>() {

    private val mHomeViewModel by viewModels<HomeViewModel>()

    override fun initEvent() {
        binding.apply {
            homeSrl.setOnRefreshListener {
                initHomeView()
                GlobalScope.launch {
                    delay(2000) /*延时2秒*/
                    homeSrl.isRefreshing =false
                }
            }
            initHomeView()
            observeEvent<Boolean>(EventBus.LOGIN_SUCCESS){
                initHomeView()
            }
        }
    }

    private fun FragmentIndexLayoutBinding.initHomeView(){/*初始化首页内容*/
        initBanner()
        initMenu()
        initNews()
        initRecommendView()
        initProductView()
    }

    private fun FragmentIndexLayoutBinding.initProductView() {
        shiwuxinxiMoreTv.setOnClickListener {/*查看更多*/
            ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_LIST_SHIWUXINXI)
        }
        HomeRepository.list<ShiwuxinxiItemBean>("shiwuxinxi",
            mapOf(
                "page" to "1",
                "limit" to "6"
            )
        ).observeKt {
                it.getOrNull()?.let {
                    shiwuxinxiListLl.removeAllViews()
                        it.data.list.forEachIndexed { index,data->
                            val itemView =creatshiwuxinxiItemView(data,shiwuxinxiListLl)
                            shiwuxinxiListLl.addView(itemView)
                            itemView.setOnClickListener {
                                ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_SHIWUXINXI,map = mapOf("mId" to data.id))
                            }
                        }
            }
        }
    }

    private fun creatshiwuxinxiItemView(data:ShiwuxinxiItemBean,parent: ViewGroup) =LayoutInflater.from(context).inflate(R.layout.shiwuxinxi_item_index_list_layout,parent,false).apply{
        findViewById<TextView>(R.id.wupinmingcheng_title_tv).text =  data.wupinmingcheng.toString()
        findViewById<ImageView>(R.id.tupian_iv).load(context,data.tupian.split(",")[0], needPrefix = !(data.tupian.startsWith("http")))
        findViewById<TextView>(R.id.diushididian_title_tv).text = "丢失地点:"+ data.diushididian.toString()
        findViewById<TextView>(R.id.wupinzhuangtai_title_tv).text = "物品状态:"+ data.wupinzhuangtai.toString()
    }
    private fun FragmentIndexLayoutBinding.initRecommendView() { /*商品推荐初始化*/
        val map = mapOf("page" to "1", "limit" to "4")
    if (Utils.isLogin()) {
        HomeRepository.autoSort2<ShiquxinxiItemBean>("shiquxinxi", map).observeKt {
            it.getOrNull()?.let {
                shiquxinxiGl.removeAllViews()
                it.data.list.forEachIndexed { index,data->
                    val itemView =creatshiquxinxiRecommendItemView(data,shiquxinxiGl)
                    shiquxinxiGl.addView(itemView)
                    itemView.setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_SHIQUXINXI,map = mapOf("mId" to data.id))
                    }
                }
            }
        }
    } else {
        HomeRepository.autoSort<ShiquxinxiItemBean>("shiquxinxi", map).observeKt {
            it.getOrNull()?.let {
                shiquxinxiGl.removeAllViews()
                var recommendList = it.data.list
                recommendList.forEachIndexed { index,data->
                    val itemView =creatshiquxinxiRecommendItemView(data,shiquxinxiGl)
                    shiquxinxiGl.addView(itemView)
                    itemView.setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_SHIQUXINXI,map = mapOf("mId" to data.id))
                    }
                }
            }
        }
    }
}

    private fun creatshiquxinxiRecommendItemView(data:ShiquxinxiItemBean,parent: ViewGroup) =LayoutInflater.from(context).inflate(R.layout.shiquxinxi_item_recommend_layout,parent,false).apply{
        findViewById<TextView>(R.id.wupinmingcheng_title_tv).text = "物品名称:"+ data.wupinmingcheng.toString()
        findViewById<ImageView>(R.id.tupian_iv).load(context,data.tupian.split(",")[0], needPrefix = !(data.tupian.startsWith("http")))
        findViewById<TextView>(R.id.wupinzhuangtai_title_tv).text = "物品状态:"+ data.wupinzhuangtai.toString()
    }
    private fun FragmentIndexLayoutBinding.initNews() {/*新闻咨询*/
        newsMoreTv.setOnClickListener {/*查看更多*/
            ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_LIST_NEWS)
        }
        mHomeViewModel.newsList(mapOf("page" to "1", "limit" to "6"))
        mHomeViewModel.newsListLiveData.observeKt {
            it.getOrNull()?.let {
                newsLl.removeAllViews()
                it.data.list.forEachIndexed { index, newsListBean ->
                    newsLl.addView(creatNewItemView(newsListBean))
                }
            }
        }
    }

    private fun creatNewItemView(newsListBean: NewsItemBean) =LayoutInflater.from(context).inflate(R.layout.item_news_layout,binding.newsLl,false).apply{
        findViewById<ImageView>(R.id.news_iv).load(context, newsListBean.picture)
        findViewById<TextView>(R.id.news_title_tv).text = newsListBean.title
        findViewById<TextView>(R.id.news_time_tv).text = "发布时间："+newsListBean.addtime?.split(" ")?.get(0)?:""
        findViewById<TextView>(R.id.news_desc_tv).text = newsListBean.introduction
        setOnClickListener {
            ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_NEWS, map = mapOf("mId" to newsListBean.id))
        }
    }
    /**轮播图*/
    private fun FragmentIndexLayoutBinding.initBanner() {
        HomeRepository.list<ConfigItemBean>("config", mapOf("page" to "1", "limit" to "3")).observeKt{
            it.getOrNull()?.let {
                banner.setAdapter(object :
                    BannerImageAdapter<ConfigItemBean>(it.data.list.filter { it.name.contains("swiper") }) {
                    override fun onBindView(
                        holder: BannerImageHolder,
                        data: ConfigItemBean,
                        position: Int,
                        size: Int
                    ) {
                        activity?.let { holder.imageView.load(it, data.value.split(",")[0], radius = 5.dp) }
                    }
                }).setOnBannerListener { data, position ->
                    data.toConversion<ConfigItemBean>()?.let {
                        it.name.showToast()
                    }
                }
            }
        }
    }
    /**菜单*/
    private fun FragmentIndexLayoutBinding.initMenu() {
        val menuList = mutableListOf<MenuBean>()
        menuGl.removeAllViews()
            roleMenusList.filter { it.tableName == CommonBean.tableName }.forEach {/*筛选可查看的菜单*/
                it.frontMenu.forEach {
                    val menuBean = MenuBean(
                        child = it.child.filter {child->child.buttons.contains("查看")},
                        menu = it.menu?:"", fontClass = it.fontClass?:"", unicode = it.unicode?:"")
                    menuList.add(menuBean)
                }
            }
        menuList.forEachIndexed { index, menu ->
            if (menu.child.size>0 && !listOf("yifahuodingdan","yituikuandingdan","yiquxiaodingdan","weizhifudingdan","yizhifudingdan","yiwanchengdingdan").contains(menu.child[0].tableName)){
                val itemView = creatMenuItemView(menu)
                menuGl.addView(itemView)
            }
        }
    }

    private fun creatMenuItemView(menu: MenuBean) = LayoutInflater.from(context).inflate(R.layout.item_index_menu_layout,binding.menuGl,false).apply{
        findViewById<TextView>(R.id.menu_title_tv).text = menu.menu.split("列表")[0]
        findViewById<TextView>(R.id.menu_icon_tv).text = Html.fromHtml(menu.unicode?:"")
        setOnClickListener {
            if (menu.child.size>1){//二级菜单
                XPopup.Builder(requireActivity()).asBottomList("",
                    menu.child.map {it.menu}.toTypedArray()
                ) { position, text ->
                    ArouterUtils.startFragment("/ui/fragment/${menu.child[position].tableName}/list")
                }.show()
            }else{// 一级菜单
                ArouterUtils.startFragment("/ui/fragment/${menu.child[0].tableName}/list")
            }
        }
    }

}