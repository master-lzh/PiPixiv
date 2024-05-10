package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Tag
import com.mrl.pixiv.data.search.SearchHistory

sealed class SearchAction : Action {
    data object ClearAutoCompleteSearchWords : SearchAction()
    data object ClearSearchResult : SearchAction()
    data object LoadSearchHistory : SearchAction()

    data class UpdateSearchWords(
        val searchWords: String,
    ) : SearchAction()

    data class SearchAutoComplete(
        val searchWords: String,
        val mergePlainKeywordResults: Boolean = true,
    ) : SearchAction()

    data class UpdateAutoCompleteSearchWords(
        val autoCompleteSearchWords: List<Tag>,
    ) : SearchAction()

    data class SearchIllust(
        val searchWords: String,
    ) : SearchAction()

    data class SearchIllustNext(
        val nextUrl: String,
    ) : SearchAction()

    data class UpdateSearchHistory(
        val searchHistory: List<SearchHistory>,
    ) : SearchAction()

    data class AddSearchHistory(
        val searchWords: String,
    ) : SearchAction()

    data class DeleteSearchHistory(
        val searchWords: String,
    ) : SearchAction()

    data class UpdateSearchIllustsResult(
        val illusts: List<Illust>,
        val nextUrl: String?,
    ) : SearchAction()

    data class UpdateFilter(
        val searchFilter: SearchState.SearchFilter,
    ) : SearchAction()

}