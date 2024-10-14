package com.mrl.pixiv.search.viewmodel.result

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.search.SearchAiType
import com.mrl.pixiv.data.search.SearchIllustQuery
import com.mrl.pixiv.data.search.SearchSort
import com.mrl.pixiv.data.search.SearchTarget
import com.mrl.pixiv.repository.paging.SearchIllustPagingSource
import com.mrl.pixiv.search.viewmodel.SearchState.SearchFilter
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Stable
data class SearchResultState(
    val searchWords: String,
    val searchFilter: SearchFilter,
) : State {
    companion object {
        val INITIAL = SearchResultState(
            searchWords = "",
            searchFilter = SearchFilter(
                sort = SearchSort.POPULAR_DESC,
                searchTarget = SearchTarget.PARTIAL_MATCH_FOR_TAGS,
                searchAiType = SearchAiType.HIDE_AI,
            ),
        )
    }
}

sealed class SearchResultAction : Action {
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
), KoinComponent {
    val searchResults = Pager(config = PagingConfig(pageSize = 20)) {
        SearchIllustPagingSource(
            get(), SearchIllustQuery(
                word = searchWords,
                searchTarget = state().searchFilter.searchTarget,
                sort = state().searchFilter.sort,
                searchAiType = state().searchFilter.searchAiType,
            )
        )
    }.flow.cachedIn(viewModelScope)
}