package com.mrl.pixiv.common.util

import io.ktor.http.Url
import io.ktor.util.toMap

val String.queryParams: Map<String, String>
    get() = Url(this).parameters.toMap().mapValues { it.value.joinToString(" ") }


val Any.TAG: String
    get() = this::class.simpleName ?: "TAG"

