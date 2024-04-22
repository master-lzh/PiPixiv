package com.mrl.pixiv.api

import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.data.EmptyResp
import com.mrl.pixiv.data.user.UserDetailResp
import com.mrl.pixiv.data.user.UserHistoryIllustsResp
import com.mrl.pixiv.data.user.UserIllustsResp
import com.mrl.pixiv.data.user.UserNovelsResp
import kotlinx.coroutines.flow.Flow
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface UserApi {
    @GET("/v2/user/detail")
    suspend fun getUserDetail(
        @QueryMap userDetailQuery: Map<String, String>,
    ): Flow<Rlt<UserDetailResp>>

    @GET("/v1/user/illusts")
    suspend fun getUserIllusts(
        @QueryMap userIllustsQuery: Map<String, String>,
    ): Flow<Rlt<UserIllustsResp>>

    @GET("/v1/user/bookmarks/illust")
    suspend fun getUserBookmarksIllust(
        @QueryMap userBookmarksIllustQuery: Map<String, String>,
    ): Flow<Rlt<UserIllustsResp>>

    @GET("/v1/user/bookmarks/novel")
    suspend fun getUserBookmarksNovel(
        @QueryMap userBookmarksNovelQuery: Map<String, String>,
    ): Flow<Rlt<UserNovelsResp>>

    @POST("/v1/user/follow/add")
    @FormUrlEncoded
    suspend fun followUser(
        @FieldMap followUserField: Map<String, String>,
    ): Flow<Rlt<EmptyResp>>

    @POST("/v1/user/follow/delete")
    @FormUrlEncoded
    suspend fun unFollowUser(
        @FieldMap unFollowUserField: Map<String, String>,
    ): Flow<Rlt<EmptyResp>>

    @GET("/v1/user/browsing-history/illusts")
    suspend fun getUserBrowsingHistoryIllusts(
        @QueryMap userBrowsingHistoryIllustsQuery: Map<String, String>,
    ): Flow<Rlt<UserHistoryIllustsResp>>
}