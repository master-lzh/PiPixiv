package com.mrl.pixiv.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mrl.pixiv.common.repository.paging.IllustRecommendedPagingSource
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.ViewIntent
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent

@Stable
data object HomeState

sealed class HomeAction : ViewIntent

@KoinViewModel
class HomeViewModel : BaseMviViewModel<HomeState, HomeAction>(
    initialState = HomeState
), KoinComponent {
    val recommendImageList = Pager(PagingConfig(pageSize = 20)) {
        IllustRecommendedPagingSource()
    }.flow.cachedIn(viewModelScope)

    override suspend fun handleIntent(intent: HomeAction) {
    }
}