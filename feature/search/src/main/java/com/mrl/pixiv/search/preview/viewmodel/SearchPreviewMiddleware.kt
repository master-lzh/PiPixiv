package com.mrl.pixiv.search.preview.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.repository.SearchRepository
import com.mrl.pixiv.repository.TrendingRepository

class SearchPreviewMiddleware(
    private val trendingRepository: TrendingRepository,
    private val searchRepository: SearchRepository,
) : Middleware<SearchPreviewState, SearchPreviewAction>() {
    override suspend fun process(state: SearchPreviewState, action: SearchPreviewAction) {
        when (action) {
            is SearchPreviewAction.LoadTrendingTags -> loadTrendingTags()
            is SearchPreviewAction.AddSearchHistory -> addSearchHistory(action.keyword)
            else -> Unit
        }
    }

    private fun addSearchHistory(keyword: String) {
        searchRepository.addSearchHistory(keyword)
    }

    private fun loadTrendingTags() {
        launchNetwork {
            requestHttpDataWithFlow(
                request = trendingRepository.trendingTags(Filter.ANDROID)
            ) {
                dispatch(SearchPreviewAction.UpdateTrendingTags(it.trendTags))
            }
        }
    }
}