package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer

class ProfileReducer : Reducer<ProfileState, ProfileAction> {
    override fun ProfileState.reduce(action: ProfileAction): ProfileState {
        return when (action) {
            is ProfileAction.UpdateUserInfo -> copy(user = action.userInfo)
            else -> this
        }
    }
}
