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
    val userTotalWorks: Int,
    val userIllusts: ImmutableList<Illust>,
    val userBookmarksIllusts: ImmutableList<Illust>,
    val userBookmarksNovels: ImmutableList<Novel>,
    val userInfo: UserInfo,
) : State {
    companion object {
        val INITIAL = ProfileDetailState(
            userTotalWorks = 0,
            userIllusts = persistentListOf(),
            userBookmarksIllusts = persistentListOf(),
            userBookmarksNovels = persistentListOf(),
            userInfo = UserInfo(),
        )
    }
}