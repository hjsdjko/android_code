package com.design.appproject.ui.messages

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.BaseBindingActivity
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.base.CommonBean
import com.design.appproject.bean.messages.MessagesItemBean
import com.design.appproject.databinding.ActivityAddMessagesLayoutBinding
import com.design.appproject.ext.load
import com.design.appproject.logic.repository.UserRepository
import com.design.appproject.logic.viewmodel.messages.MessagesModel
import com.design.appproject.utils.Utils
import com.union.union_basic.ext.showToast
import com.union.union_basic.image.selector.SmartPictureSelector
import com.union.union_basic.utils.StorageUtil
import java.io.File

/**
 * 添加留言界面
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADD_MESSAGES)
class AddOrUpdateActivity: BaseBindingActivity<ActivityAddMessagesLayoutBinding>() {

    private val mMessagesModel by viewModels<MessagesModel>()

    override fun initEvent() {
        binding.apply {
            setBarTitle("添加留言")
            imageLlc.setOnClickListener {
                SmartPictureSelector.openPicture(this@AddOrUpdateActivity){
                    val path = it[0]
                    showLoading("发送中...")
                    UserRepository.upload(File(path),"").observeKt {
                        it.getOrNull()?.let {
                            messageQriv.load(this@AddOrUpdateActivity,"file/"+it.file)
                            messageQriv.tag = "file/"+it.file
                        }
                    }
                }
            }
            usernameTv.text = StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:""
            submitBtn.setOnClickListener {
               var content = contentTv.text.toString()
                if (content.isNullOrEmpty()){
                    "请填写留言内容".showToast()
                }else{
                    showLoading("提交中...")

                  val message = MessagesItemBean(
                        avatarurl = StorageUtil.decodeString(CommonBean.HEAD_URL_KEY)?:"",
                        userid = Utils.getUserId(),
                        username = StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"",
                        content = content)
                    if ( messageQriv.tag!=null){
                        message.cpicture = messageQriv.tag.toString()
                    }
                    mMessagesModel.addMessages(message)
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        mMessagesModel.addMessagesLiveData.observeKt {
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
    }
}