package com.mrl.pixiv.repository.remote

import com.mrl.pixiv.data.search.SearchAutoCompleteQuery
import com.mrl.pixiv.data.search.SearchIllustQuery
import com.mrl.pixiv.datasource.remote.SearchHttpService

class SearchRemoteRepository(
    private val searchHttpService: SearchHttpService,
) {
    suspend fun searchIllust(searchIllustQuery: SearchIllustQuery) =
        searchHttpService.searchIllust(searchIllustQuery)

    suspend fun searchIllustNext(queryMap: Map<String, String>) =
        searchHttpService.searchIllust(queryMap)

    suspend fun searchAutoComplete(searchAutoCompleteQuery: SearchAutoCompleteQuery) =
        searchHttpService.searchAutoComplete(searchAutoCompleteQuery)
}