package com.mrl.pixiv.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.mrl.pixiv.common.base.BaseViewModel
import com.mrl.pixiv.home.initRecommendedQuery
import kotlinx.coroutines.launch

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

    override fun onCreate() {
        viewModelScope.launch {
            exception.collect {
                dispatch(HomeAction.CollectExceptionFlow(it))
            }
        }
    }
}