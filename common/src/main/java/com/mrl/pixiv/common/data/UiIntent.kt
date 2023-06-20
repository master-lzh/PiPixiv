package com.mrl.pixiv.common.data

open class UiIntent(
    var forceHandle: Boolean = false
)

fun UiIntent.forceHandle() = apply { forceHandle = true }

