package com.design.appproject.base

import com.union.union_basic.utils.StorageUtil

object CommonBean {

    /**登录存储token的键值*/
    const val TOKEN_KEY = "token_key"

    /**登录存储userid的键值*/
    const val USER_ID_KEY = "user_id_key"

    /**登录存储是否vip的键值*/
    const val VIP_KEY = "vip_key"

    /**登录存储用户头像键值*/
    const val HEAD_URL_KEY = "head_url_key"
    /**登录的用户类型键值*/
    const val LOGIN_USER_OPTIONS = "login_user_options"
    /**登录存储用户名键值*/
    const val USERNAME_KEY = "username_key"

    /**tableName的键值*/
    const val TABLE_NAME_KEY = "table_name_key"

    val DOWNLOAD_PATH = "${com.union.union_basic.utils.AppUtils.getApp().getExternalFilesDir("")}/app_project"

    val TABLE_LIST = listOf<String>(
          "yonghu",
    )

    var tableName = StorageUtil.decodeString(TABLE_NAME_KEY, "") ?: ""

}