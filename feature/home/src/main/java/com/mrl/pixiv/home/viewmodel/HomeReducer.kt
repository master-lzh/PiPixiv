package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.data.Reducer

class HomeReducer : Reducer<HomeState, HomeAction> {
    override fun reduce(state: HomeState, action: HomeAction): HomeState {
        return when (action) {
            is HomeAction.UpdateState -> action.state
            is HomeAction.ShowLoading -> state.copy(isRefresh = true)
            is HomeAction.DismissLoading -> state.copy(isRefresh = false)
            else -> state
        }
    }
}