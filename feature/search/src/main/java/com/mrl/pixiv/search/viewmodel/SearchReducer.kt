package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.data.Reducer

class SearchReducer : Reducer<SearchState, SearchAction> {
    override fun reduce(state: SearchState, action: SearchAction): SearchState {
        return when (action) {
            is SearchAction.UpdateSearchWords -> {
                state.copy(searchWords = action.searchWords)
            }

            is SearchAction.UpdateAutoCompleteSearchWords -> {
                state.copy(autoCompleteSearchWords = action.autoCompleteSearchWords)
            }

            is SearchAction.UpdateSearchIllustsResult -> {
                state.copy(
                    searchResults = state.searchResults + action.illusts,
                    nextUrl = action.nextUrl
                )
            }

            else -> state
        }
    }
}