package com.mrl.pixiv.api

import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.data.ugoira.UgoiraMetadataResp
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface UgoiraApi {
    @GET("/v1/ugoira/metadata")
    suspend fun getUgoiraMetadata(@Query("illust_id") illustId: Long): Flow<Rlt<UgoiraMetadataResp>>
}