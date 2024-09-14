package com.design.appproject.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.design.appproject.R
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.SmartGlideImageLoader
import com.union.union_basic.ext.dp
import com.union.union_basic.ext.toConversion
import com.union.union_basic.logger.LoggerManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.regex.Pattern

/**
 * classname：CustomWebView
 * author：ZWQ
 * date: 2022/8/30 10:45
 * desc: 自定义webview
 */
@SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
class CustomWebView : WebView {

    private var startClickTime: Long = 0
    private var onClickListener: ((View) -> Boolean)? = null
    private var cacheText: String? = ""

    var PATTEN_EMOJI = "\\[em[A-Za-z0-9_\\-\\u4e00-\\u9fa5]+\\]" //表情包匹配规则
    var mEmojiBeanList: List<Pair<String, String>> = mutableListOf() //表情包数据 first为code，second为图片地址

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) { //取消滚动条
    }

    init {
        scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY //取消滚动条
        setBackgroundResource(R.color.common_transparent)
        setBackgroundColor(0)
        settings.setSupportZoom(false) //不支持缩放功能
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        addJavascriptInterface(RichTextJs(), "RichTextJs")
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startClickTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {
                    if (onClickListener == null) return@setOnTouchListener false
                    val clickDuration = System.currentTimeMillis() - startClickTime
                    if (clickDuration < 200) {
                        return@setOnTouchListener onClickListener!!.invoke(this)
                    }
                }
            }
            false
        } //TAG2
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.postDelayed({
                    loadUrl("""javascript:RichTextJs.resize(document.body.getBoundingClientRect().height)""")
                }, 50)
            }
        }
    }

    /**
     * RichTextView 点击事件监听
     * @param listener 监听 返回true拦截事件
     **/
    fun setOnClickListener(listener: (View) -> Boolean) {
        onClickListener = listener
    }

    fun setHtml(text: String?) {
        if (text == null) return
        cacheText = text //先解析表情包，将其转换为<img标签>
        val matcher = Pattern.compile(PATTEN_EMOJI, Pattern.CASE_INSENSITIVE).matcher(text.toString())
        while (matcher.find()) {
            val key = matcher.group()
            if (matcher.start() < 0) {
                continue
            } // 根据对应的key,找出相应的表情文件
            var emojiUrl = ""
            mEmojiBeanList.forEach {
                if (it.first == key) {
                    emojiUrl = "<img src=\"${it.second}\"/>"
                }
            }
            cacheText = cacheText?.replace(key, emojiUrl)
        }
        val doc = Jsoup.parse(cacheText)
        changeColor(doc)
        fixImg(doc)
        fixA(doc)
        fixEmbed(doc) //TAG2
        val docHtml = """
            <!DOCTYPE html>
            ${doc.html()}
        """
        loadDataWithBaseURL(null, docHtml, "text/html", "UTF-8", null)
    }

    fun getHtml(): String {
        return cacheText ?: ""
    }

    /** 修复 img 标签 **/
    private fun fixImg(doc: Document) { //使用 jsoup 修改 img 的属性:
        val images = doc.getElementsByTag("img")
        for (i in 0 until images.size) { //宽度最大100%，高度自适应
            if (images[i].toString().contains("emoticon")) {
                images[i].attr("style",
                    "max-width: 100%; height: auto;") //.attr("onclick", """RichTextJs.onTagClick(this.src, this.getAttribute('data-filename'))""")
                    .attr("onclick", """RichTextJs.onTagClick(this.src,'表情包')""")
            } else {
                images[i].attr("style",
                    "max-width: 100%; height: auto;border-radius: 5px;") //.attr("onclick", """RichTextJs.onTagClick(this.src, this.getAttribute('data-filename'))""")
                    .attr("onclick", """RichTextJs.onTagClick(this.src,'图片')""")
            }
        }
    }

    /** 修复 a 标签 **/
    private fun fixA(doc: Document) { //使用 jsoup 修改 img 的属性:
        val `as` = doc.getElementsByTag("a")
        for (i in 0 until `as`.size) {
            val tempA = `as`[i]
            LoggerManager.d("tempA$tempA")
//            if (tempA.toString().contains(CommonBean.AT_USER_URL)) { //@别人
//                tempA.attr("onclick", """RichTextJs.onTagClick('${tempA.attr("href")}','@')""")
//                    .attr("href", "javascript:void(0)")
//                    .attr("style", "word-break: break-word;color: green;text-decoration: none")
//            } else {
                tempA.attr("onclick", """RichTextJs.onTagClick('${tempA.attr("href")}','LINK')""")
                    .attr("href", "javascript:void(0)").attr("style", "word-break: break-word")
//            }
        }
    }

    /** 修复 颜色 标签 **/
    private fun changeColor(doc: Document) { //使用 jsoup 修改 embed 的属性:
        val divList = doc.getElementsByTag("div")
//        val isNight = StorageUtil.decodeBool(CommonBean.SKIN_MODEL_NIGHT, false)
        val color = "color:#333333"
        divList.forEach {
            it.attr("style", color)
        }
    }

    /** 修复 embed 标签 **/
    private fun fixEmbed(doc: Document) { //使用 jsoup 修改 embed 的属性:
        val embeds = doc.getElementsByTag("embed")
        for (element in embeds) { //宽度最大100%，高度自适应
            element.attr("style", "max-width: 100%; height: auto;").attr("controls", "controls")
        } //webview 无法正确识别 embed 为视频时，需要这个标签改成 video 手机就可以识别了
        doc.select("embed").tagName("video")
    }

    private inner class RichTextJs {
        //TAG1 js调用 标签点击
        //@JavascriptInterface注解方法，js端调用，4.2以后安全
        //4.2以前，当JS拿到Android这个对象后，就可以调用这个Android对象中所有的方法，包括系统类（java.lang.Runtime 类），从而进行任意代码执行。
        @JavascriptInterface
        fun onTagClick(url: String, info: String) {
            if (info == "图片") {
                context.toConversion<Activity>()?.runOnUiThread {
                    XPopup.Builder(context).asImageViewer(null, 0, listOf(url), { popupView, position ->
                        if (getChildAt(position) is ImageFilterView) {
                            popupView.updateSrcView(getChildAt(position) as ImageFilterView)
                        }
                    }, SmartGlideImageLoader(true, com.luck.picture.lib.R.drawable.ps_image_placeholder)).show()
                }
            } else if (info == "@") { //@的用户
//                if (url.contains(CommonBean.AT_USER_URL)) {
//                    MyUtils.toUserIndexActivity(url.substring(url.lastIndexOf("=") + 1).toInt())
//                }
            }
            println(url)
        }

        //TAG2 js调用 重设WebView高度
        @JavascriptInterface
        fun resize(height: Int) {
            if (context is Activity) {
                (context as Activity).runOnUiThread { //重设高度
                    layoutParams = layoutParams.also {
                        it.height = (height + 40).dp
                    }
                }
            }
        }
    }
}