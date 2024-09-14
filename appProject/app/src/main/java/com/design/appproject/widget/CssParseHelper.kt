package com.design.appproject.widget

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.design.appproject.R
import com.design.appproject.calc.Calculator
import com.qmuiteam.qmui.layout.IQMUILayout
import com.union.union_basic.ext.cssdp
import com.union.union_basic.ext.dp
import com.union.union_basic.ext.isNotNullOrEmpty
import com.union.union_basic.ext.marginKTX
import com.union.union_basic.ext.toConversion
import com.union.union_basic.ext.yes
import com.union.union_basic.logger.LoggerManager
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


/*css属性解析*/
class CssParseHelper {

    var mCaclWidth: String = ""
    var mCaclHeight: String = ""
    var mPerWidth = ""
    var mPerHeight = ""
    var mPerMarginLeft = ""
    var mPerMarginTop = ""
    var mPerMarginRight = ""
    var mPerMarginBottom = ""
    var mPaddingLeft = ""
    var mPaddingRight = ""
    var mPaddingTop = ""
    var mPaddingBottom = ""
    var mBorderColor = 0
    var mBorderWidth = ""
    var mHttpBackGround = ""
    var mCssLeft = -9999f
    var mCssTop = -9999f
    var mCssBottom = -9999f
    var mCssRight = -9999f
    var mCssPosition = ""
    var cssBackground = ""
    var cssShadow = ""

    private val mPanint by lazy {
        Paint().apply {
            color = mBorderColor
        }
    }
    lateinit var mOwner: WeakReference<View>

    private val mLayoutParams by lazy {
        if (mOwner.get()?.layoutParams is LinearLayout.LayoutParams) {
            mOwner.get()?.layoutParams as LinearLayout.LayoutParams
        } else if (mOwner.get()?.layoutParams is FrameLayout.LayoutParams) {
            mOwner.get()?.layoutParams as FrameLayout.LayoutParams
        } else if (mOwner.get()?.layoutParams is ViewGroup.MarginLayoutParams) {
            mOwner.get()?.layoutParams as ViewGroup.MarginLayoutParams
        } else if (mOwner.get()?.layoutParams is ViewGroup.LayoutParams) {
            mOwner.get()?.layoutParams as ViewGroup.LayoutParams
        } else {
            null
        }
    }


