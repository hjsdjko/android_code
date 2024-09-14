package com.design.appproject.widget

import android.content.Context
import android.view.View
import com.design.appproject.databinding.ListFilterDialogLayoutBinding
import com.dylanc.viewbinding.inflate
import com.lxj.xpopup.core.DrawerPopupView
import com.lxj.xpopup.util.XPopupUtils

class ListFilterdialog(context: Context) : DrawerPopupView(context) {

    lateinit var binding: ListFilterDialogLayoutBinding

    var filterContentView:View? = null

    override fun addInnerContent() {
        binding = drawerContentContainer.inflate()
    }

    var callBackListener: ((isEnsure:Boolean) -> Unit)? =null

    override fun onCreate() {
        super.onCreate()
        binding.apply {
            resetBtn.setOnClickListener { //重置
                callBackListener?.invoke(false)
            }
            ensureBtn.setOnClickListener { //确定
                callBackListener?.invoke(true)
                dismiss()
            }
            filterContentView?.let { filterSv.addView(it) }
        }
    }

    override fun getMaxWidth(): Int {
        return (XPopupUtils.getScreenWidth(context)*0.85).toInt()
    }

    override fun getPopupWidth(): Int {
        return (XPopupUtils.getScreenWidth(context)*0.85).toInt()
    }

    override fun doAfterShow() {
        super.doAfterShow()

    }


}