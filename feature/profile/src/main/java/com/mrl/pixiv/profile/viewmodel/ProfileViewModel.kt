package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.setting.SettingTheme
import com.mrl.pixiv.data.user.UserInfo

data class ProfileState(
    val user: UserInfo
) : State {
    companion object {
        val INITIAL = ProfileState(
            user = UserInfo.defaultInstance
        )
    }
}

sealed class ProfileAction : Action {
    data object GetUserInfo : ProfileAction()
    data class ChangeAppTheme(val theme: SettingTheme) : ProfileAction()

    data class UpdateUserInfo(val userInfo: UserInfo) : ProfileAction()
}

class ProfileViewModel(
    reducer: ProfileReducer,
    middleware: ProfileMiddleware,
) : BaseViewModel<ProfileState, ProfileAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = ProfileState.INITIAL,
) {
    init {
        dispatch(ProfileAction.GetUserInfo)
    }
}


