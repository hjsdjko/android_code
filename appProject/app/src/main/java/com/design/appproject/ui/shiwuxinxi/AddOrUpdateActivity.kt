package com.design.appproject.ui.shiwuxinxi

import android.Manifest
import com.union.union_basic.permission.PermissionUtil
import com.design.appproject.ext.UrlPrefix
import androidx.core.widget.addTextChangedListener
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.core.view.children
import com.design.appproject.utils.Utils
import com.design.appproject.bean.BaiKeBean
import androidx.core.app.ActivityCompat.startActivityForResult
import com.blankj.utilcode.util.UriUtils
import android.content.Intent
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.internal.LinkedTreeMap
import com.union.union_basic.ext.*
import com.blankj.utilcode.util.RegexUtils
import com.union.union_basic.utils.StorageUtil
import com.github.gzuliyujiang.wheelpicker.DatimePicker
import com.design.appproject.widget.BottomSpinner
import com.design.appproject.base.CommonBean
import com.blankj.utilcode.util.TimeUtils
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity
import com.github.gzuliyujiang.wheelpicker.impl.BirthdayFormatter
import com.github.gzuliyujiang.wheelpicker.impl.UnitTimeFormatter
import java.text.SimpleDateFormat
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.logic.repository.UserRepository
import com.union.union_basic.image.selector.SmartPictureSelector
import java.io.File
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.BaseBindingActivity
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.bean.ShiwuxinxiItemBean
import com.design.appproject.databinding.ShiwuxinxiaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 失物信息新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_SHIWUXINXI)
class AddOrUpdateActivity:BaseBindingActivity<ShiwuxinxiaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mRefid: Long = 0 /*refid数据*/

    /**上传数据*/
    var mShiwuxinxiItemBean = ShiwuxinxiItemBean()

    override fun initEvent() {
        setBarTitle("失物信息")
        setBarColor("#FFFFFF","black")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mShiwuxinxiItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mShiwuxinxiItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mShiwuxinxiItemBean,mRefid)
                }
            }
            if (mShiwuxinxiItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mShiwuxinxiItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mShiwuxinxiItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mShiwuxinxiItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mShiwuxinxiItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mShiwuxinxiItemBean,Utils.getUserId())
            }
        }
        binding.initView()

        binding.wupinmiaoshuRichLayout.apply{
            actionBold.setOnClickListener {
                richEt.setBold()
            }
            actionItalic.setOnClickListener {
                richEt.setItalic()
            }
            actionStrikethrough.setOnClickListener {
                richEt.setStrikeThrough()
            }
            actionUnderline.setOnClickListener {
                richEt.setUnderline()
            }
            actionHeading1.setOnClickListener {
                richEt.setHeading(1)
            }
            actionHeading2.setOnClickListener {
                richEt.setHeading(2)
            }
            actionHeading3.setOnClickListener {
                richEt.setHeading(3)
            }
            actionHeading4.setOnClickListener {
                richEt.setHeading(4)
            }
            actionHeading5.setOnClickListener {
                richEt.setHeading(5)
            }
            actionIndent.setOnClickListener {
                richEt.setIndent()
            }
            actionOutdent.setOnClickListener {
                richEt.setOutdent()
            }
            actionAlignCenter.setOnClickListener {
                richEt.setAlignCenter()
            }
            actionAlignLeft.setOnClickListener {
                richEt.setAlignLeft()
            }
            actionAlignRight.setOnClickListener {
                richEt.setAlignRight()
            }
            actionInsertBullets.setOnClickListener {
                richEt.setBullets()
            }
            actionInsertNumbers.setOnClickListener {
                richEt.setNumbers()
            }
            actionInsertImage.setOnClickListener {
                SmartPictureSelector.openPicture(this@AddOrUpdateActivity) {
                    val path = it[0]
                    UserRepository.upload(File(path),"").observeKt {
                        it.getOrNull()?.let {
                            richEt.insertImage(UrlPrefix.URL_PREFIX+"file/" + it.file, "dachshund", 320)
                        }
                    }
                }
            }
        }
    }

    fun ShiwuxinxiaddorupdateLayoutBinding.initView(){
             tupianLl.setOnClickListener {
            SmartPictureSelector.openPicture(this@AddOrUpdateActivity) {
                val path = it[0]
                showLoading("上传中...")
                UserRepository.upload(File(path), "tupian").observeKt{
                    it.getOrNull()?.let {
                        tupianIfv.load(this@AddOrUpdateActivity, "file/"+it.file)
                        mShiwuxinxiItemBean.tupian = "file/" + it.file
                    }
                }
            }
        }
            wupinleixingBs.let { spinner ->
            spinner.setOnItemSelectedListener(object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    spinner.text = content
                    mShiwuxinxiItemBean.wupinleixing =content
                }
            })
            spinner.setOnClickListener {
                spinner.options.isNullOrEmpty().yes {
                    UserRepository.option("wupinleixing", "wupinleixing", "",null,"",false).observeKt{
                        it.getOrNull()?.let {
                            spinner.setOptions(it.data, "请选择物品类型", false)
                            spinner.dialogShow()
                        }
                    }
                }.otherwise {
                    spinner.dialogShow()
                }
            }
        }
            val mdiushishijianPicker = DatePicker(this@AddOrUpdateActivity).apply {
                wheelLayout.setDateFormatter(BirthdayFormatter())
                wheelLayout.setRange(DateEntity.target(1923, 1, 1),DateEntity.target(2050, 12, 31), DateEntity.today())
                setOnDatePickedListener { year, month, day ->
                    diushishijianTv.text = "$year-$month-$day"
                    mShiwuxinxiItemBean.diushishijian="$year-$month-$day"
                }
        }
            diushishijianTv.setOnClickListener {
            mdiushishijianPicker.show()
        }
            wupinzhuangtaiBs.setOptions("挂失,挂失找回".split(","),"请选择物品状态",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    wupinzhuangtaiBs.text = content
                    mShiwuxinxiItemBean.wupinzhuangtai =content
                }
            })
            submitBtn.setOnClickListener{/*提交*/
                submit()
            }
            setData()
    }

    lateinit var mUserBean:LinkedTreeMap<String, Any>/*当前用户数据*/

    override fun initData() {
        super.initData()
        UserRepository.session<Any>().observeKt {
            it.getOrNull()?.let {
                it.data.toConversion<LinkedTreeMap<String, Any>>()?.let {
                    mUserBean = it
                    it["touxiang"]?.let { it1 -> StorageUtil.encode(CommonBean.HEAD_URL_KEY, it1) }
                    /**ss读取*/
                    if (mShiwuxinxiItemBean.yonghuzhanghao.isNullOrEmpty()){
                        mShiwuxinxiItemBean.yonghuzhanghao = it["yonghuzhanghao"]?.toString()?:""
                    }
                    binding.yonghuzhanghaoEt.keyListener = null
                    if (mShiwuxinxiItemBean.yonghuming.isNullOrEmpty()){
                        mShiwuxinxiItemBean.yonghuming = it["yonghuming"]?.toString()?:""
                    }
                    binding.yonghumingEt.keyListener = null
                    if (mShiwuxinxiItemBean.lianxidianhua.isNullOrEmpty()){
                        mShiwuxinxiItemBean.lianxidianhua = it["lianxidianhua"]?.toString()?:""
                    }
                    binding.lianxidianhuaEt.keyListener = null
                    binding.setData()
                }
            }
        }

        (mId>0).yes {/*更新操作*/
            HomeRepository.info<ShiwuxinxiItemBean>("shiwuxinxi",mId).observeKt {
                it.getOrNull()?.let {
                    mShiwuxinxiItemBean = it.data
                    mShiwuxinxiItemBean.id = mId
                    binding.setData()
                }
            }
        }
        mShiwuxinxiItemBean.storeupnum = 0
        mShiwuxinxiItemBean.wupinzhuangtai = "挂失"
        binding.setData()
    }

    /**验证*/
    private fun ShiwuxinxiaddorupdateLayoutBinding.submit() {
        mShiwuxinxiItemBean.wupinmingcheng = wupinmingchengEt.text.toString()
        shuliangEt.inputType = InputType.TYPE_CLASS_NUMBER
        mShiwuxinxiItemBean.shuliang = shuliangEt.text.toString().toInt()
        mShiwuxinxiItemBean.diushididian = diushididianEt.text.toString()
        mShiwuxinxiItemBean.wupinmiaoshu = wupinmiaoshuRichLayout.richEt.html
        mShiwuxinxiItemBean.yonghuzhanghao = yonghuzhanghaoEt.text.toString()
        mShiwuxinxiItemBean.yonghuming = yonghumingEt.text.toString()
        mShiwuxinxiItemBean.lianxidianhua = lianxidianhuaEt.text.toString()
        storeupnumEt.inputType = InputType.TYPE_CLASS_NUMBER
        mShiwuxinxiItemBean.storeupnum = storeupnumEt.text.toString().toInt()
        addOrUpdate()

}
    private fun addOrUpdate(){/*更新或添加*/
        if (mShiwuxinxiItemBean.id>0){
            UserRepository.update("shiwuxinxi",mShiwuxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<ShiwuxinxiItemBean>("shiwuxinxi",mShiwuxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun ShiwuxinxiaddorupdateLayoutBinding.setData(){
        if (mShiwuxinxiItemBean.wupinmingcheng.isNotNullOrEmpty()){
            wupinmingchengEt.setText(mShiwuxinxiItemBean.wupinmingcheng.toString())
        }
        if (mShiwuxinxiItemBean.tupian.isNotNullOrEmpty()){
            tupianIfv.load(this@AddOrUpdateActivity, mShiwuxinxiItemBean.tupian)
        }
        if (mShiwuxinxiItemBean.wupinleixing.isNotNullOrEmpty()){
            wupinleixingBs.text =mShiwuxinxiItemBean.wupinleixing
        }
        if (mShiwuxinxiItemBean.shuliang>=0){
            shuliangEt.setText(mShiwuxinxiItemBean.shuliang.toString())
        }
        if (mShiwuxinxiItemBean.diushididian.isNotNullOrEmpty()){
            diushididianEt.setText(mShiwuxinxiItemBean.diushididian.toString())
        }
        diushishijianTv.text = mShiwuxinxiItemBean.diushishijian
        if (mShiwuxinxiItemBean.yonghuzhanghao.isNotNullOrEmpty()){
            yonghuzhanghaoEt.setText(mShiwuxinxiItemBean.yonghuzhanghao.toString())
        }
        if (mShiwuxinxiItemBean.yonghuming.isNotNullOrEmpty()){
            yonghumingEt.setText(mShiwuxinxiItemBean.yonghuming.toString())
        }
        if (mShiwuxinxiItemBean.lianxidianhua.isNotNullOrEmpty()){
            lianxidianhuaEt.setText(mShiwuxinxiItemBean.lianxidianhua.toString())
        }
        if (mShiwuxinxiItemBean.storeupnum>=0){
            storeupnumEt.setText(mShiwuxinxiItemBean.storeupnum.toString())
        }
        if (mShiwuxinxiItemBean.wupinzhuangtai.isNotNullOrEmpty()){
            wupinzhuangtaiBs.text =mShiwuxinxiItemBean.wupinzhuangtai
        }
        wupinmiaoshuRichLayout.richEt.setHtml(mShiwuxinxiItemBean.wupinmiaoshu)
    }
}