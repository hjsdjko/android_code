package com.design.appproject.ui.shiwuxinxi

import com.design.appproject.databinding.ShiwuxinxiFilterDialogLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.dylanc.viewbinding.inflate
import com.design.appproject.R
import com.design.appproject.logic.repository.UserRepository
import com.lxj.xpopup.core.DrawerPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.union.union_basic.ext.toConversion
import android.app.Activity
import com.blankj.utilcode.util.ScreenUtils
import com.union.union_basic.ext.isNotNullOrEmpty
import com.union.union_basic.ext.yes

class ListFilterDialog(context: Context) : DrawerPopupView(context) {

    lateinit var binding: ShiwuxinxiFilterDialogLayoutBinding

    override fun addInnerContent() {
        binding = drawerContentContainer.inflate()
    }

    private val params by lazy { /*请求参数*/
        mutableMapOf<String,String>()
    }
    var callBackListener: ((isEnsure:Boolean,params:MutableMap<String,String>?) -> Unit)? =null

    override fun onCreate() {
        super.onCreate()
        binding.apply {
            dialogNsv.layoutParams = dialogNsv.layoutParams.apply {
                width = ScreenUtils.getScreenWidth()
                height = ScreenUtils.getScreenHeight()
            }
            dialogFl.setOnClickListener { dismiss() }
            boxMll.setOnClickListener {  }
        val startPicker= com.github.gzuliyujiang.wheelpicker.DatimePicker(context as Activity).apply {
            wheelLayout.setDateFormatter(com.github.gzuliyujiang.wheelpicker.impl.BirthdayFormatter())
            wheelLayout.setTimeMode(com.github.gzuliyujiang.wheelpicker.annotation.TimeMode.NONE)
            wheelLayout.setRange(
                com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity.yearOnFuture(-100),
                com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity.yearOnFuture(50),
                com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity.now()
            )
            setOnDatimePickedListener { year, month, day, hour, minute, second ->
                diushishijianStartTv.text = "$year-$month-$day"
            }
        }
        diushishijianStartTv.setOnClickListener{
            startPicker.show()
        }
        val endPicker= com.github.gzuliyujiang.wheelpicker.DatimePicker(context as Activity).apply {
            wheelLayout.setDateFormatter(com.github.gzuliyujiang.wheelpicker.impl.BirthdayFormatter())
            wheelLayout.setTimeMode(com.github.gzuliyujiang.wheelpicker.annotation.TimeMode.NONE)
            wheelLayout.setRange(
                com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity.yearOnFuture(-100),
                com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity.yearOnFuture(50),
                com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity.now()
            )
            setOnDatimePickedListener { year, month, day, hour, minute, second ->
                diushishijianEndTv.text = "$year-$month-$day"
            }
        }
        diushishijianEndTv.setOnClickListener{
            endPicker.show()
        }
            resetBtn.setOnClickListener { //重置
                diushishijianStartTv.text=""
                diushishijianEndTv.text=""
                callBackListener?.invoke(false,null)
            }
            ensureBtn.setOnClickListener { //确定
                params.clear()
                diushishijianStartTv.text.toString().isNotNullOrEmpty().yes {
                    params.put("diushishijianstart",diushishijianStartTv.text.toString())
                }
                diushishijianEndTv.text.toString().isNotNullOrEmpty().yes {
                    params.put("diushishijianend",diushishijianEndTv.text.toString())
                }
                callBackListener?.invoke(true,params)
                dismiss()
            }
        }
    }

    private fun createCustomItemView(index:Int,title:String,viewGroup:ViewGroup,isSelect:Boolean=false):View= LayoutInflater.from(context).inflate(if (isSelect) R.layout.item_custom_active_layout else R.layout.item_custom_layout, viewGroup, false).apply{
        toConversion<TextView>()?.text = title
        isSelected=isSelect
        setOnClickListener {
            if (isSelected){
                return@setOnClickListener
            }else{
                val oldIndex= viewGroup.children.indexOfFirst { it.isSelected }
                if (oldIndex>=0){
                    val itemView = viewGroup.children.find { it.isSelected }
                    val oldTitle = itemView?.toConversion<TextView>()?.text.toString()?:""
                    viewGroup.removeViewAt(oldIndex)
                    val view = createCustomItemView(oldIndex,oldTitle,viewGroup)
                    viewGroup.addView(view,oldIndex)
                }
                viewGroup.removeViewAt(index)
                viewGroup.addView(createCustomItemView(index,title,viewGroup,true),index)
            }
        }
    }
}