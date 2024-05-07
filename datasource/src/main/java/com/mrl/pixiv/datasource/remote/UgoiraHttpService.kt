package com.mrl.pixiv.datasource.remote

import com.mrl.pixiv.api.UgoiraApi

class UgoiraHttpService(
    private val ugoiraApi: UgoiraApi
) {
    suspend fun getUgoiraMetadata(illustId: Long) = ugoiraApi.getUgoiraMetadata(illustId)
}