package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList

class ProfileReducer : Reducer<ProfileState, ProfileAction> {
    override fun reduce(state: ProfileState, action: ProfileAction): ProfileState {
        return when (action) {
            is ProfileAction.UpdateUserInfo -> state.copy(userInfo = action.userInfo)
            is ProfileAction.UpdateUserBookmarksIllusts -> state.copy(userBookmarksIllusts = action.illusts.toImmutableList())
            is ProfileAction.UpdateUserBookmarksNovels -> state.copy(userBookmarksNovels = action.novels.toImmutableList())
            else -> state
        }
    }
}