package com.design.appproject.logic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.design.appproject.bean.news.NewsItemBean
import com.design.appproject.logic.repository.HomeRepository

class HomeViewModel : ViewModel() {


    private val newsListData = MutableLiveData<Map<String, String>>()

    val newsListLiveData = Transformations.switchMap(newsListData) { request ->
        newsListData.value?.let {
            HomeRepository.list<NewsItemBean>("news", it)
        }
    }

    /**新闻列表*/
    fun newsList(map: Map<String, String>) {
        newsListData.value = map
    }
}