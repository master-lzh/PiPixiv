package com.mrl.pixiv.repository

import com.mrl.pixiv.data.search.SearchAutoCompleteQuery
import com.mrl.pixiv.data.search.SearchIllustQuery
import com.mrl.pixiv.data.search.searchHistory
import com.mrl.pixiv.datasource.local.SearchDataSource
import com.mrl.pixiv.datasource.remote.SearchHttpService

class SearchRepository(
    private val searchHttpService: SearchHttpService,
    private val searchDataSource: SearchDataSource,
) {
    val searchLocalSource = searchDataSource.data
    fun deleteSearchHistory(searchWords: String) {
        searchDataSource.updateData {
            val index = it.searchHistoryList.indexOfFirst { it.keyword == searchWords }
            it.toBuilder().removeSearchHistory(index).build()
        }
    }

    fun addSearchHistory(searchWords: String) {
        searchDataSource.updateData {
            // add to search history if not exist
            val index = it.searchHistoryList.indexOfFirst { it.keyword == searchWords }
            if (index == -1) {
                it.toBuilder().addSearchHistory(
                    0,
                    searchHistory {
                        keyword = searchWords
                        timestamp = System.currentTimeMillis()
                    }
                ).build()
            } else {
                // move to first if exist
                val searchHistory = it.searchHistoryList[index]
                it.toBuilder().removeSearchHistory(index).addSearchHistory(0, searchHistory).build()
            }
        }
    }
    suspend fun searchIllust(searchIllustQuery: SearchIllustQuery) =
        searchHttpService.searchIllust(searchIllustQuery)

    suspend fun searchIllustNext(queryMap: Map<String, String>) =
        searchHttpService.searchIllust(queryMap)

    suspend fun searchAutoComplete(searchAutoCompleteQuery: SearchAutoCompleteQuery) =
        searchHttpService.searchAutoComplete(searchAutoCompleteQuery)
}