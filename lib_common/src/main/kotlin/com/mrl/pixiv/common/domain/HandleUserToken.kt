package com.mrl.pixiv.common.domain

import com.mrl.pixiv.common.repository.UserRepository
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit

@Single
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

@Single
class SetUserRefreshTokenUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(refreshToken: String) {
        userRepository.setUserRefreshToken(refreshToken)
    }
}