package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.data.Reducer

class ProfileReducer : Reducer<ProfileState, ProfileAction> {
    override fun reduce(state: ProfileState, action: ProfileAction): ProfileState {
        return when (action) {
            is ProfileAction.UpdateUserInfo -> state.copy(userInfo = action.userInfo)
            is ProfileAction.UpdateUserBookmarksIllusts -> state.copy(userBookmarksIllusts = action.illusts)
            is ProfileAction.UpdateUserBookmarksNovels -> state.copy(userBookmarksNovels = action.novels)
            else -> state
        }
    }
}