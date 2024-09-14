package com.design.appproject.logic.viewmodel.yonghu

import com.design.appproject.logic.repository.UserRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.bean.YonghuItemBean
/**
 * 用户viewmodel
 * */
class DetailsViewModel : ViewModel() {

    private val infoData = MutableLiveData<List<String>>()

    val infoLiveData = Transformations.switchMap(infoData) { request ->
        infoData.value?.let {
            HomeRepository.info<YonghuItemBean>(it[0], it[1].toLong())
        }
    }

    fun info(tableName: String, id: String) {
        infoData.value = listOf(tableName, id)
    }

    private val updateData = MutableLiveData<List<Any>>()

    val updateLiveData = Transformations.switchMap(updateData) { request ->
        updateData.value?.let {
            UserRepository.update(it[0] as String,it[1],it[2] as String)
        }
    }

    fun update(tableName: String,body: Any,tag:String="") {
        updateData.value = listOf(tableName,body,tag)
    }

}