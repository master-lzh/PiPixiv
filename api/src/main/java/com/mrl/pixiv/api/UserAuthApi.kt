package com.mrl.pixiv.api

import com.mrl.pixiv.data.Rlt
import com.mrl.pixiv.data.auth.AuthTokenResp
import kotlinx.coroutines.flow.Flow
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAuthApi {
    @POST("auth/token")
    @FormUrlEncoded
    suspend fun login(
        @FieldMap authTokenFieldReq: Map<String, String>
    ): Flow<Rlt<AuthTokenResp>>

}