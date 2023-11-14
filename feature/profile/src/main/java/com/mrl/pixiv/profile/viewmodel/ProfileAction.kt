package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.data.Action
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Novel
import com.mrl.pixiv.profile.state.UserInfo

sealed class ProfileAction : Action {
    data object GetUserInfoIntent : ProfileAction()
    data object GetUserIllustsIntent : ProfileAction()
    data object GetUserBookmarksIllustIntent : ProfileAction()

    data object GetUserBookmarksNovelIntent : ProfileAction()

    data class UpdateUserInfo(val userInfo: UserInfo) : ProfileAction()
    data class UpdateUserBookmarksIllusts(val illusts: List<Illust>) : ProfileAction()

    data class UpdateUserBookmarksNovels(val novels: List<Novel>) : ProfileAction()
}