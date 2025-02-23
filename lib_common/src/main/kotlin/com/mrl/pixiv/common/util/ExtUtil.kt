package com.mrl.pixiv.common.util

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import java.net.URL
import java.net.URLDecoder

val String.queryParams: ImmutableMap<String, String>
    get() {
        val queryMap = mutableMapOf<String, String>()
        return try {
            val query = URLDecoder.decode(URL(this).query, "UTF-8")
            if (query != null) {
                for (param in query.split("&")) {
                    val keyValuePair = param.split("=")
                    queryMap[keyValuePair[0]] = if (keyValuePair.size > 1) keyValuePair[1] else ""
                }
            }
            queryMap.toImmutableMap()
        } catch (e: Exception) {
            persistentMapOf()
        }
    }

val Any.TAG: String
    get() = this::class.simpleName ?: "TAG"

