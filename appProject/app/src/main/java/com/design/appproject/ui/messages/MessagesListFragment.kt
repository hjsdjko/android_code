package com.design.appproject.ui.messages

import androidx.fragment.app.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.design.appproject.base.BaseBindingFragment
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.ActivityMessageListLayoutBinding
import com.design.appproject.logic.viewmodel.messages.MessagesModel

/**
 * 留言板列表界面
 */
@Route(path = CommonArouteApi.PATH_FRAGMENT_LIST_MESSAGES)
class MessagesListFragment : BaseBindingFragment<ActivityMessageListLayoutBinding>() {

    @JvmField
    @Autowired
    var mHasBack: Boolean = true /*是否有返回按钮*/

    private val mMessagesModel by viewModels<MessagesModel>()

    override fun initEvent() {
        binding.apply {
            setBarTitle("留言板",mHasBack)
            messageSrv.setOnRefreshListener { mMessagesModel.messagesList(1) }
            messageSrv.setAdapter(MessageListAdapter().apply {
                pageLoadMoreListener {
                    mMessagesModel.messagesList(it)
                }
            })

            addMessageBtn.setOnClickListener {
                ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADD_MESSAGES).navigation()
            }
        }
    }

    override fun initData() {
        super.initData()
        mMessagesModel.messagesList(1)
        mMessagesModel.messagesLiveData.observeKt(errorBlock = {
            binding.messageSrv.isRefreshing = false
        }) {
            it.getOrNull()?.let {
                binding.messageSrv.setData(it.data.list, it.data.total)
            }
        }
    }
}