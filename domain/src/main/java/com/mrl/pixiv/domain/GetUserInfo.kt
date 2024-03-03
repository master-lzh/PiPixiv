package com.mrl.pixiv.domain

import com.mrl.pixiv.data.user.UserInfo
import com.mrl.pixiv.repository.local.UserLocalRepository
import kotlinx.coroutines.flow.first

class GetLocalUserInfoUseCase(
    private val userLocalRepository: UserLocalRepository
) {
    suspend operator fun invoke(): UserInfo {
        return userLocalRepository.userInfo.first()
    }
}

class SetLocalUserInfoUseCase(
    private val userLocalRepository: UserLocalRepository
) {
    operator fun invoke(userInfo: (UserInfo) -> UserInfo) {
        userLocalRepository.setUserInfo(userInfo)
    }
}