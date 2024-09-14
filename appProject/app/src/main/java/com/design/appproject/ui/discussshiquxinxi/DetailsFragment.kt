package com.design.appproject.ui.discussshiquxinxi

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
import com.design.appproject.logic.viewmodel.discussshiquxinxi.DetailsViewModel
import androidx.activity.viewModels
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.databinding.DiscussshiquxinxicommonDetailsLayoutBinding
import com.design.appproject.databinding.DiscussshiquxinxicommonDetailsHeaderLayoutBinding
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
 * 拾取信息评论表详情页
 */
@Route(path = CommonArouteApi.PATH_FRAGMENT_DETAILS_DISCUSSSHIQUXINXI)
class DetailsFragment : BaseBindingFragment<DiscussshiquxinxicommonDetailsLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0 /*id*/

    @JvmField
    @Autowired
    var mIsBack: Boolean = false /*是否用户后台进入*/

    private val mDetailsViewModel by viewModels<DetailsViewModel>()

    private var mDiscussshiquxinxiItemBean=DiscussshiquxinxiItemBean()/*详情内容*/

    private val mCommentsAdatper by lazy {
        CommentsAdatper().apply {
            pageLoadMoreListener {
                mDetailsViewModel.comments("discussdiscussshiquxinxi", mapOf("page" to it.toString(),"limit" to "10","refid" to mId.toString()))
            }
        }
    }

    private val mHeader by lazy {
        LayoutInflater.from(requireActivity()).inflate(R.layout.discussshiquxinxicommon_details_header_layout, null,false)
    }
    private val mHeaderBinding:DiscussshiquxinxicommonDetailsHeaderLayoutBinding by lazy {
        mHeader.getBinding()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initEvent() {
        setBarTitle("拾取信息评论表详情页")
        setBarColor("#FFFFFF","black")
        binding.apply{
            mCommentsAdatper.addHeaderView(mHeader)
            srv.setAdapter(mCommentsAdatper)
            srv.setOnRefreshListener {
                loadData()
                mDetailsViewModel.comments("discussdiscussshiquxinxi", mapOf("page" to "1","limit" to "10","refid" to mId.toString()))
            }
            mHeaderBinding.addCommentBtn.setOnClickListener {
                ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_DISCUSSSHIQUXINXI)
                    .withLong("mRefid",mId)
                    .navigation(requireActivity(),101)
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
                mDiscussshiquxinxiItemBean = info.data
                mHeaderBinding.setInfo()
            }
        }
        mDetailsViewModel.comments("discussdiscussshiquxinxi", mapOf("page" to "1","limit" to "10","refid" to mId.toString()))
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
        mDetailsViewModel.info("discussshiquxinxi",mId.toString())
    }


    private fun DiscussshiquxinxicommonDetailsHeaderLayoutBinding.setInfo(){
        refidTv.text = "${mDiscussshiquxinxiItemBean.refid}"
        avatarurlIfv.load(requireActivity(),"${mDiscussshiquxinxiItemBean.avatarurl}")
        nicknameTv.text = "${mDiscussshiquxinxiItemBean.nickname}"
        contentTv.text = "${mDiscussshiquxinxiItemBean.content}"
        replyTv.text = "${mDiscussshiquxinxiItemBean.reply}"
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== AppCompatActivity.RESULT_OK && requestCode==101){
            loadData()
            mDetailsViewModel.comments("discussdiscussshiquxinxi", mapOf("page" to "1","limit" to "10","refid" to mId.toString()))
        }
    }

}