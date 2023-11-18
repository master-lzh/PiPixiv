package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.data.Reducer

class HomeReducer : Reducer<HomeState, HomeAction> {
    override fun reduce(state: HomeState, action: HomeAction): HomeState {
        return when (action) {
            is HomeAction.UpdateState -> action.state
            is HomeAction.DismissLoading -> state.copy(isRefresh = false)
            is HomeAction.UpdateIllustBookmark -> updateRecommendList(state, action)
            is HomeAction.RefreshIllustRecommendedIntent -> state.copy(isRefresh = true)
            else -> state
        }
    }

    private fun updateRecommendList(
        state: HomeState,
        action: HomeAction.UpdateIllustBookmark
    ): HomeState {
        val imageList = state.recommendImageList
        val index = imageList.indexOfFirst { it.illust.id == action.id }
        if (index != -1) {
            imageList[index] = imageList[index].copy(isBookmarked = action.bookmarked)
        }
        return state
    }
}