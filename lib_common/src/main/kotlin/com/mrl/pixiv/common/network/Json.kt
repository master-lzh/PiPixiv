package com.mrl.pixiv.common.network

import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}