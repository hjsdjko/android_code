package com.design.appproject.widget

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.webkit.DownloadListener
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIPackageHelper
import com.qmuiteam.qmui.util.QMUIWindowInsetHelper
import com.qmuiteam.qmui.util.QMUIWindowInsetHelper.InsetHandler
import com.qmuiteam.qmui.widget.webview.QMUIWebViewClient
import com.union.union_basic.BuildConfig
import java.lang.reflect.Method

class UnionWebView : WebView{

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private val TAG = "QMUIWebView"
    private var sIsReflectionOccurError = false

    private var mAwContents: Any? = null
    private var mWebContents: Any? = null
    private var mSetDisplayCutoutSafeAreaMethod: Method? = null
    private var mSafeAreaRectCache: Rect? = null

    /**
     * if true, the web content may be located under status bar
     */
    private var mNeedDispatchSafeAreaInset = false
    private var mCallback: Callback? = null
    private val mOnScrollChangeListeners: MutableList<OnScrollChangeListener> = ArrayList()

    private fun init() {
        removeJavascriptInterface("searchBoxJavaBridge_")
        removeJavascriptInterface("accessibility")
        removeJavascriptInterface("accessibilityTraversal")
        QMUIWindowInsetHelper.handleWindowInsets(this,
            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout(),
            InsetHandler { view, insets ->
                if (mNeedDispatchSafeAreaInset) {
                    val density = QMUIDisplayHelper.getDensity(getContext())
                    val rect = Rect((insets.left / density + getExtraInsetLeft(density)).toInt(),
                        (insets.top / density + getExtraInsetTop(density)).toInt(),
                        (insets.right / density + getExtraInsetRight(density)).toInt(),
                        (insets.bottom / density + getExtraInsetBottom(density)).toInt())
                    setStyleDisplayCutoutSafeArea(rect)
                }
            }, true, false, false)

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
        // 开启调试
        if (BuildConfig.DEBUG) {
            setWebContentsDebuggingEnabled(true)
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

    fun addCustomOnScrollChangeListener(listener: OnScrollChangeListener) {
        if (!mOnScrollChangeListeners.contains(listener)) {
            mOnScrollChangeListeners.add(listener)
        }
    }

    fun removeOnScrollChangeListener(listener: OnScrollChangeListener) {
        mOnScrollChangeListeners.remove(listener)
    }

    fun removeAllOnScrollChangeListener() {
        mOnScrollChangeListeners.clear()
    }

    protected override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        for (onScrollListener in mOnScrollChangeListeners) {
            onScrollListener.onScrollChange(this, l, t, oldl, oldt)
        }
    }

    override fun setWebViewClient(client: WebViewClient) {
        require(client is QMUIWebViewClient) { "must use the instance of QMUIWebViewClient" }
        super.setWebViewClient(client)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return super.dispatchKeyEvent(event)
    }

    fun setNeedDispatchSafeAreaInset(needDispatchSafeAreaInset: Boolean) {
        if (mNeedDispatchSafeAreaInset != needDispatchSafeAreaInset) {
            mNeedDispatchSafeAreaInset = needDispatchSafeAreaInset
            if (ViewCompat.isAttachedToWindow(this)) {
                if (needDispatchSafeAreaInset) {
                    ViewCompat.requestApplyInsets(this)
                } else { // clear insets
                    setStyleDisplayCutoutSafeArea(Rect())
                }
            }
        }
    }

    fun isNeedDispatchSafeAreaInset(): Boolean {
        return mNeedDispatchSafeAreaInset
    }

    fun setCallback(callback: Callback?) {
        mCallback = callback
    }

    private fun doNotSupportChangeCssEnv() {
        sIsReflectionOccurError = true
        if (mCallback != null) {
            mCallback!!.onSureNotSupportChangeCssEnv()
        }
    }

    fun isNotSupportChangeCssEnv(): Boolean {
        return sIsReflectionOccurError
    }

    protected fun getExtraInsetTop(density: Float): Int {
        return 0
    }

    protected fun getExtraInsetLeft(density: Float): Int {
        return 0
    }

    protected fun getExtraInsetRight(density: Float): Int {
        return 0
    }

    protected fun getExtraInsetBottom(density: Float): Int {
        return 0
    }

    override fun destroy() {
        mAwContents = null
        mWebContents = null
        mSetDisplayCutoutSafeAreaMethod = null
        stopLoading()
        super.destroy()
    }

    private fun setStyleDisplayCutoutSafeArea(rect: Rect) {
        if (sIsReflectionOccurError || Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            return
        }
        if (rect === mSafeAreaRectCache) {
            return
        }
        if (mSafeAreaRectCache == null) {
            mSafeAreaRectCache = Rect(rect)
        } else {
            mSafeAreaRectCache!!.set(rect)
        }
        val start = System.currentTimeMillis()
        if (mAwContents == null || mWebContents == null || mSetDisplayCutoutSafeAreaMethod == null) {
            try {
                val providerField = WebView::class.java.getDeclaredField("mProvider")
                providerField.isAccessible = true
                val provider = providerField[this]
                mAwContents = getAwContentsFieldValueInProvider(provider)
                if (mAwContents == null) {
                    return
                }
                mWebContents = getWebContentsFieldValueInAwContents(mAwContents!!)
                if (mWebContents == null) {
                    return
                }
                mSetDisplayCutoutSafeAreaMethod = getSetDisplayCutoutSafeAreaMethodInWebContents(mWebContents!!)
                if (mSetDisplayCutoutSafeAreaMethod == null) { // no such method, maybe the old version
                    doNotSupportChangeCssEnv()
                    return
                }
            } catch (e: Exception) {
                doNotSupportChangeCssEnv()
                Log.i(TAG, "setStyleDisplayCutoutSafeArea error: $e")
            }
        }
        try {
            mSetDisplayCutoutSafeAreaMethod!!.isAccessible = true
            mSetDisplayCutoutSafeAreaMethod!!.invoke(mWebContents, rect)
        } catch (e: Exception) {
            sIsReflectionOccurError = true
            Log.i(TAG, "setStyleDisplayCutoutSafeArea error: $e")
        }
        Log.i(TAG, "setDisplayCutoutSafeArea speed time: " + (System.currentTimeMillis() - start))
    }

    protected override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ViewCompat.requestApplyInsets(this)
    }

