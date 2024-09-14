package com.design.appproject.logic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.design.appproject.logic.repository.UserRepository

/**忘记密码*/
class ForgetViewModel: ViewModel() {


    private val updateData = MutableLiveData<List<Any>>()

    val updateLiveData = Transformations.switchMap(updateData) { request ->
        updateData.value?.let {
            UserRepository.update(it[0] as String,it[1])
        }
    }

    fun update(tableName: String,body: Any) {
        updateData.value = listOf(tableName,body)
    }
}