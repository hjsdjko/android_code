package com.design.appproject
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.design.appproject.base.CommonBean
import com.design.appproject.base.NetRetrofitClient
import com.design.appproject.ext.UrlPrefix
import com.union.union_basic.BasicInit
import com.union.union_basic.image.loader.GlideLoader
import com.union.union_basic.logger.LoggerManager
import com.union.union_basic.utils.AppUtils
import me.jessyan.autosize.AutoSizeConfig
import com.union.union_basic.utils.StorageUtil
import okhttp3.Interceptor
/***
 * 类名：com.design.appproject.MainApplication
 * 文件描述：主程序application
 */
class MainApplication : MultiDexApplication() {
    companion object {
        var IS_DEBUG = true
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        LoggerManager.initLogger(false)
        BasicInit.init()
        ARouter.init(AppUtils.getApp()) // 尽可能早，推荐在Application中初始化
        if (BuildConfig.DEBUG) {
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        AutoSizeConfig.getInstance().isExcludeFontScale = true //屏蔽系统字体大小对 AndroidAutoSize 的影响

        val SERVICE_URL = "http://localhost:8080/cl42597122/"
        UrlPrefix.URL_PREFIX = SERVICE_URL
        GlideLoader.setRequestOptions(R.color.common_bg_color_gray2,R.color.common_bg_color_gray2)
        val interceptors  = mutableListOf(
            Interceptor { chain: Interceptor.Chain ->
                val build = chain.request().newBuilder()
                    .addHeader("content-type", "application/json")
                    .addHeader("Token", StorageUtil.decodeString(CommonBean.TOKEN_KEY,"")?:"")
                chain.proceed(build.build())
            },
        )
        NetRetrofitClient.initClient(SERVICE_URL,interceptors)
    }
}