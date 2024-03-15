package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import kotlinx.collections.immutable.ImmutableMap

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