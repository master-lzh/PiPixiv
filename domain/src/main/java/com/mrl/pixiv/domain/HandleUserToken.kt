package com.mrl.pixiv.domain

import com.mrl.pixiv.repository.local.UserLocalRepository
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit

class SetUserAccessTokenUseCase(
    private val userLocalRepository: UserLocalRepository
) {
    operator fun invoke(accessToken: String) {
        userLocalRepository.setAccessTokenExpiresTime(
            System.currentTimeMillis() + 1.hours.toLong(
                DurationUnit.MILLISECONDS
            )
        )
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