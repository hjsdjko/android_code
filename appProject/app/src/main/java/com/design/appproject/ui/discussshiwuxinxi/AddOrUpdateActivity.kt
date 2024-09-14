package com.design.appproject.ui.discussshiwuxinxi

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
import com.design.appproject.bean.DiscussshiwuxinxiItemBean
import com.design.appproject.databinding.DiscussshiwuxinxiaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 失物信息评论表新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_DISCUSSSHIWUXINXI)
class AddOrUpdateActivity:BaseBindingActivity<DiscussshiwuxinxiaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mRefid: Long = 0 /*refid数据*/

    /**上传数据*/
    var mDiscussshiwuxinxiItemBean = DiscussshiwuxinxiItemBean()

    override fun initEvent() {
        setBarTitle("失物信息评论表")
        setBarColor("#FFFFFF","black")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mDiscussshiwuxinxiItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mDiscussshiwuxinxiItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mDiscussshiwuxinxiItemBean,mRefid)
                }
            }
            if (mDiscussshiwuxinxiItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mDiscussshiwuxinxiItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mDiscussshiwuxinxiItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mDiscussshiwuxinxiItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mDiscussshiwuxinxiItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mDiscussshiwuxinxiItemBean,Utils.getUserId())
            }
        }
        binding.initView()

    }

    fun DiscussshiwuxinxiaddorupdateLayoutBinding.initView(){
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
                    binding.setData()
                }
            }
        }

        (mId>0).yes {/*更新操作*/
            HomeRepository.info<DiscussshiwuxinxiItemBean>("discussshiwuxinxi",mId).observeKt {
                it.getOrNull()?.let {
                    mDiscussshiwuxinxiItemBean = it.data
                    mDiscussshiwuxinxiItemBean.id = mId
                    binding.setData()
                }
            }
        }
        binding.setData()
    }

    /**验证*/
    private fun DiscussshiwuxinxiaddorupdateLayoutBinding.submit() {
        mDiscussshiwuxinxiItemBean.content = contentEt.text.toString()
        mDiscussshiwuxinxiItemBean.avatarurl = StorageUtil.decodeString(CommonBean.HEAD_URL_KEY)?:""
        if(mDiscussshiwuxinxiItemBean.refid<=0){
            "关联表id不能为空".showToast()
            return
        }
        if(mDiscussshiwuxinxiItemBean.userid<=0){
            "用户id不能为空".showToast()
            return
        }
        if(mDiscussshiwuxinxiItemBean.content.isNullOrEmpty()){
            "评论内容不能为空".showToast()
            return
        }
        addOrUpdate()

}
    private fun addOrUpdate(){/*更新或添加*/
        if (mDiscussshiwuxinxiItemBean.id>0){
            UserRepository.update("discussshiwuxinxi",mDiscussshiwuxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                mDiscussshiwuxinxiItemBean.avatarurl = StorageUtil.decodeString(CommonBean.HEAD_URL_KEY)?:""
                setResult(RESULT_OK)
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<DiscussshiwuxinxiItemBean>("discussshiwuxinxi",mDiscussshiwuxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                mDiscussshiwuxinxiItemBean.avatarurl = StorageUtil.decodeString(CommonBean.HEAD_URL_KEY)?:""
                setResult(RESULT_OK)
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun DiscussshiwuxinxiaddorupdateLayoutBinding.setData(){
        if (mDiscussshiwuxinxiItemBean.content.isNotNullOrEmpty()){
            contentEt.setText(mDiscussshiwuxinxiItemBean.content.toString())
        }
    }
}