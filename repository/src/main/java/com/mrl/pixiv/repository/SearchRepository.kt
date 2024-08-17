package com.mrl.pixiv.repository

import com.mrl.pixiv.data.search.SearchAutoCompleteQuery
import com.mrl.pixiv.data.search.SearchHistory
import com.mrl.pixiv.data.search.SearchIllustQuery
import com.mrl.pixiv.datasource.local.datastore.SearchDataSource
import com.mrl.pixiv.datasource.remote.SearchHttpService

class SearchRepository(
    private val searchHttpService: SearchHttpService,
    private val searchDataSource: SearchDataSource,
) {
    val searchLocalSource = searchDataSource.data
    fun deleteSearchHistory(searchWords: String) {
        searchDataSource.updateData {
            val index = it.searchHistoryList.indexOfFirst { it.keyword == searchWords }
            it.copy(
                searchHistoryList = it.searchHistoryList.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
    }

    fun addSearchHistory(searchWords: String) {
        searchDataSource.updateData {
            // add to search history if not exist
            val index = it.searchHistoryList.indexOfFirst { it.keyword == searchWords }
            if (index == -1) {
                it.copy(
                    searchHistoryList = it.searchHistoryList.toMutableList().apply {
                        add(0, SearchHistory(
                            keyword = searchWords,
                            timestamp = System.currentTimeMillis()
                        ))
                    }
                )
            } else {
                // move to first if exist
                val searchHistory = it.searchHistoryList[index]
                it.copy(
                    searchHistoryList = it.searchHistoryList.toMutableList().apply {
                        removeAt(index)
                        add(0, searchHistory)
                    }
                )
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