package com.design.appproject.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.blankj.utilcode.util.ColorUtils
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.BarUtils
import com.design.appproject.R
import com.design.appproject.widget.CommonTitleBarView
import com.design.appproject.widget.StateView
import com.dylanc.viewbinding.base.ViewBindingUtil
import com.union.union_basic.ext.*
import com.union.union_basic.network.BaseException
import com.union.union_basic.network.BaseRepository

/**
 * classname：BaseBindingFragment
 * desc: 基础fragment类
 */
abstract class BaseBindingFragment<VB : ViewBinding> : Fragment() {

    var isFragmentViewInit: Boolean = false
    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    private var isInitView = false //是否初始化了情感图

    private var isShowTitleBar = false //是否要显示标题

    val fragmentTitleBar by lazy { //标题
        mView.findViewById<ViewStub>(R.id.fragment_title_bar).inflate()
            .toConversion<CommonTitleBarView>()!!.apply {
            }
    }

    val mStateView by lazy { //情感图界面
        isInitView = true
        mParentFl.findViewById<ViewStub>(R.id.state_view_fragment).inflate().toConversion<StateView>()!!.apply {
            binding.root.toConversion<ViewGroup>()?.let { //如果有标题栏，则将情感图放在标题栏之下
                it.children.find { it is CommonTitleBarView }?.let {
                    val marginTop = it.bottom
                    this.marginKTX(top = marginTop)
                }
            }
        }
    }

    private lateinit var mParentFl: FrameLayout

    lateinit var mView :View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        ARouter.getInstance().inject(this)
        mView = inflater.inflate(R.layout.common_base_fragment_layout, null)
        mParentFl = mView.findViewById(R.id.parent_fl)
        _binding = ViewBindingUtil.inflateWithGeneric(this, mParentFl)
        mParentFl.addView(binding.root, 0)
        return mView
    }
    fun setBarTitle(title: String? = null,hasBack:Boolean =true) {/*设置标题*/
        isShowTitleBar = title.isNotNullOrEmpty()
        title?.let {
            fragmentTitleBar.setTitle(title)
        }
        hasBack.no {
            fragmentTitleBar.mBackIbtn.isVisible =false
        }
    }
    fun setBarColor(bgColor: String? = null,textColor:String?=null) {/*设置标题*/
        bgColor?.let {
            fragmentTitleBar.setBackgroundColor(ColorUtils.string2Int(it))
        }
        textColor?.let {
            fragmentTitleBar.setTitleColor(ColorUtils.string2Int(it))
            fragmentTitleBar.setBackColor(ColorUtils.string2Int(it))
        }
    }
    override fun onResume() {
        super.onResume()
        if (!isFragmentViewInit) {
            initEvent()
            initData()
            binding.root.marginKTX(top = if (isShowTitleBar) 45.dp else 0)
            isFragmentViewInit = true
        }
    }

    /**
     * 初始化动作
     */
    abstract fun initEvent()

    open fun initData() { //加载数据
    }

    open fun showLoading(content: String = "") {
        activity?.toConversion<BaseBindingActivity<*>>()?.showLoading(content)
    }

    open fun dismissLoading() {
        activity?.toConversion<BaseBindingActivity<*>>()?.dismissLoading()
    }

    /**
     * 扩展livedata方法，主要对请求错误进行处理
     * @receiver LiveData<Result<T>>
     * @param block Function1<Result<T>?, Unit>
     * @param isShowErrorView Boolean  是否展示错误界面
     */
    protected fun <T : Any> LiveData<Result<T>>.observeKt(dismissLoading: Boolean = true,
        errorBlock: (() -> Unit)? = null, block: (Result<T>) -> Unit) {
        observe(this@BaseBindingFragment) { data ->
            dismissLoading.yes {
                dismissLoading()
            }
            if (data.isSuccess) {
                block(data)
            } else { //展示错误界面或空界面
                dismissLoading()
                errorBlock?.invoke()
                data.getOrElse {
                    it.toConversion<BaseException>()?.apply {
                        if (it.message.isNotNullOrEmpty()){
                            it.message?.showToast()
                        }
                        if (errorCode == BaseRepository.NETWORK_ERROR_NEED_LOGIN) { //去登录
                            ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_LOGIN).navigation()
                        }
                    }
                }
            }
        }
    }

    /**
     * 扩展livedata方法，主要对请求错误进行处理
     * @receiver LiveData<Result<T>>
     * @param block Function1<Result<T>?, Unit>
     * @param isShowErrorView Boolean  是否展示错误界面
     */
    protected fun <T : Any> LiveData<Result<T>>.observeKtStateView(isShowErrorView: Boolean = true,
        dismissLoading: Boolean = true, errorBlock: (() -> Unit)? = null, refreshLoadCall: (() -> Unit)? = null,
        iconRes: Int = 0, stateMessage: String = "", block: (Result<T>) -> Unit) {
        observe(this@BaseBindingFragment) { data ->
            if (isInitView) {
                mStateView.isVisible = false
            }
            dismissLoading.yes {
                dismissLoading()
            }
            if (data.isSuccess) {
                block(data)
            } else { //展示错误界面或空界面
                dismissLoading()
                errorBlock?.invoke()
                data.getOrElse {
                    it.toConversion<BaseException>()?.apply {
                        if (it.message.isNotNullOrEmpty()){
                            it.message?.showToast()
                        }
                        if (errorCode == BaseRepository.NETWORK_ERROR_NEED_LOGIN) { //去登录
                            ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_LOGIN).navigation()
                        } else if (isShowErrorView) {
                            mStateView.isVisible = true
                            mStateView.setSate(when (errorCode) {
                                BaseRepository.NETWORK_ERROR_NOT_FIND -> StateView.STATE_NO_FIND
                                BaseRepository.NETWORK_ERROR_NET -> StateView.STATE_NET_ERROR
                                BaseRepository.NETWORK_ERROR_JSON -> StateView.STATE_EMPTY
                                else -> StateView.STATE_SERVICE_ERROR
                            }, iconRes = iconRes, message = stateMessage, refreshLoadCall = refreshLoadCall)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}