package com.design.appproject.utils

import android.graphics.Color
import com.design.appproject.base.CommonBean
import com.design.appproject.bean.roleMenusList
import com.union.union_basic.ext.isNotNullOrEmpty
import com.union.union_basic.logger.LoggerManager
import com.union.union_basic.utils.StorageUtil
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun isLogin() = StorageUtil.decodeString(CommonBean.TOKEN_KEY, "").isNotNullOrEmpty()

    fun isVip() = StorageUtil.decodeBool(CommonBean.VIP_KEY, false)

    fun getUserId() = StorageUtil.decodeLong(CommonBean.USER_ID_KEY, 0)

    /**
     * 是否有权限(前台权限),tableName：表名，key:权限名
     */
    fun isAuthFront(tableName:String,key:String):Boolean {
        roleMenusList.firstOrNull { it.tableName == CommonBean.tableName }?.let {
            it.frontMenu.forEach {
                it.child.firstOrNull{ it.tableName==tableName }?.let {
                    return it.buttons.contains(key)
                }
            }
        }
        return false
    }
    /**
     * 是否有权限(后台权限),tableName：表名，key:权限名
     */
    fun isAuthBack(tableName:String,key:String):Boolean {
        roleMenusList.firstOrNull { it.tableName == CommonBean.tableName }?.let {
            it.backMenu.forEach {
                it.child.firstOrNull{ it.tableName==tableName }?.let {
                    return it.buttons.contains(key)
                }
            }
        }
        return false
    }

    fun genTradeNo():String{
        return System.currentTimeMillis().toString()
    }

    fun string2int(str:String):Int{
        if (str.length==4){
            var strList = str.split("")
            strList = strList.subList(1,strList.size-1)
            val newColor = strList[0]+strList[1]+strList[1]+strList[2]+strList[2]+strList[3]+strList[3]
            return Color.parseColor(newColor)
        }
        return Color.parseColor(str)
    }
}