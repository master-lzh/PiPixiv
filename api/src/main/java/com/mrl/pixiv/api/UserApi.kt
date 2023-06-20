package com.mrl.pixiv.api

import com.mrl.pixiv.data.Rlt
import com.mrl.pixiv.data.user.UserDetailResp
import com.mrl.pixiv.data.user.UserIllustsResp
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {
    @GET("v1/user/detail")
    suspend fun getUserDetail(
        @Query("filter") filter: String,
        @Query("user_id") userId: String
    ): Flow<Rlt<UserDetailResp>>

    @GET("v1/user/illusts")
    suspend fun getUserIllusts(
        @Query("filter") filter: String,
        @Query("user_id") userId: String,
        @Query("type") type: String
    ): Flow<Rlt<UserIllustsResp>>
}