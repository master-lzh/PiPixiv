package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class SearchReducer : Reducer<SearchState, SearchAction> {
    override fun reduce(state: SearchState, action: SearchAction): SearchState {
        return when (action) {
            is SearchAction.SearchIllust -> state.copy(refreshing = true)
            is SearchAction.ClearAutoCompleteSearchWords -> {
                state.copy(autoCompleteSearchWords = persistentListOf())
            }

            is SearchAction.ClearSearchResult -> {
                state.copy(
                    searchResults = persistentListOf(),
                    nextUrl = null
                )
            }

            is SearchAction.UpdateSearchWords -> {
                state.copy(searchWords = action.searchWords)
            }

            is SearchAction.UpdateAutoCompleteSearchWords -> {
                state.copy(autoCompleteSearchWords = action.autoCompleteSearchWords.toImmutableList())
            }

            is SearchAction.UpdateSearchIllustsResult -> {
                state.copy(
                    searchResults = (state.searchResults + action.illusts).toImmutableList(),
                    nextUrl = action.nextUrl,
                    loading = false,
                    refreshing = false
                )
            }

            is SearchAction.UpdateFilter -> {
                state.copy(searchFilter = action.searchFilter)
            }

            is SearchAction.UpdateSearchHistory -> {
                state.copy(searchHistory = action.searchHistory.toImmutableList())
            }

            else -> state
        }
    }
}