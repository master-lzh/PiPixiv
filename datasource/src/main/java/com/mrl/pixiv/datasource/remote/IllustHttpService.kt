package com.mrl.pixiv.datasource.remote

import com.mrl.pixiv.data.EmptyResp
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.data.illust.IllustBookmarkDetailResp
import com.mrl.pixiv.data.illust.IllustDetailQuery
import com.mrl.pixiv.data.illust.IllustDetailResp
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.data.illust.IllustRecommendedResp
import com.mrl.pixiv.data.illust.IllustRelatedQuery
import com.mrl.pixiv.data.illust.IllustRelatedResp
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.http.parameters
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class IllustHttpService(
    @Named("api") private val httpClient: HttpClient,
) {
    suspend fun getIllustRecommended(illustRecommendedQuery: IllustRecommendedQuery) =
        httpClient.safeGet<IllustRecommendedResp>("/v1/illust/recommended") {
            illustRecommendedQuery.toMap().forEach { (key, value) ->
                parameter(key, value)
            }
        }


    suspend fun loadMoreIllustRecommended(queryMap: Map<String, String>) =
        httpClient.safeGet<IllustRecommendedResp>("/v1/illust/recommended") {
            queryMap.forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun postIllustBookmarkAdd(illustBookmarkAddReq: IllustBookmarkAddReq) =
        httpClient.safePostForm<EmptyResp>(
            "/v2/illust/bookmark/add",
            formParameters = parameters {
                illustBookmarkAddReq.toMap().forEach { (key, value) ->
                    append(key, value.toString())
                }
            }
        )

    suspend fun postIllustBookmarkDelete(illustBookmarkDeleteReq: IllustBookmarkDeleteReq) =
        httpClient.safePostForm<EmptyResp>(
            "/v1/illust/bookmark/delete",
            formParameters = parameters {
                illustBookmarkDeleteReq.toMap().forEach { (key, value) ->
                    append(key, value.toString())
                }
            }
        )

    suspend fun getIllustRelated(illustRelatedQuery: IllustRelatedQuery) =
        httpClient.safeGet<IllustRelatedResp>("/v2/illust/related") {
            illustRelatedQuery.toMap().forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun loadMoreIllustRelated(queryMap: Map<String, String>) =
        httpClient.safeGet<IllustRelatedResp>("/v2/illust/related") {
            queryMap.forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun getIllustDetail(illustDetailQuery: IllustDetailQuery) =
        httpClient.safeGet<IllustDetailResp>("/v1/illust/detail") {
            illustDetailQuery.toMap().forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun getIllustBookmarkDetail(illustId: Long) =
        httpClient.safeGet<IllustBookmarkDetailResp>("/v2/illust/bookmark/detail") {
            parameter("illust_id", illustId)
        }
}