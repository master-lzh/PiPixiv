package com.mrl.pixiv.datasource.local.datastore.base

import com.mrl.pixiv.util.AppUtil
import okio.Path
import okio.Path.Companion.toOkioPath

fun dataStorePath(fileName: String): Path {
    val context = AppUtil.appContext
    return context.filesDir.resolve("datastore/$fileName").toOkioPath()
}