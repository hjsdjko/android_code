package com.design.appproject.ui.shiquxinxi

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
import com.design.appproject.bean.ShiquxinxiItemBean
import com.design.appproject.databinding.ShiquxinxiaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 拾取信息新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_SHIQUXINXI)
class AddOrUpdateActivity:BaseBindingActivity<ShiquxinxiaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mRefid: Long = 0 /*refid数据*/

    /**上传数据*/
    var mShiquxinxiItemBean = ShiquxinxiItemBean()

    override fun initEvent() {
        setBarTitle("拾取信息")
        setBarColor("#FFFFFF","black")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mShiquxinxiItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mShiquxinxiItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mShiquxinxiItemBean,mRefid)
                }
            }
            if (mShiquxinxiItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mShiquxinxiItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mShiquxinxiItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mShiquxinxiItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mShiquxinxiItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mShiquxinxiItemBean,Utils.getUserId())
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

    fun ShiquxinxiaddorupdateLayoutBinding.initView(){
             tupianLl.setOnClickListener {
            SmartPictureSelector.openPicture(this@AddOrUpdateActivity) {
                val path = it[0]
                showLoading("上传中...")
                UserRepository.upload(File(path), "tupian").observeKt{
                    it.getOrNull()?.let {
                        tupianIfv.load(this@AddOrUpdateActivity, "file/"+it.file)
                        mShiquxinxiItemBean.tupian = "file/" + it.file
                    }
                }
            }
        }
            wupinleixingBs.let { spinner ->
            spinner.setOnItemSelectedListener(object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    spinner.text = content
                    mShiquxinxiItemBean.wupinleixing =content
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
            mShiquxinxiItemBean.shiqushijian = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd"))
            shiqushijianTv.text = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd"))
            val mshiqushijianPicker = DatePicker(this@AddOrUpdateActivity).apply {
                wheelLayout.setDateFormatter(BirthdayFormatter())
                wheelLayout.setRange(DateEntity.target(1923, 1, 1),DateEntity.target(2050, 12, 31), DateEntity.today())
                setOnDatePickedListener { year, month, day ->
                    shiqushijianTv.text = "$year-$month-$day"
                    mShiquxinxiItemBean.shiqushijian="$year-$month-$day"
                }
        }
            shiqushijianTv.setOnClickListener {
            mshiqushijianPicker.show()
        }
            wupinzhuangtaiBs.setOptions("已认领,未认领".split(","),"请选择物品状态",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    wupinzhuangtaiBs.text = content
                    mShiquxinxiItemBean.wupinzhuangtai =content
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
                    binding.setData()
                }
            }
        }

        (mId>0).yes {/*更新操作*/
            HomeRepository.info<ShiquxinxiItemBean>("shiquxinxi",mId).observeKt {
                it.getOrNull()?.let {
                    mShiquxinxiItemBean = it.data
                    mShiquxinxiItemBean.id = mId
                    binding.setData()
                }
            }
        }
        mShiquxinxiItemBean.storeupnum = 0
        mShiquxinxiItemBean.wupinzhuangtai = "未认领"
        binding.setData()
    }

    /**验证*/
    private fun ShiquxinxiaddorupdateLayoutBinding.submit() {
        mShiquxinxiItemBean.wupinmingcheng = wupinmingchengEt.text.toString()
        shuliangEt.inputType = InputType.TYPE_CLASS_NUMBER
        mShiquxinxiItemBean.shuliang = shuliangEt.text.toString().toInt()
        mShiquxinxiItemBean.shiqudidian = shiqudidianEt.text.toString()
        mShiquxinxiItemBean.wupinmiaoshu = wupinmiaoshuRichLayout.richEt.html
        mShiquxinxiItemBean.shiquzhexingming = shiquzhexingmingEt.text.toString()
        mShiquxinxiItemBean.shiquzhedianhua = shiquzhedianhuaEt.text.toString()
        storeupnumEt.inputType = InputType.TYPE_CLASS_NUMBER
        mShiquxinxiItemBean.storeupnum = storeupnumEt.text.toString().toInt()
        RegexUtils.isMobileExact(mShiquxinxiItemBean.shiquzhedianhua).no {
            "拾取者电话应输入手机格式".showToast()
            return
        }
        addOrUpdate()

}
    private fun addOrUpdate(){/*更新或添加*/
        if (mShiquxinxiItemBean.id>0){
            UserRepository.update("shiquxinxi",mShiquxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<ShiquxinxiItemBean>("shiquxinxi",mShiquxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun ShiquxinxiaddorupdateLayoutBinding.setData(){
        if (mShiquxinxiItemBean.wupinmingcheng.isNotNullOrEmpty()){
            wupinmingchengEt.setText(mShiquxinxiItemBean.wupinmingcheng.toString())
        }
        if (mShiquxinxiItemBean.tupian.isNotNullOrEmpty()){
            tupianIfv.load(this@AddOrUpdateActivity, mShiquxinxiItemBean.tupian)
        }
        if (mShiquxinxiItemBean.wupinleixing.isNotNullOrEmpty()){
            wupinleixingBs.text =mShiquxinxiItemBean.wupinleixing
        }
        if (mShiquxinxiItemBean.shuliang>=0){
            shuliangEt.setText(mShiquxinxiItemBean.shuliang.toString())
        }
        if (mShiquxinxiItemBean.shiqudidian.isNotNullOrEmpty()){
            shiqudidianEt.setText(mShiquxinxiItemBean.shiqudidian.toString())
        }
        if (mShiquxinxiItemBean.shiquzhexingming.isNotNullOrEmpty()){
            shiquzhexingmingEt.setText(mShiquxinxiItemBean.shiquzhexingming.toString())
        }
        if (mShiquxinxiItemBean.shiquzhedianhua.isNotNullOrEmpty()){
            shiquzhedianhuaEt.setText(mShiquxinxiItemBean.shiquzhedianhua.toString())
        }
        if (mShiquxinxiItemBean.storeupnum>=0){
            storeupnumEt.setText(mShiquxinxiItemBean.storeupnum.toString())
        }
        if (mShiquxinxiItemBean.wupinzhuangtai.isNotNullOrEmpty()){
            wupinzhuangtaiBs.text =mShiquxinxiItemBean.wupinzhuangtai
        }
        wupinmiaoshuRichLayout.richEt.setHtml(mShiquxinxiItemBean.wupinmiaoshu)
    }
}