package com.mrl.pixiv.repository

import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.datasource.remote.TrendingHttpService

class TrendingRepository(
    private val trendingHttpService: TrendingHttpService
) {
    suspend fun trendingTags(filter: Filter) = trendingHttpService.trendingTags(filter)
}