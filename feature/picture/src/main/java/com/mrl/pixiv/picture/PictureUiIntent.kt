package com.mrl.pixiv.picture

import com.mrl.pixiv.common.data.UiIntent

sealed class PictureUiIntent : UiIntent() {
    data class GetUserIllustsIntent(
        val userId: Long,
    ) : PictureUiIntent()
}