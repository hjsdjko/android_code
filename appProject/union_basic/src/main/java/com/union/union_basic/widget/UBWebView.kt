package com.union.union_basic.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.webkit.DownloadListener
import android.webkit.WebSettings
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIPackageHelper
import com.qmuiteam.qmui.widget.webview.QMUIWebView

/**
 * classname：UBWebView
 * desc: 基础webview
 */
open class UBWebView : QMUIWebView {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected fun init(context: Context?) {
        val webSettings = settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.defaultTextEncodingName = "GBK"
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.textZoom = 100
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        val screen = QMUIDisplayHelper.getScreenWidth(context).toString() + "x" + QMUIDisplayHelper.getScreenHeight(context)
        val userAgent = ("UBWebView/" + QMUIPackageHelper.getAppVersion(
            context) + " (Android; " + Build.VERSION.SDK_INT + "; Screen/" + screen + "; Scale/" + QMUIDisplayHelper.getDensity(context) + ")")
        val agent = settings.userAgentString
        if (agent == null || !agent.contains(userAgent)) {
            settings.userAgentString = "$agent $userAgent"
        }
        setDownloadListener(object : DownloadListener {
            override fun onDownloadStart(url: String, userAgent: String, contentDisposition: String, mimetype: String, contentLength: Long) {
                doDownload(url)
            }

            private fun doDownload(url: String) { //跳转到系统浏览器下载
                val uri: Uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context?.startActivity(intent)
            }
        })
    }

    fun exec(jsCode: String?) {
        evaluateJavascript(jsCode!!, null)
    }
}