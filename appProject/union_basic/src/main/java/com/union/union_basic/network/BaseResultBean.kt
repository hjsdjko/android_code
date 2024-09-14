package com.union.union_basic.network

open class BaseResultBean<T>(
    val code: Int = 0, //200为成功回调 ,403 未登录  404未找到界面
    open val data: T,
    val token:String?="",
    val msg: String? = "",
    val score: Int = 0,
    val file: String? = "",//文件名称
    var callBackData: Any? = null,
)
