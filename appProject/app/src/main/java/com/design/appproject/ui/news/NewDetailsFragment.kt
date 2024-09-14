package com.design.appproject.ui.news

import androidx.fragment.app.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.BaseBindingFragment
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.NewsDetailsActivityBinding
import com.design.appproject.logic.viewmodel.news.NewDetailsModel

@Route(path = CommonArouteApi.PATH_FRAGMENT_DETAILS_NEWS)
class NewDetailsFragment: BaseBindingFragment<NewsDetailsActivityBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0 /*id*/

    private val mNewDetailsModel by viewModels<NewDetailsModel>()

    override fun initEvent() {
        setBarTitle("资讯详情")
    }

    override fun initData() {
        super.initData()
        mNewDetailsModel.newsInfo(mId)
        mNewDetailsModel.newsInfoLiveData.observeKt {
            it.getOrNull()?.let {
                binding.titleTv.text = it.data.title
                binding.webview.setHtml(it.data.content.replace("<img","<img style=\"width: 100%;").trim())
            }
        }
    }
}