package com.union.union_basic.utils

import java.io.File

object FileUtils {

    fun getSandboxPath(): String {
        val externalFilesDir: File? = AppUtils.currentApplication?.getExternalFilesDir("")
        val customFile = File(externalFilesDir?.absolutePath, "Sandbox")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }
}