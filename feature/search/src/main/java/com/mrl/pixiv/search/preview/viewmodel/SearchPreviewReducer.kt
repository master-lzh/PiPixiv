package com.mrl.pixiv.search.preview.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList

class SearchPreviewReducer : Reducer<SearchPreviewState, SearchPreviewAction> {
    override fun reduce(
        state: SearchPreviewState,
        action: SearchPreviewAction
    ): SearchPreviewState {
        return when (action) {
            is SearchPreviewAction.LoadTrendingTags -> state.copy(refreshing = true)
            is SearchPreviewAction.UpdateTrendingTags -> state.copy(
                trendingTags = action.trendingTags.toImmutableList(),
                refreshing = false
            )

            else -> state
        }
    }
}