package com.mrl.pixiv.common.repository

import com.mrl.pixiv.common.data.Filter
import com.mrl.pixiv.common.datasource.remote.TrendingHttpService
import org.koin.core.annotation.Single

@Single
class TrendingRepository(
    private val trendingHttpService: TrendingHttpService
) {
    suspend fun trendingTags(filter: Filter) = trendingHttpService.trendingTags(filter)
}