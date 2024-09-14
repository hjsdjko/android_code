package com.union.union_basic.permission

import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX

/**
 * classname：PermissionUtil
 * desc: 权限申请工具类
 */
object PermissionUtil {

    fun permission(activity: FragmentActivity, vararg permissions: String, successCallBack: (() -> Unit)? = null,
        errorCallBack: ((deniedList: List<String>) -> Unit)? = null) {
        PermissionX.init(activity).permissions(permissions = permissions).onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(deniedList, "当前功能需要以下权限才能正常使用", "去授权", "取消")
        }.onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(deniedList, "当前功能需要以下权限才能正常使用", "去授权", "取消")
        }.request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                successCallBack?.invoke()
            } else {
                errorCallBack?.invoke(deniedList)
            }
        }
    }
}