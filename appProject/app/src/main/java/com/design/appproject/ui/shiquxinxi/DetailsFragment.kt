package com.design.appproject.ui.shiquxinxi

import com.design.appproject.logic.repository.UserRepository
import com.qmuiteam.qmui.widget.QMUIRadiusImageView
import android.annotation.SuppressLint
import com.union.union_basic.utils.StorageUtil
import com.design.appproject.utils.ArouterUtils
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.design.appproject.base.*
import com.design.appproject.ext.postEvent
import android.media.MediaPlayer
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import com.google.gson.Gson
import android.view.Gravity
import android.view.ViewGroup
import com.design.appproject.widget.DetailBannerAdapter
import android.widget.*
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.union.union_basic.ext.*
import androidx.core.view.setMargins
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import kotlinx.coroutines.*
import com.union.union_basic.network.DownloadListener
import com.union.union_basic.network.DownloadUtil
import java.io.File
import androidx.core.view.setPadding
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.utils.Utils
import java.util.*
import kotlin.concurrent.timerTask
import com.design.appproject.ext.load
import com.design.appproject.logic.viewmodel.shiquxinxi.DetailsViewModel
import androidx.activity.viewModels
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.databinding.ShiquxinxicommonDetailsLayoutBinding
import com.design.appproject.databinding.ShiquxinxicommonDetailsHeaderLayoutBinding
import com.dylanc.viewbinding.getBinding
import com.design.appproject.bean.*
import com.design.appproject.ui.CommentsAdatper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.view.isVisible
import android.text.Html
import com.design.appproject.widget.MyTextView
import com.design.appproject.widget.MyFlexBoxLayout
import com.design.appproject.widget.MyImageView
import android.view.ContextThemeWrapper
import com.google.android.flexbox.FlexWrap
import com.union.union_basic.image.loader.GlideLoader.load
/**
 * 拾取信息详情页
 */
