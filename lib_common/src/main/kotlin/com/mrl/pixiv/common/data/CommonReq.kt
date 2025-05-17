package com.mrl.pixiv.common.data

import androidx.annotation.StringDef

enum class Filter(override val value: String) : IBaseEnum {
    ANDROID("for_android"),
    IOS("for_ios"),
}

interface IBaseEnum {
    val value: Any
}

@StringDef(Restrict.PUBLIC, Restrict.PRIVATE, Restrict.ALL)
annotation class Restrict {
    companion object {
        const val PUBLIC = "public"
        const val PRIVATE = "private"
        const val ALL = "all"
    }
}
