package com.mrl.pixiv.common.data

import androidx.annotation.StringDef
import androidx.compose.runtime.Stable
import com.google.common.base.CaseFormat
import kotlinx.serialization.json.Json
import kotlin.reflect.full.memberProperties

enum class Filter(override val value: String): IBaseEnum {
    ANDROID("for_android"),
    IOS("for_ios"),
}

@Stable
interface IBaseMap {
    fun toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        this.javaClass.kotlin.memberProperties.forEach { field ->
            val name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.name)
            val value = field.get(this)
            if (value != null && value.toString().isNotEmpty()) {
                map[name] = if (value is IBaseEnum) value.value.toString() else value.toString()
            }
        }
        return map
    }
}

interface IBaseQueryMap : IBaseMap

interface IBaseFieldMap : IBaseMap

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

internal val JSON = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}
