package com.design.appproject.ui

import android.content.Context
import com.design.appproject.databinding.DialogChangePasswordLayoutBinding
import com.dylanc.viewbinding.inflate
import com.lxj.xpopup.core.BottomPopupView
import com.union.union_basic.ext.showToast

class ChangePassWordDialog(context: Context,oldPassword:String) : BottomPopupView(context) {

    lateinit var binding: DialogChangePasswordLayoutBinding

    private var mOldPassword = oldPassword


    var mUpdateListener: ((String) -> Unit)? =null

    override fun addInnerContent() {
        binding = bottomPopupContainer.inflate()
    }

    override fun onCreate() {
        super.onCreate()
        binding.apply {
            updateBtn.setOnClickListener {
                if (oldPassowrdEt.text.toString() != mOldPassword){
                    "原密码不正确！".showToast()
                    return@setOnClickListener
                }
                if (newPassowrdEt.text.toString().length<8){
                    "新密码长度不小于8位！".showToast()
                    return@setOnClickListener
                }
                if (newPassowrdEt.text.toString() != ensurePassowrdEt.text.toString()){
                    "两次密码不一致".showToast()
                    return@setOnClickListener
                }
                mUpdateListener?.invoke(newPassowrdEt.text.toString())
            }
        }
    }
}