package com.mrl.pixiv.home.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.repository.paging.IllustRecommendedPagingSource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Stable
data object HomeState : State {
    val INITIAL = HomeState
}

sealed class HomeAction : Action {
    data object RefreshTokenIntent : HomeAction()

    data class UpdateState(val state: HomeState) : HomeAction()
}

@KoinViewModel
class HomeViewModel(
    homeReducer: HomeReducer,
    homeMiddleware: HomeMiddleware,
) : BaseViewModel<HomeState, HomeAction>(
    reducer = homeReducer,
    middlewares = listOf(homeMiddleware),
    initialState = HomeState.INITIAL
), KoinComponent {
    val recommendImageList = Pager(PagingConfig(pageSize = 20, prefetchDistance = 5)) {
        get<IllustRecommendedPagingSource>()
    }.flow.cachedIn(viewModelScope)

    override fun onCreate() {

    }
}