    @Throws(IllegalAccessException::class, NoSuchFieldException::class)
    private fun getAwContentsFieldValueInProvider(provider: Any): Any? {
        try {
            val awContentsField = provider.javaClass.getDeclaredField("mAwContents")
            if (awContentsField != null) {
                awContentsField.isAccessible = true
                return awContentsField[provider]
            }
        } catch (ignored: NoSuchFieldException) {
        } // Unfortunately, the source code is ugly in some roms, so we can not reflect the field/method by name
        for (field in provider.javaClass.declaredFields) { // 1. get field mAwContents
            field.isAccessible = true
            val awContents = field[provider] ?: continue
            if (awContents.javaClass.simpleName == "AwContents") {
                return awContents
            }
        }
        return null
    }

    @Throws(IllegalAccessException::class)
    private fun getWebContentsFieldValueInAwContents(awContents: Any): Any? {
        try {
            val webContentsField = awContents.javaClass.getDeclaredField("mWebContents")
            if (webContentsField != null) {
                webContentsField.isAccessible = true
                return webContentsField[awContents]
            }
        } catch (ignored: NoSuchFieldException) {
        } // Unfortunately, the source code is ugly in some roms, so we can not reflect the field/method by name
        for (innerField in awContents.javaClass.declaredFields) {
            innerField.isAccessible = true
            val webContents = innerField[awContents] ?: continue
            if (webContents.javaClass.simpleName == "WebContentsImpl") {
                return webContents
            }
        }
        return null
    }

    private fun getSetDisplayCutoutSafeAreaMethodInWebContents(webContents: Any): Method? {
        try {
            return webContents.javaClass.getDeclaredMethod("setDisplayCutoutSafeArea", Rect::class.java)
        } catch (ignored: NoSuchMethodException) {
        } // Unfortunately, the source code is ugly in some roms, so we can not reflect the field/method by name
        // not very safe in future
        for (method in webContents.javaClass.declaredMethods) {
            if (method.returnType == Void.TYPE && method.parameterTypes.size == 1 && method.parameterTypes[0] == Rect::class.java) {
                return method
            }
        }
        return null
    }

    interface Callback {
        fun onSureNotSupportChangeCssEnv()
    }

    interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param webView    The view whose scroll position has changed.
         * @param scrollX    Current horizontal scroll origin.
         * @param scrollY    Current vertical scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        fun onScrollChange(webView: WebView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)
    }
}