package com.mrl.pixiv.search.viewmodel

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Tag
import com.mrl.pixiv.data.search.SearchAiType
import com.mrl.pixiv.data.search.SearchHistory
import com.mrl.pixiv.data.search.SearchSort
import com.mrl.pixiv.data.search.SearchTarget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel

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

@KoinViewModel
class SearchViewModel(
    reducer: SearchReducer,
    middleware: SearchMiddleware
) : BaseViewModel<SearchState, SearchAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = SearchState.INITIAL,
) {
}

