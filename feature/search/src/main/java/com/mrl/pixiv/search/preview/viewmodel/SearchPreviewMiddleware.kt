package com.mrl.pixiv.search.preview.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.repository.local.SearchLocalRepository
import com.mrl.pixiv.repository.remote.TrendingRemoteRepository

class SearchPreviewMiddleware(
    private val trendingRemoteRepository: TrendingRemoteRepository,
    private val searchLocalRepository: SearchLocalRepository,
) : Middleware<SearchPreviewState, SearchPreviewAction>() {
    override suspend fun process(state: SearchPreviewState, action: SearchPreviewAction) {
        when (action) {
            is SearchPreviewAction.LoadTrendingTags -> loadTrendingTags()
            is SearchPreviewAction.AddSearchHistory -> addSearchHistory(action.keyword)
            else -> Unit
        }
    }

    private fun addSearchHistory(keyword: String) {
        searchLocalRepository.addSearchHistory(keyword)
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