package com.mrl.pixiv.datasource.remote

import com.mrl.pixiv.data.ugoira.UgoiraMetadataResp
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class UgoiraHttpService(
    @Named("api") private val httpClient: HttpClient
) {
    suspend fun getUgoiraMetadata(illustId: Long) =
        httpClient.safeGet<UgoiraMetadataResp>("/v1/ugoira/metadata") {
            parameter("illust_id", illustId)
        }
}