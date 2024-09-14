package com.design.appproject.logic.viewmodel.messages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.design.appproject.bean.messages.MessagesItemBean
import com.design.appproject.logic.repository.HomeRepository

/**
 * 留言板
 */
class MessagesModel: ViewModel() {

    private val messagesListData = MutableLiveData<Int>()

    val messagesLiveData = Transformations.switchMap(messagesListData) { request ->
        messagesListData.value?.let {
            HomeRepository.list<MessagesItemBean>("messages", mapOf("page" to it.toString(),"limit" to "20"
                ,"sort" to "addtime","order" to "desc"))
        }
    }

    /**留言板列表*/
    fun messagesList(page:Int) {
        messagesListData.value = page
    }

    private val addMessagesData = MutableLiveData<MessagesItemBean>()

    val addMessagesLiveData = Transformations.switchMap(addMessagesData) { request ->
        addMessagesData.value?.let {
            HomeRepository.add<MessagesItemBean>("messages", it)
        }
    }

    /**添加留言*/
    fun addMessages(messageItemBean:MessagesItemBean) {
        addMessagesData.value = messageItemBean
    }

}