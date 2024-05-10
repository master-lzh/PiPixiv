package com.mrl.pixiv.repository

import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.data.illust.IllustDetailQuery
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.data.illust.IllustRelatedQuery
import com.mrl.pixiv.datasource.remote.IllustHttpService
import com.mrl.pixiv.datasource.remote.UgoiraHttpService

class IllustRepository(
    private val illustHttpService: IllustHttpService,
    private val ugoiraHttpService: UgoiraHttpService,
) {
    suspend fun getIllustRecommended(illustRecommendedQuery: IllustRecommendedQuery) =
        illustHttpService.getIllustRecommended(illustRecommendedQuery)

    suspend fun loadMoreIllustRecommended(queryMap: Map<String, String>) =
        illustHttpService.loadMoreIllustRecommended(queryMap)

    suspend fun postIllustBookmarkAdd(illustBookmarkAddReq: IllustBookmarkAddReq) =
        illustHttpService.postIllustBookmarkAdd(illustBookmarkAddReq)

    suspend fun postIllustBookmarkDelete(illustBookmarkDeleteReq: IllustBookmarkDeleteReq) =
        illustHttpService.postIllustBookmarkDelete(illustBookmarkDeleteReq)

    suspend fun getIllustRelated(illustRelatedQuery: IllustRelatedQuery) =
        illustHttpService.getIllustRelated(illustRelatedQuery)

    suspend fun loadMoreIllustRelated(queryMap: Map<String, String>) =
        illustHttpService.loadMoreIllustRelated(queryMap)

    suspend fun getIllustDetail(illustDetailQuery: IllustDetailQuery) =
        illustHttpService.getIllustDetail(illustDetailQuery)

    suspend fun getUgoiraMetadata(illustId: Long) = ugoiraHttpService.getUgoiraMetadata(illustId)

    suspend fun getIllustBookmarkDetail(illustId: Long) =
        illustHttpService.getIllustBookmarkDetail(illustId)
}