package com.mrl.pixiv.profile

import com.mrl.pixiv.common.data.setting.SettingTheme
import com.mrl.pixiv.common.data.setting.setAppCompatDelegateThemeMode
import com.mrl.pixiv.common.datasource.local.mmkv.AuthManager
import com.mrl.pixiv.common.datasource.local.mmkv.UserManager
import com.mrl.pixiv.common.repository.SearchRepository
import com.mrl.pixiv.common.repository.SettingRepository
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.ToastUtil
import com.mrl.pixiv.common.util.copyToClipboard
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.ViewIntent
import org.koin.android.annotation.KoinViewModel

data object ProfileState

sealed class ProfileAction : ViewIntent {
    data object GetUserInfo : ProfileAction()
    data class ChangeAppTheme(val theme: SettingTheme) : ProfileAction()
    data object ExportToken : ProfileAction()
}

@KoinViewModel
class ProfileViewModel : BaseMviViewModel<ProfileState, ProfileAction>(
    initialState = ProfileState,
) {
    override suspend fun handleIntent(intent: ProfileAction) {
        when (intent) {
            is ProfileAction.GetUserInfo -> getUserInfo()
            is ProfileAction.ChangeAppTheme -> changeAppTheme(intent.theme)
            is ProfileAction.ExportToken -> exportToken()
        }
    }

    private fun exportToken() {
        val token = AuthManager.userRefreshToken
        copyToClipboard(token)
        ToastUtil.safeShortToast(RString.copy_to_clipboard)
    }

    fun logout() {
        AuthManager.logout()
        UserManager.logout()
        SettingRepository.clear()
        SearchRepository.clear()
    }

    private fun changeAppTheme(theme: SettingTheme) {
        SettingRepository.setSettingTheme(theme)
        setAppCompatDelegateThemeMode(theme)
    }

    private fun getUserInfo() {
        launchIO {
            UserManager.updateUserInfoAsync()
        }
    }
}


