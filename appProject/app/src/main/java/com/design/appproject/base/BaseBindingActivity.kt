package com.design.appproject.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import com.design.appproject.widget.CommonTitleBarView
import com.design.appproject.widget.StateView
import com.design.appproject.widget.StateView.Companion.STATE_NO_FIND
import com.dylanc.viewbinding.base.ViewBindingUtil
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.union.union_basic.ext.*
import com.union.union_basic.logger.LoggerManager
import com.union.union_basic.network.BaseException
import com.union.union_basic.network.BaseRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * classname：BaseBindingActivity
 * desc: 基础activity类，
 * 1、通过viewbinding辅助类进行封装，方便使用能减少 id 写错或类型写错导致的异常;
 */
abstract class BaseBindingActivity<VB : ViewBinding> : AppCompatActivity() {

    private var mIsShow = false

    private val loadingDialog by lazy { //加载弹窗
        QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
            .setTipWord("正在加载").create()
    }

    private val mParentFl by lazy {
        window.decorView as FrameLayout
    }
    private var isShowTitleBar = false //是否要显示标题
    private var isInitView = false //是否初始化了情感图
    private var isInitListenView = false //是否初始化了情感图
    protected var isFullSceen = false //是否全屏

    private var mStateMarginTop = 0
    private val mStateView by lazy { //情感图界面
        isInitView = true
        findViewById<ViewStub>(R.id.state_view).inflate().toConversion<StateView>()!!.apply {
            if (mStateMarginTop > 0) {
                this.marginKTX(top = mStateMarginTop)
            } else {
                binding.root.toConversion<ViewGroup>()?.let { //如果有标题栏，则将情感图放在标题栏之下
                    it.children.find { it is CommonTitleBarView }?.let {
                        val marginTop = it.bottom
                        this.marginKTX(top = marginTop)
                    }
                }
            }
        }
    }

    val activityTitleBar by lazy { //标题
        findViewById<ViewStub>(R.id.activity_title_bar).inflate()
            .toConversion<CommonTitleBarView>()!!.apply {

            }
    }

    fun setStateViewMarginTop(marginTop: Int) {
        mStateMarginTop = marginTop
    }

    lateinit var binding: VB

    private fun condition(): Boolean { //只处理安卓10机器
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
    }

    private fun intercept(context: Context?, bundle: Bundle?) {
        if (bundle == null) return
        val condition = condition()
        if (context != null && condition) {
            bundle.classLoader = context.javaClass.classLoader // 需要修改的是bundle下androidx.lifecycle.BundlableSavedStateRegistry.key中子项的classloader
            bundle.getBundle("androidx.lifecycle.BundlableSavedStateRegistry.key")?.let {
                it.keySet()?.forEach { key ->
                    (it.get(key) as? Bundle)?.classLoader = context.javaClass.classLoader
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            LoggerManager.d("class_name:${localClassName}")
            super.onCreate(savedInstanceState)
            ARouter.getInstance().inject(this)
            setContentView(R.layout.common_base_layout)
            binding = ViewBindingUtil.inflateWithGeneric(this, mParentFl)
            mParentFl.addView(binding.root, 0)
            initEvent()
            initData()
            GlobalScope.launch {/*延迟检查是否有虚拟导航栏*/
                delay(200)
                val barShow = BarUtils.isNavBarVisible(this@BaseBindingActivity)
                barShow.yes {
                    runOnUiThread {
                        binding.root.marginKTX(bottom =BarUtils.getNavBarHeight())
                    }
                }
            }
            isFullSceen.no {
                binding.root.marginKTX(top = BarUtils.getStatusBarHeight() + if (isShowTitleBar) 43.dp else 0)
            }
        } else { //重新启动
            com.blankj.utilcode.util.AppUtils.relaunchApp(true)
        }
    }

    fun setBarTitle(title: String? = null) {/*设置标题*/
        isShowTitleBar = title.isNotNullOrEmpty()
        title?.let {
            activityTitleBar.setTitle(title)
        }
    }

    fun setBarColor(bgColor: String? = null,textColor:String?=null) {/*设置标题*/
        bgColor?.let {
            BarUtils.addMarginTopEqualStatusBarHeight(activityTitleBar)
            BarUtils.setStatusBarColor(this,ColorUtils.string2Int(it))
            activityTitleBar.setBackgroundColor(ColorUtils.string2Int(it))
        }
        textColor?.let {
            activityTitleBar.setTitleColor(ColorUtils.string2Int(it))
            activityTitleBar.setBackColor(ColorUtils.string2Int(it))
        }
    }

    private fun setStatusMode(nightModel: Boolean) {
        BarUtils.setStatusBarLightMode(this, !nightModel)
    }

    fun toRecreate(isNight: Boolean) {
        setStatusMode(isNight)
    }

    // 加载数据
    open fun initData() {}

    abstract fun initEvent()

    //加载弹窗
    open fun showLoading(content: String = "") {
        mIsShow = true
        loadingDialog.show()
    }


    //取消弹窗
    open fun dismissLoading() {
        mIsShow.yes {
            loadingDialog.dismiss()
        }
    }

    /**
     * 扩展livedata方法，主要对请求错误进行处理
     * @receiver LiveData<Result<T>>
     * @param block Function1<Result<T>?, Unit>
     * @param isShowErrorView Boolean  是否展示错误界面
     */
    protected fun <T : Any> LiveData<Result<T>>.observeKt(
        dismissLoading: Boolean = true,
        errorBlock: (() -> Unit)? = null, block: (Result<T>) -> Unit
    ) {
        observe(this@BaseBindingActivity) { data ->
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
                        if (errorCode == BaseRepository.NETWORK_ERROR_NEED_LOGIN) { //去登录
                            ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_LOGIN)
                                .navigation()
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
    protected fun <T : Any> LiveData<Result<T>>.observeKtStateView(
        isShowErrorView: Boolean = false,
        dismissLoading: Boolean = true,
        errorBlock: (() -> Unit)? = null,
        refreshLoadCall: (() -> Unit)? = null,
        block: (Result<T>) -> Unit
    ) {
        observe(this@BaseBindingActivity) { data ->
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
                        if (errorCode == BaseRepository.NETWORK_ERROR_NEED_LOGIN) { //去登录
                            ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_LOGIN)
                                .navigation()
                        } else if (isShowErrorView) {
                            mStateView.isVisible = true
                            mStateView.setSate(
                                when (errorCode) {
                                    BaseRepository.NETWORK_ERROR_NOT_FIND -> STATE_NO_FIND
                                    BaseRepository.NETWORK_ERROR_NET -> StateView.STATE_NET_ERROR
                                    else -> StateView.STATE_SERVICE_ERROR
                                }, refreshLoadCall = refreshLoadCall
                            )
                        }
                    }
                }
            }
        }
    }
}