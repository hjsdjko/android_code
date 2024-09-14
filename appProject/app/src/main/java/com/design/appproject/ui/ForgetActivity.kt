package com.design.appproject.ui

import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.children
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.BaseBindingActivity
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.ActivityForgetLayoutBinding
import com.design.appproject.logic.viewmodel.ForgetViewModel
import com.design.appproject.widget.BottomSpinner
import com.union.union_basic.ext.showToast
import com.union.union_basic.ext.toConversion
import com.union.union_basic.ext.yes

/**忘记密码*/
@Route(path = CommonArouteApi.PATH_ACTIVITY_FORGET)
class ForgetActivity : BaseBindingActivity<ActivityForgetLayoutBinding>() {

    /**进行到的步骤*/
    var mStep = 1

    val mForgetViewModel by viewModels<ForgetViewModel>()

    var tableName=""
    var panswer=""/**密保答案*/

    override fun initEvent() {
        binding.apply {
            setBarTitle("忘记密码")
            setBarColor("#FFFFFF","black")
            updateView()
            initUsertypeTv()
            nextBtn.setOnClickListener {
                verify().yes {
                    when (mStep) {
                        2 -> {
                            "验证成功".showToast()
                            mStep = 3
                            updateView()
                        }
                        3 -> {
                            showLoading()
                            when(tableName){
                            }
                        }
                        else -> {
                            showLoading()
                            when(tableName){
                            }
                        }
                    }
                }
            }
        }
    }

    private fun ActivityForgetLayoutBinding.initUsertypeTv() {
        val roleNameList = mutableMapOf<String,String>()
        usertypeBs.setOptions(roleNameList.keys.toList(), "请选择登录用户类型",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    usertypeBs.text = content
                    tableName = roleNameList.getValue(content)
                }
            })
    }

    private fun ActivityForgetLayoutBinding.updateView() {
        resetpwdMyl.toConversion<ViewGroup>()?.children?.forEach {
            it.isVisible = it.tag == "step${mStep}"
        }
        nextBtn.isVisible = true
        nextBtn.text = when (mStep) {
            2 -> "确定"
            3 -> "修改密码"
            else -> "下一步"
        }
    }

    private fun ActivityForgetLayoutBinding.verify(): Boolean {
        if (mStep == 1) {
            usernameEt.text.isNullOrEmpty().yes {
                "请输入您的账号".showToast()
                return false
            }
            if (usertypeBs.text == "请选择登录用户类型") {
                "请选择登录用户类型".showToast()
                return false
            }
        } else if (mStep == 2) {
            if (panswerEt.text.toString() != panswer){
                "密保答案错误".showToast()
                return false
            }
        } else {
            passwordEt.text.toString().isNullOrEmpty().yes {
                "密码不能为空".showToast()
                return false
            }
            if (passwordEt.text.toString() != ensurePasswordEt.text.toString()) {
                "两次密码输入不一致".showToast()
                return false
            }
        }
        return true
    }

    override fun initData() {
        super.initData()

        mForgetViewModel.updateLiveData.observeKt {
            it.getOrNull()?.let {
                "密码修改成功".showToast()
                finish()
            }
        }
    }

}