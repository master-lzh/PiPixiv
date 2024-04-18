package com.mrl.pixiv.profile.detail.viewmodel

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Novel
import com.mrl.pixiv.profile.detail.state.UserInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class ProfileDetailState(
    var userBookmarksIllusts: ImmutableList<Illust>,
    var userBookmarksNovels: ImmutableList<Novel>,
    var userInfo: UserInfo,
) : State {
    companion object {
        val INITIAL = ProfileDetailState(
            userBookmarksIllusts = persistentListOf(),
            userBookmarksNovels = persistentListOf(),
            userInfo = UserInfo(),
        )
    }
}