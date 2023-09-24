package com.mrl.pixiv.picture.viewmodel

import com.mrl.pixiv.common.data.Action

sealed class PictureAction : Action {
    data class GetUserIllustsIntent(
        val userId: Long,
    ) : PictureAction()

    data class GetIllustRelatedIntent(
        val illustId: Long,
    ) : PictureAction()

    data class LoadMoreIllustRelatedIntent(
        val queryMap: Map<String, String>? = null,
    ) : PictureAction()

    data class UpdateState(val state: PictureState) : PictureAction()
}