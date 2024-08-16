package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class SearchReducer : Reducer<SearchState, SearchAction> {
    override fun SearchState.reduce(action: SearchAction): SearchState {
        return when (action) {
            is SearchAction.SearchIllust -> copy(refreshing = true)
            is SearchAction.ClearAutoCompleteSearchWords -> {
                copy(autoCompleteSearchWords = persistentListOf())
            }

            is SearchAction.ClearSearchResult -> {
                copy(
                    searchResults = persistentListOf(),
                    nextUrl = null
                )
            }

            is SearchAction.UpdateSearchWords -> {
                copy(searchWords = action.searchWords)
            }

            is SearchAction.UpdateAutoCompleteSearchWords -> {
                copy(autoCompleteSearchWords = action.autoCompleteSearchWords.toImmutableList())
            }

            is SearchAction.UpdateSearchIllustsResult -> {
                copy(
                    searchResults = (searchResults + action.illusts).toImmutableList(),
                    nextUrl = action.nextUrl,
                    loading = false,
                    refreshing = false
                )
            }

            is SearchAction.UpdateFilter -> {
                copy(searchFilter = action.searchFilter)
            }

            is SearchAction.UpdateSearchHistory -> {
                copy(searchHistory = action.searchHistory.toImmutableList())
            }

            else -> this
        }
    }
}