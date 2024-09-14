package com.design.appproject.logic.viewmodel.yonghu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.design.appproject.bean.YonghuItemBean
import com.design.appproject.logic.repository.UserRepository
import java.io.File

/**
 *  Yonghu注册viewmodel类
 */
class RegisterViewModel : ViewModel() {
    private val optionData = MutableLiveData<List<Any?>>()

    val optionLiveData = Transformations.switchMap(optionData) { request ->
        optionData.value?.let {
            UserRepository.option(
                it[0] as String, it[1] as String, it[2] as String?,
                it[3] as String?, it[4] as String, it[5] as Boolean
            )
        }
    }

    fun option(
        tableName: String,
        columnName: String,
        tag: String,
        isMultiSelect: Boolean,
        conditionColumn: String? = null,
        conditionValue: String? = null
    ) {
        /**联运查询*/
        optionData.value =
            listOf(tableName, columnName, conditionColumn, conditionValue, tag, isMultiSelect)
    }
    private val uploadData = MutableLiveData<List<Any>>()

    val uploadLiveData = Transformations.switchMap(uploadData) { request ->
        uploadData.value?.let {
            UserRepository.upload(it[0] as File, it[1] as String)
        }
    }

    /**上传文件*/
    fun upload(file: File, tag: String="") {
        uploadData.value = listOf(file, tag)
    }

    private val registerData = MutableLiveData<List<Any>>()

    val registerLiveData = Transformations.switchMap(registerData) { request ->
        registerData.value?.let {
            UserRepository.register(it[0] as String, it[1] as YonghuItemBean)
        }
    }

    /**注册*/
    fun register(tableName: String, body:YonghuItemBean) {
        registerData.value = listOf(tableName, body)
    }

}