@Route(path = CommonArouteApi.PATH_FRAGMENT_DETAILS_SHIQUXINXI)
class DetailsFragment : BaseBindingFragment<ShiquxinxicommonDetailsLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0 /*id*/

    @JvmField
    @Autowired
    var mIsBack: Boolean = false /*是否用户后台进入*/

    private val mDetailsViewModel by viewModels<DetailsViewModel>()

    private var mShiquxinxiItemBean=ShiquxinxiItemBean()/*详情内容*/

    private val mCommentsAdatper by lazy {
        CommentsAdatper().apply {
            pageLoadMoreListener {
                mDetailsViewModel.comments("discussshiquxinxi", mapOf("page" to it.toString(),"limit" to "10","refid" to mId.toString()))
            }
        }
    }

    private val mHeader by lazy {
        LayoutInflater.from(requireActivity()).inflate(R.layout.shiquxinxicommon_details_header_layout, null,false)
    }
    private val mHeaderBinding:ShiquxinxicommonDetailsHeaderLayoutBinding by lazy {
        mHeader.getBinding()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initEvent() {
        setBarTitle("拾取信息详情页")
        setBarColor("#FFFFFF","black")
        binding.apply{
            mCommentsAdatper.addHeaderView(mHeader)
            srv.setAdapter(mCommentsAdatper)
            srv.setOnRefreshListener {
                loadData()
                mDetailsViewModel.comments("discussshiquxinxi", mapOf("page" to "1","limit" to "10","refid" to mId.toString()))
            }
            mHeaderBinding.addCommentBtn.setOnClickListener {
                ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_DISCUSSSHIQUXINXI)
                .withLong("mRefid",mId)
                .navigation(requireActivity(),101)
            }
        val wupinzhuangtaiCrossSelect = "已认领,未认领".split(",")
            mIsBack.yes {
                crossOptButtonBtn0.isVisible = Utils.isAuthBack("shiquxinxi","认领")
            }.otherwise {
                crossOptButtonBtn0.isVisible = Utils.isAuthFront("shiquxinxi","认领")
            }
            crossOptButtonBtn0.setOnClickListener{/*跨表*/
            if (mShiquxinxiItemBean.wupinzhuangtai=="已认领"){
                "已认领".showToast()
                return@setOnClickListener
            }
            ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_RENLINGJILU)
                .withString("mCrossTable","shiquxinxi")
                .withObject("mCrossObj",mShiquxinxiItemBean)
                .withString("mStatusColumnName","wupinzhuangtai")
                .withString("mStatusColumnValue","已认领")
                .withString("mTips","已认领")
                .navigation()
        }
    }
    }

    override fun initData() {
        super.initData()
        showLoading()
        loadData()
        mDetailsViewModel.infoLiveData.observeKt(errorBlock = {binding.srv.isRefreshing =false}) {
            it.getOrNull()?.let { info->
                binding.srv.isRefreshing =false
                mShiquxinxiItemBean = info.data
                mHeaderBinding.setInfo()
            }
        }
        mDetailsViewModel.comments("discussshiquxinxi", mapOf("page" to "1","limit" to "10","refid" to mId.toString()))
        mDetailsViewModel.commentsLiveData.observeKt {
            it.getOrNull()?.let {
                binding.srv.setData(it.data.list,it.data.total)
            }
        }
        mDetailsViewModel.updateLiveData.observeKt {
            it.getOrNull()?.let {
            }

        }
    }

    private fun loadData(){
        mDetailsViewModel.info("shiquxinxi",mId.toString())
    }


    private fun ShiquxinxicommonDetailsHeaderLayoutBinding.setInfo(){
        wupinmingchengTv.text = "${mShiquxinxiItemBean.wupinmingcheng}"
        banner.setAdapter(DetailBannerAdapter(mShiquxinxiItemBean.tupian.split(","))).setOnBannerListener { data, position ->
            data.toConversion<String>()?.let {
                it.showToast()
            }
        }
        wupinleixingTv.text = "${mShiquxinxiItemBean.wupinleixing}"
        shuliangTv.text = "${mShiquxinxiItemBean.shuliang}"
        shiqudidianTv.text = "${mShiquxinxiItemBean.shiqudidian}"
        shiqushijianTv.text = "${mShiquxinxiItemBean.shiqushijian}"
        wupinmiaoshuCw.setHtml(mShiquxinxiItemBean.wupinmiaoshu.trim())
        shiquzhexingmingTv.text = "${mShiquxinxiItemBean.shiquzhexingming}"
        shiquzhedianhuaTv.text = "${mShiquxinxiItemBean.shiquzhedianhua}"
        storeupnumTv.text = "${mShiquxinxiItemBean.storeupnum}"
        wupinzhuangtaiTv.text = "${mShiquxinxiItemBean.wupinzhuangtai}"
        initCollection()
    }



    private fun ShiquxinxicommonDetailsHeaderLayoutBinding.initCollection(){/*收藏关注*/
        HomeRepository.list<StoreupItemBean>("storeup", mapOf("page" to "1","limit" to "1","refid" to mId.toString(),
            "tablename" to "shiquxinxi","userid" to Utils.getUserId().toString(),"type" to "1")).observeKt {
            it.getOrNull()?.let {
                collectionIbtn.isSelected = it.data.list.isNotEmpty()/*true为已收藏*/
                collectionIbtn.text = Html.fromHtml(if (collectionIbtn.isSelected) "&#xe668;" else "&#xe669;")
            }
        }
        collectionIbtn.setOnClickListener {
            XPopup.Builder(requireActivity()).asConfirm("提示",if (collectionIbtn.isSelected)"是否取消" else "是否收藏") {
                if (collectionIbtn.isSelected){/*取消收藏或关注*/
                    HomeRepository.list<StoreupItemBean>("storeup", mapOf("page" to "1","limit" to "1",
                        "refid" to mId.toString(), "tablename" to "shiquxinxi", "userid" to Utils.getUserId().toString(), "type" to "1" )).observeKt {
                        it.getOrNull()?.let {
                            if (it.data.list.isNotEmpty()){
                                HomeRepository.delete<StoreupItemBean>("storeup", listOf(it.data.list[0].id)).observeKt {
                                    it.getOrNull()?.let {
                                        mShiquxinxiItemBean.storeupnum-=1
                                        UserRepository.update("shiquxinxi",mShiquxinxiItemBean).observeKt {
                                            it.getOrNull()?.let {
                                                storeupnumTv.text = mShiquxinxiItemBean.storeupnum.toString()
                                            }
                                         }
                                        "取消成功".showToast()
                                        collectionIbtn.isSelected =false
                                        collectionIbtn.text = Html.fromHtml("&#xe669;")
                                    }
                                }
                            }
                        }
                    }
                }else{/*收藏或关注*/
                    HomeRepository.add<StoreupItemBean>("storeup",StoreupItemBean(
                        userid = Utils.getUserId(),
                        name = mShiquxinxiItemBean.wupinmingcheng.toString(),
                        inteltype=mShiquxinxiItemBean.wupinmingcheng,
                        picture=mShiquxinxiItemBean.tupian.split(",")[0],
                        refid = mShiquxinxiItemBean.id,
                        tablename="shiquxinxi",
                        type="1"                    )).observeKt {
                        it.getOrNull()?.let {
                            mShiquxinxiItemBean.storeupnum+=1
                            UserRepository.update("shiquxinxi",mShiquxinxiItemBean).observeKt {
                                it.getOrNull()?.let {
                                    storeupnumTv.text = mShiquxinxiItemBean.storeupnum.toString()
                                }
                            }
                            "收藏成功".showToast()
                            collectionIbtn.isSelected = true
                            collectionIbtn.text = Html.fromHtml("&#xe668;")
                        }
                    }
                }
            }.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== AppCompatActivity.RESULT_OK && requestCode==101){
            loadData()
            mDetailsViewModel.comments("discussshiquxinxi", mapOf("page" to "1","limit" to "10","refid" to mId.toString()))
        }
    }

}