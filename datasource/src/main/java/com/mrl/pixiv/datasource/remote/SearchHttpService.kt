package com.mrl.pixiv.datasource.remote

import com.mrl.pixiv.api.SearchApi
import com.mrl.pixiv.data.search.SearchAutoCompleteQuery
import com.mrl.pixiv.data.search.SearchIllustQuery

class SearchHttpService(
    private val searchApi: SearchApi
) {
    suspend fun searchIllust(searchIllustQuery: SearchIllustQuery) =
        searchApi.searchIllust(searchIllustQuery.toMap())

    suspend fun searchIllust(queryMap: Map<String, String>) =
        searchApi.searchIllust(queryMap)

    suspend fun searchAutoComplete(searchAutoCompleteQuery: SearchAutoCompleteQuery) =
        searchApi.searchAutoComplete(searchAutoCompleteQuery.toMap())
}