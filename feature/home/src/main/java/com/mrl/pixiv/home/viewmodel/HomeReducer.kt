package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer

class HomeReducer : Reducer<HomeState, HomeAction> {
    override fun HomeState.reduce(action: HomeAction): HomeState {
        return when (action) {
            is HomeAction.UpdateState -> action.state
            is HomeAction.DismissLoading -> copy(isRefresh = false)
            is HomeAction.RefreshIllustRecommendedIntent -> copy(isRefresh = true)
            else -> this
        }
    }
}