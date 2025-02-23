package com.mrl.pixiv.common.datasource.local.datastore.base

import com.mrl.pixiv.common.util.AppUtil
import okio.Path
import okio.Path.Companion.toOkioPath

fun dataStorePath(fileName: String): Path {
    val context = AppUtil.appContext
    return context.filesDir.resolve("datastore/$fileName").toOkioPath()
}