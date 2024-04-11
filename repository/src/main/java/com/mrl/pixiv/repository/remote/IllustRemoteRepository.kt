package com.mrl.pixiv.repository.remote

import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.data.illust.IllustRelatedQuery
import com.mrl.pixiv.datasource.remote.IllustHttpService

class IllustRemoteRepository(
    private val illustHttpService: IllustHttpService,
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
}