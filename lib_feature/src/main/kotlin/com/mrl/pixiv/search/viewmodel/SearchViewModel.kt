package com.mrl.pixiv.search.viewmodel

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.data.Tag
import com.mrl.pixiv.common.data.search.SearchAiType
import com.mrl.pixiv.common.data.search.SearchHistory
import com.mrl.pixiv.common.data.search.SearchSort
import com.mrl.pixiv.common.data.search.SearchTarget
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel

@Stable
data class SearchState(
    val searchWords: String,
    val autoCompleteSearchWords: ImmutableList<Tag>,
    val searchHistory: ImmutableList<SearchHistory>,
) : State {

    data class SearchFilter(
        val sort: SearchSort,
        val searchTarget: SearchTarget,
        val searchAiType: SearchAiType,
    )

    companion object {
        val INITIAL = SearchState(
            searchWords = "",
            autoCompleteSearchWords = persistentListOf(),
            searchHistory = persistentListOf(),
        )
    }
}

sealed class SearchAction : Action {
    data object ClearAutoCompleteSearchWords : SearchAction()
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

    data class UpdateSearchHistory(
        val searchHistory: List<SearchHistory>,
    ) : SearchAction()

    data class AddSearchHistory(
        val searchWords: String,
    ) : SearchAction()

    data class DeleteSearchHistory(
        val searchWords: String,
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

