package com.mrl.pixiv.common.datasource.remote

import com.mrl.pixiv.common.data.Filter
import com.mrl.pixiv.common.data.search.TrendingTagsResp
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class TrendingHttpService(
    @Named("api") private val httpClient: HttpClient
) {
    suspend fun trendingTags(filter: Filter) =
        httpClient.safeGet<TrendingTagsResp>("/v1/trending-tags/illust") {
            parameter("filter", filter.name)
        }
}