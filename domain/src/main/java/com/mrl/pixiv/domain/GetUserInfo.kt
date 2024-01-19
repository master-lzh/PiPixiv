package com.mrl.pixiv.domain

import com.mrl.pixiv.data.auth.AuthUser
import com.mrl.pixiv.repository.local.UserLocalRepository
import kotlinx.coroutines.flow.first

class GetLocalUserInfoUseCase(
    private val userLocalRepository: UserLocalRepository
) {
    suspend operator fun invoke(): AuthUser {
        return userLocalRepository.userInfo.first()
    }
}

class SetLocalUserInfoUseCase(
    private val userLocalRepository: UserLocalRepository
) {
    operator fun invoke(userInfo: AuthUser) {
        userLocalRepository.setUserInfo(userInfo)
    }
}