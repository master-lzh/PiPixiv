package com.mrl.pixiv.repository.remote

import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.data.illust.IllustRelatedQuery
import com.mrl.pixiv.datasource.remote.IllustHttpService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn

class IllustRemoteRepository constructor(
    private val illustHttpService: IllustHttpService,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getIllustRecommended(illustRecommendedQuery: IllustRecommendedQuery) =
        illustHttpService.getIllustRecommended(illustRecommendedQuery)
            .flowOn(ioDispatcher)

    suspend fun loadMoreIllustRecommended(queryMap: Map<String, String>) =
        illustHttpService.loadMoreIllustRecommended(queryMap)
            .flowOn(ioDispatcher)

    suspend fun postIllustBookmarkAdd(illustBookmarkAddReq: IllustBookmarkAddReq) =
        illustHttpService.postIllustBookmarkAdd(illustBookmarkAddReq)
            .flowOn(ioDispatcher)

    suspend fun postIllustBookmarkDelete(illustBookmarkDeleteReq: IllustBookmarkDeleteReq) =
        illustHttpService.postIllustBookmarkDelete(illustBookmarkDeleteReq)
            .flowOn(ioDispatcher)

    suspend fun getIllustRelated(illustRelatedQuery: IllustRelatedQuery) =
        illustHttpService.getIllustRelated(illustRelatedQuery)
            .flowOn(ioDispatcher)

    suspend fun loadMoreIllustRelated(queryMap: Map<String, String>) =
        illustHttpService.loadMoreIllustRelated(queryMap)
            .flowOn(ioDispatcher)
}