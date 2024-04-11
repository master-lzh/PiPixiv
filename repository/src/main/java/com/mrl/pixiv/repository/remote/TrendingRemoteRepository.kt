package com.mrl.pixiv.repository.remote

import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.datasource.remote.TrendingHttpService

class TrendingRemoteRepository(
    private val trendingHttpService: TrendingHttpService
) {
    suspend fun trendingTags(filter: Filter) = trendingHttpService.trendingTags(filter)
}