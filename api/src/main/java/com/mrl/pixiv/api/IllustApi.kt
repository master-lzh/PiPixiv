package com.mrl.pixiv.api

import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.data.EmptyResp
import com.mrl.pixiv.data.illust.IllustBookmarkDetailResp
import com.mrl.pixiv.data.illust.IllustDetailResp
import com.mrl.pixiv.data.illust.IllustRecommendedResp
import com.mrl.pixiv.data.illust.IllustRelatedResp
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface IllustApi {
    @GET("v1/illust/recommended")
    suspend fun getIllustRecommended(
        @QueryMap illustRecommendedQuery: Map<String, String>
    ): Flow<Rlt<IllustRecommendedResp>>

    @POST("v2/illust/bookmark/add")
    @FormUrlEncoded
    suspend fun postIllustBookmarkAdd(
        @Field("illust_id") illustId: Long,
        @Field("restrict") restrict: String,
        @Field("tags[]") tags: List<String>?
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

    @GET("v1/illust/detail")
    suspend fun getIllustDetail(
        @QueryMap illustDetailQuery: Map<String, String>
    ): Flow<Rlt<IllustDetailResp>>

    @GET("/v2/illust/bookmark/detail")
    suspend fun getIllustBookmarkDetail(
        @Query("illust_id") illustId: Long
    ): Flow<Rlt<IllustBookmarkDetailResp>>
}