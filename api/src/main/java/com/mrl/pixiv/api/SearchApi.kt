package com.mrl.pixiv.api

import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.data.search.SearchAutoCompleteResp
import com.mrl.pixiv.data.search.SearchIllustResp
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface SearchApi {
    @GET("v1/search/illust")
    suspend fun searchIllust(@QueryMap queryMap: Map<String, String>): Flow<Rlt<SearchIllustResp>>

    @GET("v2/search/autocomplete")
    suspend fun searchAutoComplete(@QueryMap queryMap: Map<String, String>): Flow<Rlt<SearchAutoCompleteResp>>
}