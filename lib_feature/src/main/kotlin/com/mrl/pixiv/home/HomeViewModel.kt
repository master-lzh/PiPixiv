package com.mrl.pixiv.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mrl.pixiv.common.datasource.local.mmkv.AuthManager
import com.mrl.pixiv.common.repository.paging.IllustRecommendedPagingSource
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.ToastUtil
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.ViewIntent
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Stable
data object HomeState

sealed class HomeAction : ViewIntent {
    data object RefreshTokenIntent : HomeAction()
}

@KoinViewModel
class HomeViewModel : BaseMviViewModel<HomeState, HomeAction>(
    initialState = HomeState
), KoinComponent {
    val recommendImageList = Pager(PagingConfig(pageSize = 20)) {
        get<IllustRecommendedPagingSource>()
    }.flow.cachedIn(viewModelScope)

    override suspend fun handleIntent(intent: HomeAction) {
        when (intent) {
            HomeAction.RefreshTokenIntent -> refreshToken()
        }
    }

    private fun refreshToken() {
        launchIO {
            AuthManager.requireUserAccessToken(force = true)
            ToastUtil.safeShortToast(RString.refresh_token_success)
        }
    }
}