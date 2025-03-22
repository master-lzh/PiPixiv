package com.mrl.pixiv.profile

import com.mrl.pixiv.common.data.setting.SettingTheme
import com.mrl.pixiv.common.data.setting.setAppCompatDelegateThemeMode
import com.mrl.pixiv.common.datasource.local.mmkv.UserManager
import com.mrl.pixiv.common.repository.SettingRepository
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.ViewIntent
import org.koin.android.annotation.KoinViewModel

data object ProfileState

sealed class ProfileAction : ViewIntent {
    data object GetUserInfo : ProfileAction()
    data class ChangeAppTheme(val theme: SettingTheme) : ProfileAction()
}

@KoinViewModel
class ProfileViewModel(
    private val settingRepository: SettingRepository,
) : BaseMviViewModel<ProfileState, ProfileAction>(
    initialState = ProfileState,
) {
    override suspend fun handleIntent(intent: ProfileAction) {
        when (intent) {
            is ProfileAction.GetUserInfo -> getUserInfo()
            is ProfileAction.ChangeAppTheme -> changeAppTheme(intent.theme)
        }
    }

    private fun changeAppTheme(theme: SettingTheme) {
        settingRepository.updateSettings {
            it.copy(theme = theme.name)
        }
        setAppCompatDelegateThemeMode(theme)
    }

    private fun getUserInfo() {
        launchIO {
            UserManager.updateUserInfoAsync()
        }
    }
}


