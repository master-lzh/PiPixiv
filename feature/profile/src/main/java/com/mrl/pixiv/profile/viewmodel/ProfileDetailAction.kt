package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Novel
import com.mrl.pixiv.profile.state.UserInfo

sealed class ProfileDetailAction : Action {
    data object GetUserInfoIntent : ProfileDetailAction()
    data object GetUserIllustsIntent : ProfileDetailAction()
    data object GetUserBookmarksIllustIntent : ProfileDetailAction()

    data object GetUserBookmarksNovelIntent : ProfileDetailAction()

    data class UpdateUserInfo(val userInfo: UserInfo) : ProfileDetailAction()
    data class UpdateUserBookmarksIllusts(val illusts: List<Illust>) : ProfileDetailAction()

    data class UpdateUserBookmarksNovels(val novels: List<Novel>) : ProfileDetailAction()
}