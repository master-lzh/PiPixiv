package com.mrl.pixiv.search.viewmodel.result

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.search.SearchAiType
import com.mrl.pixiv.data.search.SearchSort
import com.mrl.pixiv.data.search.SearchTarget
import com.mrl.pixiv.search.viewmodel.SearchState.SearchFilter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel

@Stable
data class SearchResultState(
    val refreshing: Boolean,
    val searchWords: String,
    val searchFilter: SearchFilter,
    val searchResults: ImmutableList<Illust>,
    val nextUrl: String?,
    val loading: Boolean,
) : State {
    companion object {
        val INITIAL = SearchResultState(
            refreshing = false,
            searchWords = "",
            searchFilter = SearchFilter(
                sort = SearchSort.POPULAR_DESC,
                searchTarget = SearchTarget.PARTIAL_MATCH_FOR_TAGS,
                searchAiType = SearchAiType.HIDE_AI,
            ),
            searchResults = persistentListOf(),
            nextUrl = null,
            loading = true,
        )
    }
}

sealed class SearchResultAction : Action {
    data class SearchIllust(
        val searchWords: String,
    ) : SearchResultAction()

    data class SearchIllustNext(
        val nextUrl: String,
    ) : SearchResultAction()

    data class UpdateSearchIllustsResult(
        val illusts: List<Illust>,
        val nextUrl: String?,
        val initial: Boolean,
    ) : SearchResultAction()

    data class UpdateFilter(
        val searchFilter: SearchFilter,
    ) : SearchResultAction()
}

@KoinViewModel
class SearchResultViewModel(
    searchWords: String,
    reducer: SearchResultReducer,
    middleware: SearchResultMiddleware
) : BaseViewModel<SearchResultState, SearchResultAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = SearchResultState.INITIAL.copy(searchWords = searchWords),
) {
    init {
        dispatch(SearchResultAction.SearchIllust(searchWords))
    }
}