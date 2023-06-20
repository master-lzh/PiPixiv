package com.mrl.pixiv.data

import com.google.common.base.CaseFormat
import kotlin.reflect.full.memberProperties

enum class Filter(val filter: String) {
    ANDROID("for_android"),
    IOS("for_ios"),
}

interface IBaseMap {
    fun toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        this.javaClass.kotlin.memberProperties.forEach { field ->
            val name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.name)
            val value = field.get(this)
            if (value != null) {
                map[name] = value.toString()
            }
        }
        return map
    }
}

interface IBaseQueryMap : IBaseMap

interface IBaseFieldMap : IBaseMap