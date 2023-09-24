package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.base.BaseViewModel
import com.mrl.pixiv.home.initRecommendedQuery

class HomeViewModel(
    homeReducer: HomeReducer,
    homeMiddleware: HomeMiddleware,
) : BaseViewModel<HomeState, HomeAction>(
    reducer = homeReducer,
    middlewares = listOf(homeMiddleware),
    initialState = HomeState.INITIAL
) {
    init {
        dispatch(HomeAction.RefreshIllustRecommendedIntent(initRecommendedQuery))
    }
}