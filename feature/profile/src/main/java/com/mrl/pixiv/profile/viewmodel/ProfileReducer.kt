package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer

class ProfileReducer : Reducer<ProfileState, ProfileAction> {
    override fun reduce(state: ProfileState, action: ProfileAction): ProfileState {
        return when (action) {
            is ProfileAction.UpdateUserInfo -> state.copy(user = action.userInfo)
            else -> state
        }
    }
}
