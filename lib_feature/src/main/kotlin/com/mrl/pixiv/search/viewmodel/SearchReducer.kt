package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.core.annotation.Single

@Single
class SearchReducer : Reducer<SearchState, SearchAction> {
    override fun SearchState.reduce(action: SearchAction): SearchState {
        return when (action) {
            is SearchAction.ClearAutoCompleteSearchWords -> {
                copy(autoCompleteSearchWords = persistentListOf())
            }

            is SearchAction.UpdateSearchWords -> {
                copy(searchWords = action.searchWords)
            }

            is SearchAction.UpdateAutoCompleteSearchWords -> {
                copy(autoCompleteSearchWords = action.autoCompleteSearchWords.toImmutableList())
            }

            is SearchAction.UpdateSearchHistory -> {
                copy(searchHistory = action.searchHistory.toImmutableList())
            }

            else -> this
        }
    }
}