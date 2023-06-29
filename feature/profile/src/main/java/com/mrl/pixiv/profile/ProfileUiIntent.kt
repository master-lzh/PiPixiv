package com.mrl.pixiv.profile

import com.mrl.pixiv.common.data.UiIntent

sealed class ProfileUiIntent : UiIntent() {
    object GetUserInfoIntent : ProfileUiIntent()
    class GetUserIllustsIntent : ProfileUiIntent()
    class GetUserBookmarksIllustIntent : ProfileUiIntent()
}