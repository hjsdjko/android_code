package com.design.appproject.ui.renlingjilu

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
import com.design.appproject.bean.RenlingjiluItemBean
import com.design.appproject.ext.afterTextChanged
import com.design.appproject.bean.ShiquxinxiItemBean
import com.design.appproject.databinding.RenlingjiluaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 认领记录新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_RENLINGJILU)
class AddOrUpdateActivity:BaseBindingActivity<RenlingjiluaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mCrossTable: String = "" /*跨表表名*/

    @JvmField
    @Autowired
    var mCrossObj: ShiquxinxiItemBean = ShiquxinxiItemBean() /*跨表表内容*/

    @JvmField
    @Autowired
    var mStatusColumnName: String = "" /*列名*/

    @JvmField
    @Autowired
    var mStatusColumnValue: String = "" /*列值*/

    @JvmField
    @Autowired
    var mTips: String = "" /*提示*/
    @JvmField
    @Autowired
    var mRefid: Long = 0 /*refid数据*/

    /**上传数据*/
    var mRenlingjiluItemBean = RenlingjiluItemBean()

    override fun initEvent() {
        setBarTitle("认领记录")
        setBarColor("#FFFFFF","black")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mRenlingjiluItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mRenlingjiluItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mRenlingjiluItemBean,mRefid)
                }
            }
            if (mRenlingjiluItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mRenlingjiluItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mRenlingjiluItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mRenlingjiluItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mRenlingjiluItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mRenlingjiluItemBean,Utils.getUserId())
            }
        }
        binding.initView()

    }

    fun RenlingjiluaddorupdateLayoutBinding.initView(){
            wupinleixingBs.let { spinner ->
            spinner.setOnItemSelectedListener(object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    spinner.text = content
                    mRenlingjiluItemBean.wupinleixing =content
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
            mRenlingjiluItemBean.renlingshijian = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd"))
            renlingshijianTv.text = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd"))
            val mrenlingshijianPicker = DatePicker(this@AddOrUpdateActivity).apply {
                wheelLayout.setDateFormatter(BirthdayFormatter())
                wheelLayout.setRange(DateEntity.target(1923, 1, 1),DateEntity.target(2050, 12, 31), DateEntity.today())
                setOnDatePickedListener { year, month, day ->
                    renlingshijianTv.text = "$year-$month-$day"
                    mRenlingjiluItemBean.renlingshijian="$year-$month-$day"
                }
        }
            renlingshijianTv.setOnClickListener {
            mrenlingshijianPicker.show()
        }
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
                    if (mRenlingjiluItemBean.yonghuzhanghao.isNullOrEmpty()){
                        mRenlingjiluItemBean.yonghuzhanghao = it["yonghuzhanghao"]?.toString()?:""
                    }
                    binding.yonghuzhanghaoEt.keyListener = null
                    if (mRenlingjiluItemBean.yonghuming.isNullOrEmpty()){
                        mRenlingjiluItemBean.yonghuming = it["yonghuming"]?.toString()?:""
                    }
                    binding.yonghumingEt.keyListener = null
                    binding.setData()
                }
            }
        }

        (mId>0).yes {/*更新操作*/
            HomeRepository.info<RenlingjiluItemBean>("renlingjilu",mId).observeKt {
                it.getOrNull()?.let {
                    mRenlingjiluItemBean = it.data
                    mRenlingjiluItemBean.id = mId
                    binding.setData()
                }
            }
        }
        if (mCrossTable.isNotNullOrEmpty()){/*跨表*/
            mCrossObj.javaClass.declaredFields.any{it.name == "shiquzhexingming"}.yes {
                mRenlingjiluItemBean.shiquzhexingming = mCrossObj.javaClass.getDeclaredField("shiquzhexingming").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "shiquzhedianhua"}.yes {
                mRenlingjiluItemBean.shiquzhedianhua = mCrossObj.javaClass.getDeclaredField("shiquzhedianhua").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "wupinmingcheng"}.yes {
                mRenlingjiluItemBean.wupinmingcheng = mCrossObj.javaClass.getDeclaredField("wupinmingcheng").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "wupinleixing"}.yes {
                mRenlingjiluItemBean.wupinleixing = mCrossObj.javaClass.getDeclaredField("wupinleixing").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "shuliang"}.yes {
                mRenlingjiluItemBean.shuliang = mCrossObj.javaClass.getDeclaredField("shuliang").also { it.isAccessible=true }.get(mCrossObj) as Int
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "renlingpingzheng"}.yes {
                mRenlingjiluItemBean.renlingpingzheng = mCrossObj.javaClass.getDeclaredField("renlingpingzheng").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yonghuzhanghao"}.yes {
                mRenlingjiluItemBean.yonghuzhanghao = mCrossObj.javaClass.getDeclaredField("yonghuzhanghao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yonghuming"}.yes {
                mRenlingjiluItemBean.yonghuming = mCrossObj.javaClass.getDeclaredField("yonghuming").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "renlingshijian"}.yes {
                mRenlingjiluItemBean.renlingshijian = mCrossObj.javaClass.getDeclaredField("renlingshijian").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
        }
        binding.setData()
    }

    /**验证*/
    private fun RenlingjiluaddorupdateLayoutBinding.submit() {
        mRenlingjiluItemBean.shiquzhexingming = shiquzhexingmingEt.text.toString()
        mRenlingjiluItemBean.shiquzhedianhua = shiquzhedianhuaEt.text.toString()
        mRenlingjiluItemBean.wupinmingcheng = wupinmingchengEt.text.toString()
        shuliangEt.inputType = InputType.TYPE_CLASS_NUMBER
        mRenlingjiluItemBean.shuliang = shuliangEt.text.toString().toInt()
        mRenlingjiluItemBean.renlingpingzheng = renlingpingzhengEt.text.toString()
        mRenlingjiluItemBean.yonghuzhanghao = yonghuzhanghaoEt.text.toString()
        mRenlingjiluItemBean.yonghuming = yonghumingEt.text.toString()
        var crossuserid:Long = 0
        var crossrefid:Long = 0
        var crossoptnum:Int = 0
        if (mStatusColumnName.isNotNullOrEmpty()){
            if (!mStatusColumnName.startsWith("[")){
                mCrossObj.javaClass.declaredFields.any{it.name == mStatusColumnName}.yes {
                    mCrossObj.javaClass.getDeclaredField(mStatusColumnName).also { it.isAccessible=true }.set(mCrossObj,mStatusColumnValue)
                    UserRepository.update(mCrossTable,mCrossObj).observeForever {  }
                }
            }else{
                crossuserid = Utils.getUserId()
                mCrossObj.javaClass.declaredFields.any{it.name == "id"}.yes {
                    crossrefid =mCrossObj.javaClass.getDeclaredField("id").also { it.isAccessible=true }.get(mCrossObj).toString().toLong()
                }
                crossoptnum = mStatusColumnName.replace("[","").replace("]","").toIntOrNull()?:0
            }
        }

        if (crossuserid>0 && crossrefid>0){
            mRenlingjiluItemBean.javaClass.declaredFields.any{it.name == "crossuserid"}.yes {
                mRenlingjiluItemBean.javaClass.getDeclaredField("crossuserid").also { it.isAccessible=true }.set(mRenlingjiluItemBean,crossuserid)
            }
            mRenlingjiluItemBean.javaClass.declaredFields.any{it.name == "crossrefid"}.yes {
                mRenlingjiluItemBean.javaClass.getDeclaredField("crossrefid").also { it.isAccessible=true }.set(mRenlingjiluItemBean,crossrefid)
            }
            HomeRepository.list<RenlingjiluItemBean>("renlingjilu", mapOf("page" to "1","limit" to "10","crossuserid" to crossuserid.toString(),"crossrefid" to crossrefid.toString())).observeKt{
                it.getOrNull()?.let {
                    if (it.data.list.size>=crossoptnum){
                        mTips.showToast()
                    }else{
                        crossCal()
                    }
                }
            }
        }else{
            crossCal()
        }

}
    private fun crossCal(){/*更新跨表数据*/
        addOrUpdate()
    }
    private fun addOrUpdate(){/*更新或添加*/
        if (mRenlingjiluItemBean.id>0){
            UserRepository.update("renlingjilu",mRenlingjiluItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<RenlingjiluItemBean>("renlingjilu",mRenlingjiluItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun RenlingjiluaddorupdateLayoutBinding.setData(){
        if (mRenlingjiluItemBean.shiquzhexingming.isNotNullOrEmpty()){
            shiquzhexingmingEt.setText(mRenlingjiluItemBean.shiquzhexingming.toString())
        }
        if (mRenlingjiluItemBean.shiquzhedianhua.isNotNullOrEmpty()){
            shiquzhedianhuaEt.setText(mRenlingjiluItemBean.shiquzhedianhua.toString())
        }
        if (mRenlingjiluItemBean.wupinmingcheng.isNotNullOrEmpty()){
            wupinmingchengEt.setText(mRenlingjiluItemBean.wupinmingcheng.toString())
        }
        if (mRenlingjiluItemBean.wupinleixing.isNotNullOrEmpty()){
            wupinleixingBs.text =mRenlingjiluItemBean.wupinleixing
        }
        if (mRenlingjiluItemBean.shuliang>=0){
            shuliangEt.setText(mRenlingjiluItemBean.shuliang.toString())
        }
        if (mRenlingjiluItemBean.yonghuzhanghao.isNotNullOrEmpty()){
            yonghuzhanghaoEt.setText(mRenlingjiluItemBean.yonghuzhanghao.toString())
        }
        if (mRenlingjiluItemBean.yonghuming.isNotNullOrEmpty()){
            yonghumingEt.setText(mRenlingjiluItemBean.yonghuming.toString())
        }
        if (mRenlingjiluItemBean.renlingpingzheng.isNotNullOrEmpty()){
            renlingpingzhengEt.setText(mRenlingjiluItemBean.renlingpingzheng.toString())
        }
    }
}