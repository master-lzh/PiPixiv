package com.mrl.pixiv.search.viewmodel.result

import com.mrl.pixiv.common.viewmodel.Reducer
import org.koin.core.annotation.Single

@Single
class SearchResultReducer : Reducer<SearchResultState, SearchResultAction> {
    override fun SearchResultState.reduce(action: SearchResultAction): SearchResultState {
        return when (action) {
            is SearchResultAction.UpdateFilter -> {
                copy(searchFilter = action.searchFilter)
            }
        }
    }
}