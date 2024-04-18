package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.setting.SettingTheme
import com.mrl.pixiv.data.setting.setAppCompatDelegateThemeMode
import com.mrl.pixiv.repository.local.SettingLocalRepository
import com.mrl.pixiv.repository.local.UserLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class ProfileMiddleware(
    private val userLocalRepository: UserLocalRepository,
    private val settingLocalRepository: SettingLocalRepository,
) : Middleware<ProfileState, ProfileAction>() {
    override suspend fun process(state: ProfileState, action: ProfileAction) {
        when (action) {
            is ProfileAction.GetUserInfo -> getUserInfo()
            is ProfileAction.ChangeAppTheme -> changeAppTheme(action.theme)
            else -> Unit
        }
    }

    private fun changeAppTheme(theme: SettingTheme) {
        settingLocalRepository.setSettingTheme(theme)
        setAppCompatDelegateThemeMode(theme)
    }

    private fun getUserInfo() {
        launchIO {
            userLocalRepository.userInfo.flowOn(Dispatchers.Main).collect { userInfo ->
                dispatch(ProfileAction.UpdateUserInfo(userInfo))
            }
        }
    }

}
