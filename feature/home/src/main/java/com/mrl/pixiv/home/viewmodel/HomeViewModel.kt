package com.mrl.pixiv.home.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.home.initRecommendedQuery
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@Stable
data class HomeState(
    val recommendImageList: ImmutableList<Illust>,
    val isRefresh: Boolean,
    val nextUrl: String,
    val loadMore: Boolean,
    val exception: Throwable?,
) : State {
    companion object {
        val INITIAL = HomeState(
            recommendImageList = persistentListOf(),
            isRefresh = true,
            nextUrl = "",
            loadMore = false,
            exception = null,
        )
    }
}

sealed class HomeAction : Action {
    data class LoadMoreIllustRecommendedIntent(
        val queryMap: ImmutableMap<String, String>? = null
    ) : HomeAction()

    data class RefreshIllustRecommendedIntent(
        val illustRecommendedQuery: IllustRecommendedQuery,
    ) : HomeAction()

    data object RefreshTokenIntent : HomeAction()

    data object DismissLoading : HomeAction()
    data class UpdateState(val state: HomeState) : HomeAction()
    data class CollectExceptionFlow(val exception: Throwable?) : HomeAction()
}

@KoinViewModel
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
        viewModelScope.launch {
            exception.collect {
                dispatch(HomeAction.CollectExceptionFlow(it))
            }
        }
    }

    override fun onCreate() {

    }
}