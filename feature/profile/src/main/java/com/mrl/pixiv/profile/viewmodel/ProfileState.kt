package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.data.State
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Novel
import com.mrl.pixiv.profile.state.UserInfo

data class ProfileState(
    var userBookmarksIllusts: List<Illust>,
    var userBookmarksNovels: List<Novel>,
    var userInfo: UserInfo,
) : State {
    companion object {
        val INITIAL = ProfileState(
            userBookmarksIllusts = emptyList(),
            userBookmarksNovels = emptyList(),
            userInfo = UserInfo(),
        )
    }
}