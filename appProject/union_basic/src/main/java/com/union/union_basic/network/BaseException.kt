package com.union.union_basic.network

/**
 * classname：BaeException
 * desc: 异常封装类
 */
class BaseException(code: Int, message: String) : Exception(message) {
    val errorCode: Int = code //错误代码
}