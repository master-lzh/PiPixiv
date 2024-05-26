package com.mrl.pixiv.search.viewmodel

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Tag
import com.mrl.pixiv.data.search.SearchAiType
import com.mrl.pixiv.data.search.SearchHistory
import com.mrl.pixiv.data.search.SearchSort
import com.mrl.pixiv.data.search.SearchTarget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class SearchState(
    val refreshing: Boolean,
    val searchWords: String,
    val autoCompleteSearchWords: ImmutableList<Tag>,
    val searchFilter: SearchFilter,
    val searchHistory: ImmutableList<SearchHistory>,
    val searchResults: ImmutableList<Illust>,
    val nextUrl: String?,
    val loading: Boolean,
) : State {

    data class SearchFilter(
        val sort: SearchSort,
        val searchTarget: SearchTarget,
        val searchAiType: SearchAiType,
    )

    companion object {
        val INITIAL = SearchState(
            refreshing = false,
            searchWords = "",
            autoCompleteSearchWords = persistentListOf(),
            searchFilter = SearchFilter(
                sort = SearchSort.POPULAR_DESC,
                searchTarget = SearchTarget.PARTIAL_MATCH_FOR_TAGS,
                searchAiType = SearchAiType.HIDE_AI,
            ),
            searchHistory = persistentListOf(),
            searchResults = persistentListOf(),
            nextUrl = null,
            loading = true,
        )
    }
}