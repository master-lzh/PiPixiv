package com.mrl.pixiv.common.router

import kotlinx.serialization.Serializable

@Serializable
sealed class Graph {
    @Serializable
    data object Main : Graph()
}