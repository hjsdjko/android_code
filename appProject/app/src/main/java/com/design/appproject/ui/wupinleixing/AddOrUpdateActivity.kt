package com.design.appproject.ui.wupinleixing

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
import com.design.appproject.bean.WupinleixingItemBean
import com.design.appproject.databinding.WupinleixingaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 物品类型新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_WUPINLEIXING)
class AddOrUpdateActivity:BaseBindingActivity<WupinleixingaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mRefid: Long = 0 /*refid数据*/

    /**上传数据*/
    var mWupinleixingItemBean = WupinleixingItemBean()

    override fun initEvent() {
        setBarTitle("物品类型")
        setBarColor("#FFFFFF","black")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mWupinleixingItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mWupinleixingItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mWupinleixingItemBean,mRefid)
                }
            }
            if (mWupinleixingItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mWupinleixingItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mWupinleixingItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mWupinleixingItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mWupinleixingItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mWupinleixingItemBean,Utils.getUserId())
            }
        }
        binding.initView()

    }

    fun WupinleixingaddorupdateLayoutBinding.initView(){
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
            HomeRepository.info<WupinleixingItemBean>("wupinleixing",mId).observeKt {
                it.getOrNull()?.let {
                    mWupinleixingItemBean = it.data
                    mWupinleixingItemBean.id = mId
                    binding.setData()
                }
            }
        }
        binding.setData()
    }

    /**验证*/
    private fun WupinleixingaddorupdateLayoutBinding.submit() {
        mWupinleixingItemBean.wupinleixing = wupinleixingEt.text.toString()
        addOrUpdate()

}
    private fun addOrUpdate(){/*更新或添加*/
        if (mWupinleixingItemBean.id>0){
            UserRepository.update("wupinleixing",mWupinleixingItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<WupinleixingItemBean>("wupinleixing",mWupinleixingItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun WupinleixingaddorupdateLayoutBinding.setData(){
        if (mWupinleixingItemBean.wupinleixing.isNotNullOrEmpty()){
            wupinleixingEt.setText(mWupinleixingItemBean.wupinleixing.toString())
        }
    }
}