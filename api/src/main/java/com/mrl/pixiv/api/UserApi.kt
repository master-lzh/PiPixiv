package com.mrl.pixiv.api

import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.data.user.UserDetailResp
import com.mrl.pixiv.data.user.UserIllustsResp
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface UserApi {
    @GET("v2/user/detail")
    suspend fun getUserDetail(
        @QueryMap userDetailQuery: Map<String, String>,
    ): Flow<Rlt<UserDetailResp>>

    @GET("v1/user/illusts")
    suspend fun getUserIllusts(
        @QueryMap userIllustsQuery: Map<String, String>,
    ): Flow<Rlt<UserIllustsResp>>

    @GET("v1/user/bookmarks/illust")
    suspend fun getUserBookmarksIllust(
        @QueryMap userBookmarksIllustQuery: Map<String, String>,
    ): Flow<Rlt<UserIllustsResp>>
}