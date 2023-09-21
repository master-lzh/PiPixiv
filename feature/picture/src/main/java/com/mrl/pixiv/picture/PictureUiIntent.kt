package com.mrl.pixiv.picture

import com.mrl.pixiv.common.data.UiIntent

sealed class PictureUiIntent : UiIntent() {
    data class GetUserIllustsIntent(
        val userId: Long,
    ) : PictureUiIntent()

    data class GetIllustRelatedIntent(
        val illustId: Long,
    ) : PictureUiIntent()

    data class LoadMoreIllustRelatedIntent(
        val queryMap: Map<String, String>? = null,
    ) : PictureUiIntent()
}