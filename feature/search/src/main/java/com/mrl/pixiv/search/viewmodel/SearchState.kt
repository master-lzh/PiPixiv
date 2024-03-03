package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.data.State
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Tag
import com.mrl.pixiv.data.search.SearchAiType
import com.mrl.pixiv.data.search.SearchHistory
import com.mrl.pixiv.data.search.SearchSort
import com.mrl.pixiv.data.search.SearchTarget

data class SearchState(
    val searchWords: String,
    val autoCompleteSearchWords: List<Tag>,
    val searchFilter: SearchFilter,
    val searchHistory: List<SearchHistory>,
    val searchResults: List<Illust>,
    val nextUrl: String,
) : State {

    data class SearchFilter(
        val sort: SearchSort,
        val searchTarget: SearchTarget,
        val searchAiType: SearchAiType,
    )

    companion object {
        val INITIAL = SearchState(
            searchWords = "",
            autoCompleteSearchWords = emptyList(),
            searchFilter = SearchFilter(
                sort = SearchSort.POPULAR_DESC,
                searchTarget = SearchTarget.PARTIAL_MATCH_FOR_TAGS,
                searchAiType = SearchAiType.HIDE_AI,
            ),
            searchHistory = emptyList(),
            searchResults = emptyList(),
            nextUrl = "",
        )
    }
}