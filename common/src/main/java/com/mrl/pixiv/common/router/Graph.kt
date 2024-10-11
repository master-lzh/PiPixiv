package com.mrl.pixiv.common.router

import kotlinx.serialization.Serializable

@Serializable
sealed class Graph {
    @Serializable
    data object Root : Graph()
    @Serializable
    data object Main : Graph()
    @Serializable
    data object Search : Graph()
}