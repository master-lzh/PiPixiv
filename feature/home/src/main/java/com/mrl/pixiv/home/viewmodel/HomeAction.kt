package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.data.Action
import com.mrl.pixiv.data.illust.IllustRecommendedQuery

sealed class HomeAction : Action {
    data class LoadMoreIllustRecommendedIntent(
        val queryMap: Map<String, String>? = null
    ) : HomeAction()

    data class RefreshIllustRecommendedIntent(
        val illustRecommendedQuery: IllustRecommendedQuery,
    ) : HomeAction()

    data object RefreshTokenIntent : HomeAction()

    data object DismissLoading : HomeAction()
    data class UpdateState(val state: HomeState) : HomeAction()
}