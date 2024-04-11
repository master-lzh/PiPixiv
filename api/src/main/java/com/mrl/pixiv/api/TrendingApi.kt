package com.mrl.pixiv.api

import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.data.search.TrendingTagsResp
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface TrendingApi {
    @GET("v1/trending-tags/illust")
    suspend fun trendingTags(@Query("filter") filter: String): Flow<Rlt<TrendingTagsResp>>
}