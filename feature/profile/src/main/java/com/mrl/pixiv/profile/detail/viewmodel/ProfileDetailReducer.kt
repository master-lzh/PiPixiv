package com.mrl.pixiv.profile.detail.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList

class ProfileDetailReducer : Reducer<ProfileDetailState, ProfileDetailAction> {
    override fun ProfileDetailState.reduce(
        action: ProfileDetailAction
    ): ProfileDetailState {
        return when (action) {
            is ProfileDetailAction.UpdateUserInfo -> copy(userInfo = action.userInfo)
            is ProfileDetailAction.UpdateUserBookmarksIllusts -> copy(userBookmarksIllusts = action.illusts.toImmutableList())
            is ProfileDetailAction.UpdateUserBookmarksNovels -> copy(userBookmarksNovels = action.novels.toImmutableList())
            is ProfileDetailAction.UpdateUserIllusts -> copy(userIllusts = action.illusts.toImmutableList())
            else -> this
        }
    }
}