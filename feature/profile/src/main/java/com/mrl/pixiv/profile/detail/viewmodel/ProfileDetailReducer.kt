package com.mrl.pixiv.profile.detail.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList

class ProfileDetailReducer : Reducer<ProfileDetailState, ProfileDetailAction> {
    override fun reduce(
        state: ProfileDetailState,
        action: ProfileDetailAction
    ): ProfileDetailState {
        return when (action) {
            is ProfileDetailAction.UpdateUserInfo -> state.copy(userInfo = action.userInfo)
            is ProfileDetailAction.UpdateUserBookmarksIllusts -> state.copy(userBookmarksIllusts = action.illusts.toImmutableList())
            is ProfileDetailAction.UpdateUserBookmarksNovels -> state.copy(userBookmarksNovels = action.novels.toImmutableList())
            is ProfileDetailAction.UpdateUserIllusts -> state.copy(userIllusts = action.illusts.toImmutableList())
            else -> state
        }
    }
}