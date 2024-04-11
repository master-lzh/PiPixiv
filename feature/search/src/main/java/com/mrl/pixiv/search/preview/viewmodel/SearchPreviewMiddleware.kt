package com.mrl.pixiv.search.preview.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.repository.remote.TrendingRemoteRepository

class SearchPreviewMiddleware(
    private val trendingRemoteRepository: TrendingRemoteRepository
) : Middleware<SearchPreviewState, SearchPreviewAction>() {
    override suspend fun process(state: SearchPreviewState, action: SearchPreviewAction) {
        when (action) {
            is SearchPreviewAction.LoadTrendingTags -> loadTrendingTags()
            else -> Unit
        }
    }

    private fun loadTrendingTags() {
        launchNetwork {
            requestHttpDataWithFlow(
                request = trendingRemoteRepository.trendingTags(Filter.ANDROID)
            ) {
                dispatch(SearchPreviewAction.UpdateTrendingTags(it.trendTags))
            }
        }
    }
}