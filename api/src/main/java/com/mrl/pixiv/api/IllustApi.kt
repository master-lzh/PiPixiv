package com.mrl.pixiv.api

import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.data.EmptyResp
import com.mrl.pixiv.data.illust.IllustRecommendedResp
import com.mrl.pixiv.data.illust.IllustRelatedResp
import kotlinx.coroutines.flow.Flow
import retrofit2.http.*

interface IllustApi {
    @GET("v1/illust/recommended")
    suspend fun getIllustRecommended(
        @QueryMap illustRecommendedQuery: Map<String, String>
    ): Flow<Rlt<IllustRecommendedResp>>

    @POST("v2/illust/bookmark/add")
    @FormUrlEncoded
    suspend fun postIllustBookmarkAdd(
        @FieldMap illustBookmarkAddReq: Map<String, String>
    ): Flow<Rlt<EmptyResp>>

    @POST("v1/illust/bookmark/delete")
    @FormUrlEncoded
    suspend fun postIllustBookmarkDelete(
        @FieldMap illustBookmarkDeleteReq: Map<String, String>
    ): Flow<Rlt<EmptyResp>>

    //https://app-api.pixiv.net/v2/illust/related?filter=for_android&illust_id=101532782
    @GET("v2/illust/related")
    suspend fun getIllustRelated(
        @QueryMap illustRelatedQuery: Map<String, String>
    ): Flow<Rlt<IllustRelatedResp>>
}