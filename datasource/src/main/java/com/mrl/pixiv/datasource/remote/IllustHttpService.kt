package com.mrl.pixiv.datasource.remote

import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.api.IllustApi

class IllustHttpService constructor(
    private val illustApi: IllustApi,
) {
    suspend fun getIllustRecommended(illustRecommendedQuery: IllustRecommendedQuery) =
        illustApi.getIllustRecommended(illustRecommendedQuery.toMap())

    suspend fun loadMoreIllustRecommended(queryMap: Map<String, String>) =
        illustApi.getIllustRecommended(queryMap)

    suspend fun postIllustBookmarkAdd(illustBookmarkAddReq: IllustBookmarkAddReq) =
        illustApi.postIllustBookmarkAdd(illustBookmarkAddReq.toMap())

    suspend fun postIllustBookmarkDelete(illustBookmarkDeleteReq: IllustBookmarkDeleteReq) =
        illustApi.postIllustBookmarkDelete(illustBookmarkDeleteReq.toMap())

}