package com.mrl.pixiv.datasource.remote

import com.mrl.pixiv.api.TrendingApi
import com.mrl.pixiv.data.Filter

class TrendingHttpService(
    private val trendingApi: TrendingApi
) {
    suspend fun trendingTags(filter: Filter) = trendingApi.trendingTags(filter.value)
}