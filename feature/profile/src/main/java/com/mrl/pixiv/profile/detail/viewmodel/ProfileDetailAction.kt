package com.mrl.pixiv.profile.detail.viewmodel

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Novel
import com.mrl.pixiv.profile.detail.state.UserInfo

sealed class ProfileDetailAction : Action {
    data class GetUserInfoIntent(val uid: Long) : ProfileDetailAction()
    data class GetUserIllustsIntent(val uid: Long) : ProfileDetailAction()
    data class GetUserBookmarksIllustIntent(val uid: Long) : ProfileDetailAction()
    data class GetUserBookmarksNovelIntent(val uid: Long) : ProfileDetailAction()

    data class UpdateUserInfo(val userInfo: UserInfo) : ProfileDetailAction()
    data class UpdateUserBookmarksIllusts(val illusts: List<Illust>) : ProfileDetailAction()

    data class UpdateUserBookmarksNovels(val novels: List<Novel>) : ProfileDetailAction()
    data class UpdateUserIllusts(val illusts: List<Illust>) : ProfileDetailAction()
}