package com.design.appproject.ui

import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import android.widget.ZoomButtonsController
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.BaseBindingActivity
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.CommonActivityWebviewBinding
import com.qmuiteam.qmui.widget.webview.QMUIWebViewClient
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.showToast
import com.union.union_basic.ext.yes
import java.lang.reflect.Field

/**
 * classname：WebViewActivity
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_WEBVIEW)
open class WebViewActivity : BaseBindingActivity<CommonActivityWebviewBinding>() {

    @Autowired
    @JvmField
    var mUrl: String = "" //地址

    var mUploadMessage: ValueCallback<Uri>? = null
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    private val FILECHOOSER_RESULTCODE = 2
    private val REQUEST_SELECT_FILE = 1

    override fun initEvent() {
        binding.apply {
            webview.setDownloadListener(object : DownloadListener {
                override fun onDownloadStart(url: String, userAgent: String, contentDisposition: String,
                    mimetype: String, contentLength: Long) {
                    doDownload(url)
                }

                private fun doDownload(url: String) { //跳转到系统浏览器下载
                    val uri: Uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            })

            webview.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress) // 修改进度条
                    progressBar.isVisible = newProgress < 100
                    progressBar.progress = newProgress
                }

                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)
                    setBarTitle(title)
                }

                override fun onShowCustomView(view: View, callback: WebChromeClient.CustomViewCallback) {
                    callback.onCustomViewHidden()
                }

                override fun onHideCustomView() {}

                // For 3.0+ Devices (Start)
                // onActivityResult attached before constructor
                open fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String?) {
                    mUploadMessage = uploadMsg
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.addCategory(Intent.CATEGORY_OPENABLE)
                    i.type = "image/*"
                    startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE)
                }

                // For Lollipop 5.0+ Devices
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
                    if (uploadMessage != null) {
                        uploadMessage?.onReceiveValue(null)
                        uploadMessage = null
                    }
                    uploadMessage = filePathCallback
                    val intent: Intent = fileChooserParams.createIntent()
                    try {
                        startActivityForResult(intent, REQUEST_SELECT_FILE)
                    } catch (e: ActivityNotFoundException) {
                        uploadMessage = null
                        Toast.makeText(baseContext, "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
                        return false
                    }
                    return true
                }

                open fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                    mUploadMessage = uploadMsg
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.addCategory(Intent.CATEGORY_OPENABLE)
                    i.type = "image/*"
                    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE)
                }
            }
            webview.webViewClient = object : QMUIWebViewClient(false, true) {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    if (!(request?.url?.toString() ?: "").startsWith("http")) { //跳转app
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(request?.url?.toString()?:""))
                        (packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0).yes {
                            startActivity(intent)
                            return true
                        }.otherwise {
                            "未下载安装相关app".showToast()
                        }
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
            webview.requestFocus(View.FOCUS_DOWN)
            setZoomControlGone(webview)
            webview.loadUrl(mUrl)
        }

    }

    open fun setZoomControlGone(webView: WebView) {
        webView.settings.displayZoomControls = false
        val classType: Class<*>
        val field: Field
        try {
            classType = WebView::class.java
            field = classType.getDeclaredField("mZoomButtonsController")
            field.isAccessible = true
            val zoomButtonsController = ZoomButtonsController(webView)
            zoomButtonsController.zoomControls.visibility = View.GONE
            try {
                field[webView] = zoomButtonsController
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null) return
                uploadMessage!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                uploadMessage = null
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            val result = if (data == null || resultCode != RESULT_OK) null else data.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        } else Toast.makeText(baseContext, "选择图片失败", Toast.LENGTH_LONG).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webview.canGoBack()) {
            binding.webview.goBack() //返回上个页面
            return true;
        }
        return super.onKeyDown(keyCode, event); //退出H5界面
    }
}