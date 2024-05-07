package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.setting.SettingTheme
import com.mrl.pixiv.data.setting.setAppCompatDelegateThemeMode
import com.mrl.pixiv.repository.SettingRepository
import com.mrl.pixiv.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class ProfileMiddleware(
    private val userRepository: UserRepository,
    private val settingRepository: SettingRepository,
) : Middleware<ProfileState, ProfileAction>() {
    override suspend fun process(state: ProfileState, action: ProfileAction) {
        when (action) {
            is ProfileAction.GetUserInfo -> getUserInfo()
            is ProfileAction.ChangeAppTheme -> changeAppTheme(action.theme)
            else -> Unit
        }
    }

    private fun changeAppTheme(theme: SettingTheme) {
        settingRepository.setSettingTheme(theme)
        setAppCompatDelegateThemeMode(theme)
    }

    private fun getUserInfo() {
        launchIO {
            userRepository.userInfo.flowOn(Dispatchers.Main).collect { userInfo ->
                dispatch(ProfileAction.UpdateUserInfo(userInfo))
            }
        }
    }

}
