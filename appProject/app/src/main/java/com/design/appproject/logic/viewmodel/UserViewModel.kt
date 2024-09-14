package com.design.appproject.logic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.design.appproject.logic.repository.UserRepository
import java.io.File

class UserViewModel : ViewModel() {

    private val loginData = MutableLiveData<List<String>>()

    val loginLiveData = Transformations.switchMap(loginData) { request ->
        loginData.value?.let {
            UserRepository.login(it[0], it[1])
        }
    }

    fun login(userName: String, passWord: String) {
        /**登录*/
        loginData.value = listOf(userName, passWord)
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

    private val faceLoginData = MutableLiveData<String>()

    val faceLoginLiveData = Transformations.switchMap(faceLoginData) { request ->
        faceLoginData.value?.let {
            UserRepository.faceLogin(it)
        }
    }

    /**人脸识别登录*/
    fun faceLogin(face: String) {
        faceLoginData.value = face
    }
}