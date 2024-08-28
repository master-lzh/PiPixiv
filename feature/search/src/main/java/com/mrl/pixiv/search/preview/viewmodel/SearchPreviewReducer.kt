package com.mrl.pixiv.search.preview.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList
import org.koin.core.annotation.Single

@Single
class SearchPreviewReducer : Reducer<SearchPreviewState, SearchPreviewAction> {
    override fun SearchPreviewState.reduce(action: SearchPreviewAction): SearchPreviewState {
        return when (action) {
            is SearchPreviewAction.LoadTrendingTags -> copy(refreshing = true)
            is SearchPreviewAction.UpdateTrendingTags -> copy(
                trendingTags = action.trendingTags.toImmutableList(),
                refreshing = false
            )

            else -> this
        }
    }
}