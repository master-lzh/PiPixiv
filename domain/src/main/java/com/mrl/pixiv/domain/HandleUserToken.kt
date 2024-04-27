package com.mrl.pixiv.domain

import com.mrl.pixiv.repository.UserRepository
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit

class SetUserAccessTokenUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(accessToken: String) {
        userRepository.setAccessTokenExpiresTime(
            System.currentTimeMillis() + 1.hours.toLong(
                DurationUnit.MILLISECONDS
            )
        )
        userRepository.setUserAccessToken(accessToken)
    }
}

class SetUserRefreshTokenUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(refreshToken: String) {
        userRepository.setUserRefreshToken(refreshToken)
    }
}