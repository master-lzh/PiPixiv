package com.mrl.pixiv.domain

import com.mrl.pixiv.repository.local.UserLocalRepository
import kotlinx.coroutines.flow.first

class GetUserIdUseCase(
    private val userLocalRepository: UserLocalRepository
) {
    suspend operator fun invoke(): Long {
        return userLocalRepository.userId.first()
    }
}

class SetUserIdUseCase(
    private val userLocalRepository: UserLocalRepository
) {
    suspend operator fun invoke(userId: Long) {
        userLocalRepository.setUserId(userId)
    }
}