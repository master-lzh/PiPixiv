package com.mrl.pixiv.domain

import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.util.hour

class SetUserAccessTokenUseCase(
    private val userLocalRepository: UserLocalRepository
) {
    operator fun invoke(accessToken: String) {
        userLocalRepository.setAccessTokenExpiresTime(System.currentTimeMillis() + 1.hour)
        userLocalRepository.setUserAccessToken(accessToken)
    }
}

class SetUserRefreshTokenUseCase(
    private val userLocalRepository: UserLocalRepository
) {
    operator fun invoke(refreshToken: String) {
        userLocalRepository.setUserRefreshToken(refreshToken)
    }
}