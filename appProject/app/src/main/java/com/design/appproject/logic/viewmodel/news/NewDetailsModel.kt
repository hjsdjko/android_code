package com.design.appproject.logic.viewmodel.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.bean.news.NewsItemBean

class NewDetailsModel: ViewModel() {

    private val newsInfoData = MutableLiveData<Long>()

    val newsInfoLiveData = Transformations.switchMap(newsInfoData) { request ->
        newsInfoData.value?.let {
            HomeRepository.info<NewsItemBean>("news", it)
        }
    }

    /**关于我们*/
    fun newsInfo(id:Long) {
        newsInfoData.value = id
    }
}