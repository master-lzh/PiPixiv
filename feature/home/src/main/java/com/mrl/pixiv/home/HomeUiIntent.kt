package com.mrl.pixiv.home

import com.mrl.pixiv.common.data.UiIntent
import com.mrl.pixiv.data.auth.GrantType
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.data.illust.IllustRecommendedQuery

sealed class HomeUiIntent : UiIntent() {
    data class LoadMoreIllustRecommendedIntent(
        val queryMap: Map<String, String>? = null
    ) : HomeUiIntent()

    data class RefreshIllustRecommendedIntent(
        val illustRecommendedQuery: IllustRecommendedQuery,
    ) : HomeUiIntent()

    data class IllustBookmarkAddIntent(
        val illustBookmarkAddReq: IllustBookmarkAddReq,
    ) : HomeUiIntent()

    data class IllustBookmarkDeleteIntent(
        val illustBookmarkDeleteReq: IllustBookmarkDeleteReq,
    ) : HomeUiIntent()

    data class RefreshTokenIntent(val grantType: GrantType) : HomeUiIntent()
}