package com.mrl.pixiv.domain

import com.mrl.pixiv.data.user.UserInfo
import com.mrl.pixiv.repository.SettingRepository
import com.mrl.pixiv.repository.UserRepository
import kotlinx.coroutines.flow.first

class GetLocalUserInfoUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserInfo {
        return userRepository.userInfo.first()
    }
}

class SetLocalUserInfoUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(userInfo: (UserInfo) -> UserInfo) {
        userRepository.setUserInfo(userInfo)
    }
}

class HasShowBookmarkTipUseCase(
    private val settingRepository: SettingRepository
) {
    suspend operator fun invoke(): Boolean {
        return settingRepository.hasShowBookmarkTip.first()
    }
}

class SetShowBookmarkTipUseCase(
    private val settingRepository: SettingRepository
) {
    operator fun invoke(hasShow: Boolean) {
        settingRepository.setHasShowBookmarkTip(hasShow)
    }
}