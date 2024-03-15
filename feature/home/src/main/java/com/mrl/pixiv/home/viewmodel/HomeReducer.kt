package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer

class HomeReducer : Reducer<HomeState, HomeAction> {
    override fun reduce(state: HomeState, action: HomeAction): HomeState {
        return when (action) {
            is HomeAction.UpdateState -> action.state
            is HomeAction.DismissLoading -> state.copy(isRefresh = false)
            is HomeAction.RefreshIllustRecommendedIntent -> state.copy(isRefresh = true)
            else -> state
        }
    }
}