    fun init(context: Context, attrs: AttributeSet, view: View) {
        mOwner = WeakReference(view)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCustomCSS)
        mCaclWidth = typedArray.getString(R.styleable.MyCustomCSS_calc_width) ?: ""
        mCaclHeight = typedArray.getString(R.styleable.MyCustomCSS_calc_height) ?: ""
        mPerWidth = typedArray.getString(R.styleable.MyCustomCSS_per_width) ?: ""
        mPerHeight = typedArray.getString(R.styleable.MyCustomCSS_per_height) ?: ""
        mPerMarginLeft = typedArray.getString(R.styleable.MyCustomCSS_per_marginLeft) ?: ""
        mPerMarginRight = typedArray.getString(R.styleable.MyCustomCSS_per_marginRight) ?: ""
        mPerMarginTop = typedArray.getString(R.styleable.MyCustomCSS_per_marginTop) ?: ""
        mPerMarginBottom = typedArray.getString(R.styleable.MyCustomCSS_per_marginBottom) ?: ""
        mPaddingLeft = typedArray.getString(R.styleable.MyCustomCSS_per_paddingLeft) ?: ""
        mPaddingRight = typedArray.getString(R.styleable.MyCustomCSS_per_paddingRight) ?: ""
        mPaddingTop = typedArray.getString(R.styleable.MyCustomCSS_per_paddingTop) ?: ""
        mPaddingBottom = typedArray.getString(R.styleable.MyCustomCSS_per_paddingBottom) ?: ""
        mBorderColor = typedArray.getColor(R.styleable.MyCustomCSS_css_borderColor, Color.BLACK)
        mBorderWidth = typedArray.getString(R.styleable.MyCustomCSS_css_borderWidth) ?: ""
        mCssPosition = typedArray.getString(R.styleable.MyCustomCSS_css_position) ?: ""
        mCssLeft = typedArray.getDimension(R.styleable.MyCustomCSS_css_left, -9999f)
        mCssTop = typedArray.getDimension(R.styleable.MyCustomCSS_css_top, -9999f)
        mCssRight = typedArray.getDimension(R.styleable.MyCustomCSS_css_right, -9999f)
        mCssBottom = typedArray.getDimension(R.styleable.MyCustomCSS_css_bottom, -9999f)
        mHttpBackGround = typedArray.getString(R.styleable.MyCustomCSS_http_background) ?: ""
        cssBackground = typedArray.getString(R.styleable.MyCustomCSS_css_background) ?: ""
        cssShadow = typedArray.getString(R.styleable.MyCustomCSS_css_shadow) ?: ""
        if (mHttpBackGround.startsWith("http")) {
            setBCImg(view,mHttpBackGround)
        }
        if (cssBackground.isNotNullOrEmpty() || mCssPosition.isNotNullOrEmpty()){
            val vto = mOwner.get()?.viewTreeObserver
            vto?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (vto.isAlive) {
                        vto?.removeOnGlobalLayoutListener(this)
                    }
                    mOwner.get()?.postDelayed({
                        if (cssBackground.isNotNullOrEmpty()&&!hasSetBg){
                            hasSetBg=true
                            getLayerDrawable()
                        }
                        if (mCssPosition.isNotNullOrEmpty()){
                            cssPosition()
                        }
                    }, 500)
                }
            })
        }
    }

    var hasSetBg =false//是否设置过背景重叠样式的标识

    //背景重叠的样式适配
    private fun getLayerDrawable(){
        if (cssBackground.startsWith("linear-gradient")){
            val lg = cssBackground.replace("linear-gradient(","").replace(")","")
            val liList = lg.split(",")
            LoggerManager.d("colorList_lg:${lg}")
            if (liList.size>=3){
                val colorList =liList.subList(1,liList.size)
                val colors = mutableListOf<Int>()
                val position = mutableListOf<Float>()
                LoggerManager.d("colorList_liList:${liList}")
                LoggerManager.d("colorList:${colorList}")
                colorList.forEach {
                    val c= it.trim().split(" ")
                    colors.add(Color.parseColor(c[0]))
                    position.add(c[1].substring(0,c[1].length-1).toFloat()/100f)
                }
                val viewWith = mOwner.get()?.measuredHeight?.toFloat()?:0f
                val viewHeight = mOwner.get()?.measuredHeight?.toFloat()?:0f
                val linearGradient= if (liList[1]=="0deg"){
                    LinearGradient(viewWith/2,viewHeight,viewWith/2,0f,colors.toIntArray(),position.toFloatArray(), Shader.TileMode.CLAMP)
                }else if (liList[1]=="45deg"){
                    LinearGradient(0f,viewHeight,viewWith,0f,colors.toIntArray(),position.toFloatArray(), Shader.TileMode.CLAMP)
                }else if (liList[1]=="90deg"){
                    LinearGradient(0f,viewHeight/2,viewWith,viewHeight/2,colors.toIntArray(),position.toFloatArray(), Shader.TileMode.CLAMP)
                }else if (liList[1]=="135deg"){
                    LinearGradient(viewWith,0f,0f,viewHeight,colors.toIntArray(),position.toFloatArray(), Shader.TileMode.CLAMP)
                }else{
                    LinearGradient(viewWith,viewHeight/2,0f,viewHeight/2,colors.toIntArray(),position.toFloatArray(), Shader.TileMode.CLAMP)
                }
                val sd = ShapeDrawable().apply {
                    paint.shader =linearGradient
                }
                mOwner.get()?.background=sd
            }
        }else{
            val cblist = cssBackground.split(",")
            val layerDrawable = LayerDrawable(arrayOf())
            val drawableList = arrayOfNulls<Triple<Int,Drawable,Pair<Int,Int>>>(cblist.size)
            val latch = CountDownLatch(cblist.size)
            thread {
                latch.await(10, TimeUnit.SECONDS)
                runOnUiThread {
                    drawableList.forEachIndexed { index, pair ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (pair!=null){
                                layerDrawable.addLayer(pair.second)
                                layerDrawable.setLayerGravity(index,pair.first?:0)
                                layerDrawable.setLayerSize(index,pair.third.first,pair.third.second)
                            }
                        }
                    }
                    mOwner.get()?.background=layerDrawable
                }
            }
            cblist.reversed().forEachIndexed { index, s ->
                addLayerDrawable(s,index,drawableList,latch)
            }
        }
    }

    private fun addLayerDrawable(url:String,index:Int,drawableList:Array<Triple<Int,Drawable,Pair<Int,Int>>?>,latch:CountDownLatch){
        if (url.startsWith("url")){
            val imgTmp = url.replace("url(","").replace(")","")
            val imgList = imgTmp.split(" ")
            val imgUrl = imgList[0]
            if (imgUrl.startsWith("http")){
                Glide.with(com.union.union_basic.utils.AppUtils.getApp()).asDrawable().load(imgUrl)
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            latch.countDown()
                            super.onLoadFailed(errorDrawable)
                        }
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            var imgWidth = resource.minimumWidth
                            var imgHeight = resource.minimumHeight
                            if (imgTmp.replace(imgUrl,"").contains("/")){
                                val sizeList = imgTmp.replace(imgUrl,"").split("/")[1].trim().split(" ").toMutableList()
                                if (sizeList.size>=1 &&sizeList[0]=="cover"){
                                    sizeList[0] ="100%"
                                }
                                if (sizeList.size>=1 && sizeList[0].endsWith("%")){
                                    imgWidth = if (sizeList[0]=="100%"){
                                        -1
                                    }else{
                                        (mOwner.get()?.measuredWidth?:0)*(sizeList[0].substring(0,sizeList[0].length-1).toInt()/100)
                                    }
                                    imgHeight = (((mOwner.get()?.measuredWidth?:0)*(sizeList[0].substring(0,sizeList[0].length-1).toInt()/100))/resource.minimumWidth *resource.minimumHeight)
                                }
                                if (sizeList.size>=2 &&sizeList[1].endsWith("%")){
                                    imgHeight = (mOwner.get()?.measuredHeight?:0)*(sizeList[1].substring(0,sizeList[1].length-1).toInt()/100)
                                    if (!sizeList[0].endsWith("%")){
                                        imgWidth = (imgHeight/resource.minimumHeight *resource.minimumWidth)
                                    }
                                }
                                resource.setBounds(0,0,imgWidth,imgHeight)
                            }
                            var gravity =0
                            if (imgTmp.contains("center")){
                                gravity=gravity.and(Gravity.CENTER)
                            }
                            if (imgTmp.contains("top")){
                                gravity=gravity.or(Gravity.TOP)
                            }
                            if (imgTmp.contains("bottom")){
                                gravity=gravity.or(Gravity.BOTTOM)
                            }
                            if (imgTmp.contains("left")){
                                gravity=gravity.or(Gravity.LEFT)
                            }
                            if (imgTmp.contains("right")){
                                gravity=gravity.or(Gravity.RIGHT)
                            }
                            LoggerManager.d("tag:${mOwner.get()?.tag}_imgWidth:${imgWidth}_imgHeight:${imgHeight}")
                            drawableList[index] = Triple(gravity,resource,Pair(imgWidth,imgHeight))
                            latch.countDown()
                        }
                    })
            }
        }
        if (url.startsWith("#")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                drawableList[index] = Triple(Gravity.CENTER,ShapeDrawable().apply {
                    paint.style = Paint.Style.FILL
                    paint.color = Color.parseColor(url)
                },Pair(mOwner.get()?.measuredWidth?:0,mOwner.get()?.measuredHeight?:0))
                latch.countDown()
            }
        }
    }

    private fun setBCImg(view: View,imgPath:String){
        Glide.with(view).asDrawable().load(imgPath)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    resource.setBounds(0, 0, resource.minimumWidth, resource.minimumHeight)
                    view.setBackgroundDrawable(resource)
                }
            })
    }

    private var hasSetShadow = false

    fun drwableShadow(canvas:Canvas){
        if (cssShadow.isNotNullOrEmpty()){
            hasSetShadow=true
            mOwner.get()?.let { mOwnerView ->
                mOwnerView.toConversion<IQMUILayout>()?.apply {
//                    LoggerManager.d("shadowList_cssShadow:${cssShadow}")
                    if (cssShadow.contains(",")) {
                        val shadowList = cssShadow.split(",".toRegex())
                        shadowList.forEach {
                            drawableShadow(it.split(" "), canvas)
                        }
                    } else {
                        drawableShadow(cssShadow.split(" "), canvas)
                    }
                }
            }
        }
    }

    var drawcount = 0

    fun dispatchDraw(canvas: Canvas) {
        if (!this::mOwner.isInitialized) {
            return
        }
        if (drawcount>=20)return

        mOwner.get()?.let { mOwnerView ->
            drawcount+=1
            mBorderWidth.isNotNullOrEmpty().yes {
                val borderList = mBorderWidth.split(" ")
                if (borderList.size == 1) {
                    mOwnerView.toConversion<IQMUILayout>()?.setBorderWidth(borderList[0].cssdp.toInt())
                    mOwnerView.toConversion<IQMUILayout>()?.setBorderColor(mBorderColor)
                } else if (borderList.size == 2) {
                    mPanint.strokeWidth = borderList[0].cssdp/*上下*/
                    (mPanint.strokeWidth > 0).yes {
                        canvas.drawLine(0f, 0f, mOwnerView.width.toFloat(), 0f, mPanint)/*上边*/
                        canvas.drawLine(
                            0f,
                            mOwnerView.height.toFloat(),
                            mOwnerView.width.toFloat(),
                            mOwnerView.height.toFloat(),
                            mPanint
                        )/*下边*/
                    }
                    mPanint.strokeWidth = borderList[1].cssdp/*左右*/
                    (mPanint.strokeWidth > 0).yes {
                        canvas.drawLine(0f, 0f, 0f, mOwnerView.height.toFloat(), mPanint)/*左边*/
                        canvas.drawLine(
                            mOwnerView.width.toFloat(),
                            0f,
                            mOwnerView.width.toFloat(),
                            mOwnerView.height.toFloat(),
                            mPanint
                        )/*右边*/
                    }
                } else if (borderList.size == 3) {/*上，左右，下*/
                    mPanint.strokeWidth = borderList[0].cssdp/*上*/
                    (mPanint.strokeWidth > 0).yes {
                        canvas.drawLine(0f, 0f, mOwnerView.width.toFloat(), 0f, mPanint)/*上边*/
                    }
                    mPanint.strokeWidth = borderList[1].cssdp/*左右*/
                    (mPanint.strokeWidth > 0).yes {
                        canvas.drawLine(0f, 0f, 0f, mOwnerView.height.toFloat(), mPanint)/*左边*/
                        canvas.drawLine(
                            mOwnerView.width.toFloat(),
                            0f,
                            mOwnerView.width.toFloat(),
                            mOwnerView.height.toFloat(),
                            mPanint
                        )/*右边*/
                    }
                    mPanint.strokeWidth = borderList[2].cssdp/*下*/
                    (mPanint.strokeWidth > 0).yes {
                        canvas.drawLine(
                            0f,
                            mOwnerView.height.toFloat(),
                            mOwnerView.width.toFloat(),
                            mOwnerView.height.toFloat(),
                            mPanint
                        )/*下边*/
                    }
                } else {/*上，右，下，左*/
                    mPanint.strokeWidth = borderList[0].cssdp/*上*/
                    (mPanint.strokeWidth > 0).yes {
                        canvas.drawLine(0f, 0f, mOwnerView.width.toFloat(), 0f, mPanint)/*上边*/
                    }
                    mPanint.strokeWidth = borderList[1].cssdp/*右*/
                    (mPanint.strokeWidth > 0).yes {
                        canvas.drawLine(
                            mOwnerView.width.toFloat(),
                            0f,
                            mOwnerView.width.toFloat(),
                            mOwnerView.height.toFloat(),
                            mPanint
                        )/*右边*/
                    }
                    mPanint.strokeWidth = borderList[2].cssdp/*下*/
                    (mPanint.strokeWidth > 0).yes {
                        canvas.drawLine(
                            0f,
                            mOwnerView.height.toFloat(),
                            mOwnerView.width.toFloat(),
                            mOwnerView.height.toFloat(),
                            mPanint
                        )/*下边*/
                    }
                    mPanint.strokeWidth = borderList[3].cssdp/*左*/
                    (mPanint.strokeWidth > 0).yes {
                        canvas.drawLine(0f, 0f, 0f, mOwnerView.height.toFloat(), mPanint)/*左边*/
                    }
                }
            }
            val parent = mOwnerView.parent as ViewGroup
            mCaclWidth.isNotNullOrEmpty().yes {
                mLayoutParams?.width = getCaclSize(
                    parent.measuredWidth - (parent.paddingLeft + parent.paddingRight),
                    mCaclWidth
                )
            }
            mCaclHeight.isNotNullOrEmpty().yes {
                mLayoutParams?.height = getCaclSize(
                    parent.measuredHeight - (parent.paddingTop + parent.paddingBottom),
                    mCaclHeight
                )
            }
            mPerWidth.endsWith("%").yes {
                if (mPerWidth == "100%") {
                    mLayoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    if (parent.layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                        parent.layoutParams = parent.layoutParams.apply {
                            this.width = ViewGroup.LayoutParams.MATCH_PARENT
                        }
                    }
                    val width: Float =
                        (parent.measuredWidth - (parent.paddingLeft + parent.paddingRight)) * mPerWidth.substring(
                            0,
                            mPerWidth.length - 1
                        ).toFloat() / 100
                    mLayoutParams?.width = width.toInt()
                }
            }
            mPerHeight.endsWith("%").yes {
                if (mPerHeight == "100%") {
                    mLayoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    val height: Float =
                        (parent.measuredHeight - (parent.paddingTop + parent.paddingBottom)) * mPerHeight.substring(
                            0,
                            mPerHeight.length - 1
                        ).toFloat() / 100
                    mLayoutParams?.height = height.toInt()
                }
            }
            if (mPerMarginLeft.endsWith("%") || mPerMarginTop.endsWith("%") || mPerMarginRight.endsWith(
                    "%"
                ) || mPerMarginBottom.endsWith("%")
            ) {
                val width = parent.measuredWidth
                val height = parent.measuredHeight
                var maringLeft = 0
                var maringTop = 0
                var maringRight = 0
                var maringBottom = 0
                if (mPerMarginLeft.endsWith("%")) {
                    maringLeft = (width * mPerMarginLeft.substring(0, mPerMarginLeft.length - 1)
                        .toFloat() / 100).toInt()
                }
                if (mPerMarginTop.endsWith("%")) {
                    maringTop = (height * mPerMarginTop.substring(0, mPerMarginTop.length - 1)
                        .toFloat() / 100).toInt()
                }
                if (mPerMarginRight.endsWith("%")) {
                    maringRight = (width * mPerMarginRight.substring(0, mPerMarginRight.length - 1)
                        .toFloat() / 100).toInt()
                }
                if (mPerMarginBottom.endsWith("%")) {
                    maringBottom =
                        (height * mPerMarginBottom.substring(0, mPerMarginBottom.length - 1)
                            .toFloat() / 100).toInt()
                }
                if (mLayoutParams is LinearLayout.LayoutParams) {
                    (mLayoutParams as LinearLayout.LayoutParams).setMargins(
                        maringLeft,
                        maringTop,
                        maringRight,
                        maringBottom
                    )
                } else if (mLayoutParams is FrameLayout.LayoutParams) {
                    (mLayoutParams as FrameLayout.LayoutParams).setMargins(
                        maringLeft,
                        maringTop,
                        maringRight,
                        maringBottom
                    )
                } else if (mLayoutParams is ViewGroup.MarginLayoutParams) {
                    (mLayoutParams as ViewGroup.MarginLayoutParams).setMargins(
                        maringLeft,
                        maringTop,
                        maringRight,
                        maringBottom
                    )
                }
            }
            if (!TextUtils.isEmpty(mPaddingLeft) || !TextUtils.isEmpty(mPaddingRight) || !TextUtils.isEmpty(
                    mPaddingTop
                ) || !TextUtils.isEmpty(mPaddingBottom)
            ) {
                var paddingLeft = 0
                var paddingTop = 0
                var paddingRight = 0
                var paddingBottom = 0
                val width = parent.measuredWidth
                val height = parent.measuredHeight
                if (mPaddingLeft.endsWith("%")) {
                    paddingLeft = (width * mPaddingLeft.substring(0, mPaddingLeft.length - 1)
                        .toFloat() / 100).toInt()
                }
                if (mPaddingRight.endsWith("%")) {
                    paddingRight = (width * mPaddingRight.substring(0, mPaddingRight.length - 1)
                        .toFloat() / 100).toInt()
                }
                if (mPaddingTop.endsWith("%")) {
                    paddingTop = (height * mPaddingTop.substring(0, mPaddingTop.length - 1)
                        .toFloat() / 100).toInt()
                }
                if (mPaddingBottom.endsWith("%")) {
                    paddingBottom = (height * mPaddingBottom.substring(0, mPaddingBottom.length - 1)
                        .toFloat() / 100).toInt()
                }
                mOwnerView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            }

            mLayoutParams.let { mOwnerView.layoutParams = it }
        }
    }

    private fun drawableShadow(shadowList:List<String>,canvas: Canvas){
        mOwner.get()?.let {mOwnerView->
            if (shadowList.contains("inset")){//内阴影
                val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                var shadowSize = 0f
                if (shadowList.size==5){
                    shadowSize=shadowList[2].cssdp
                }else if (shadowList.size==6){
                    shadowSize=shadowList[3].cssdp
                }
                shadowList.find { it.startsWith("#") }?.let {
                    shadowPaint.color = Color.parseColor(it)
                }
                shadowPaint.style = Paint.Style.STROKE
                shadowPaint.strokeWidth = shadowSize/2
                shadowPaint.strokeCap = Paint.Cap.BUTT
                if (shadowSize>0){
                    shadowPaint.maskFilter = BlurMaskFilter(shadowSize/2, BlurMaskFilter.Blur.NORMAL)
                }
                val width = mOwnerView.measuredWidth.toFloat()
                val height = mOwnerView.measuredHeight.toFloat()
                val shadowRect = RectF(0f, 0f, width, height)
                canvas.drawRoundRect(shadowRect, 0f, 0f, shadowPaint)
            }else{//外阴影
                mOwnerView.parent?.toConversion<ViewGroup>()?.clipToPadding=false
                mOwnerView.toConversion<IQMUILayout>()?.apply {
                    shadowAlpha = 1f
                    shadowList.find { it.startsWith("#") }?.let {
                        shadowColor = Color.parseColor(it)
                    }
                    if (shadowList.size>=4){
                        LoggerManager.d("")
                        shadowElevation = shadowList[2].cssdp.toInt()
                    }
                }
            }
        }
    }

    private fun cssPosition() {
        mOwner.get()?.let { mOwnerView ->
            if (mCssLeft != -9999f && !isSetLeft) {
                isSetLeft = true
                var realCssLeft = getPositionDistance(3, mCssLeft < 0)
                LoggerManager.d("positionView_tag_realCssTop:$realCssLeft")
                realCssLeft = (mCssLeft - realCssLeft - mOwnerView.marginLeft).toInt()
                LoggerManager.d("positionView_tag_realCssLeft:${realCssLeft}__$mCssLeft")
//                mOwnerView.translationX = realCssLeft.toFloat()
                mOwnerView.marginKTX(left = realCssLeft)
            }
            if (mCssTop != -9999f && !isSetTop) {
                isSetTop = true
                var realCssTop = getPositionDistance(0, mCssTop < 0)//离最近的父相类顶部距离
                LoggerManager.d("positionView_tag_realCssTop:$realCssTop")
                realCssTop = (mCssTop - realCssTop - mOwnerView.marginTop).toInt()
                LoggerManager.d("positionView_tag_realCssTop:${realCssTop}__$mCssTop")
//                mOwnerView.translationY = realCssTop.toFloat()
                mOwnerView.marginKTX(top = realCssTop)
            }
            if (mCssRight != -9999f && !isSetRight) {
                isSetRight = true
                var realCssRight = getPositionDistance(1, mCssRight < 0)
                LoggerManager.d("positionView_tag_realCssRight:${realCssRight}")
                realCssRight = realCssRight - mOwnerView.marginRight - mCssRight.toInt()
                LoggerManager.d("positionView_tag_realCssRight:${realCssRight}__$mCssRight")
                mOwnerView.translationX = realCssRight.toFloat()
//                mOwnerView.marginKTX(left = realCssRight)
            }
            if (mCssBottom != -9999f && !isSetBottom) {
                isSetBottom = true
                var realCssBottom = getPositionDistance(2, mCssBottom < 0)
                LoggerManager.d("positionView_tag_realCssBottom:${realCssBottom}")
                realCssBottom = (realCssBottom - mCssBottom - mOwnerView.marginBottom).toInt()
                LoggerManager.d("positionView_tag_realCssBottom:${realCssBottom}__$mCssBottom")
                mOwnerView.translationY = realCssBottom.toFloat()
//                mOwnerView.marginKTX(top = realCssBottom)
            }
        }
    }

    private var isSetLeft = false//是否已经设置过顶部
    private var isSetTop = false//是否已经设置过顶部
    private var isSetRight = false//是否已经设置过顶部
    private var isSetBottom = false//是否已经设置过顶部

    //获取离最近的父类定位盒子的距离
    private fun getPositionDistance(position: Int, newClip: Boolean = false): Int { //0上，1右，2下，3左
        var parentPosition = 0
        val positionView = getPositionParentView(mOwner.get()?.parent, 0, newClip)
        val parenLocation = intArrayOf(0, 0)
        positionView?.toConversion<View>()?.getLocationOnScreen(parenLocation)
        if (positionView == null || (positionView !is CssInterface && positionView.toConversion<View>()?.tag!="outview")) {//以屏幕为偏移坐标
            parentPosition = when (position) {
                0 -> 0
                1 -> ScreenUtils.getScreenWidth()
                2 -> ScreenUtils.getScreenHeight()
                3 -> 0
                else -> 0
            }
        } else {
            parentPosition = when (position) {
                0 -> parenLocation[1]
                1 -> positionView.toConversion<ViewGroup>()?.measuredWidth ?: 0
                2 -> positionView.toConversion<ViewGroup>()?.measuredHeight ?: 0
                3 -> parenLocation[0]
                else -> 0
            }
        }
        var viewWidth = mOwner.get()?.measuredWidth ?: 0
        var viewHeight = mOwner.get()?.measuredHeight ?: 0
        val location = intArrayOf(0, 0)
        mOwner.get()?.getLocationOnScreen(location)
        LoggerManager.d("positionView_tag:${parenLocation[0]}_${parenLocation[1]}_${positionView?.toConversion<View>()?.measuredWidth}_${positionView?.toConversion<View>()?.measuredHeight}")
        LoggerManager.d("positionView_tag:${location[0]}_${location[1]}_${mOwner.get()?.width}_${mOwner.get()?.height}")
        return when (position) {
            0 -> location[1] - parentPosition
            1 -> parentPosition + parenLocation[0] - (location[0] + viewWidth)
            2 -> parentPosition + parenLocation[1] - (location[1] + viewHeight)
            3 -> location[0] - parentPosition
            else -> 0
        }
    }

    //递归获取最近的PositionParentView
    private fun getPositionParentView(
        parentView: ViewParent?,
        position: Int,
        newClip: Boolean = false
    ): ViewParent? {
        if (parentView == null || (parentView is CssInterface && parentView.getPostion().isNotNullOrEmpty()) ||parentView?.toConversion<View>()?.tag=="outview") {
            if (newClip) {
                parentView?.toConversion<ViewGroup>()?.clipChildren = false
                parentView?.parent?.toConversion<ViewGroup>()?.clipChildren = false
                parentView?.toConversion<ViewGroup>()?.clipToOutline = false
            }
            parentView?.toConversion<ViewGroup>()?.clipToPadding = false
            return parentView
        } else {
            return getPositionParentView(parentView.parent, position, newClip)
        }
    }

    private val mCalculator by lazy { Calculator() }

    /*获取css cacl函数宽度或高度*/
    private fun getCaclSize(size: Int, cacl: String): Int {
        return if (cacl.isNotNullOrEmpty()) {
            var caclTemp = cacl.substring(5, cacl.length - 1).replace(" ", "")
            val caclList = caclTemp.split("[*+-/]".toRegex())
            val caclMap = mutableMapOf<String, String>()
            caclList.forEachIndexed { index, s ->
                if (s.endsWith("%")) {
                    caclMap[s] = (size * (s.substring(0, s.length - 1).toFloat() / 100)).toString()
                } else if (s.endsWith("rpx") || s.endsWith("px")) {
                    caclMap[s] = s.cssdp.toString()
                }
            }
            caclMap.forEach {
                caclTemp = caclTemp.replace(it.key, it.value)
            }
            val result = mCalculator.dealStr(caclTemp)
            result.toDouble().toInt()
        } else {
            size
        }
    }
}