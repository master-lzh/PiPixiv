package com.mrl.pixiv.search.viewmodel.result

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList
import org.koin.core.annotation.Single

@Single
class SearchResultReducer : Reducer<SearchResultState, SearchResultAction> {
    override fun SearchResultState.reduce(action: SearchResultAction): SearchResultState {
        return when (action) {
            is SearchResultAction.SearchIllust -> copy(refreshing = true)

            is SearchResultAction.UpdateSearchIllustsResult -> {
                copy(
                    searchResults = (if (action.initial) action.illusts else searchResults + action.illusts).toImmutableList(),
                    nextUrl = action.nextUrl,
                    loading = false,
                    refreshing = false
                )
            }

            is SearchResultAction.UpdateFilter -> {
                copy(searchFilter = action.searchFilter)
            }

            else -> this
        }
    }
}