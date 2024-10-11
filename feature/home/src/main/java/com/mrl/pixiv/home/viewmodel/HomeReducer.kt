package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import org.koin.core.annotation.Single

@Single
class HomeReducer : Reducer<HomeState, HomeAction> {
    override fun HomeState.reduce(action: HomeAction): HomeState {
        return when (action) {
            is HomeAction.UpdateState -> action.state
            else -> this
        }
    }
}