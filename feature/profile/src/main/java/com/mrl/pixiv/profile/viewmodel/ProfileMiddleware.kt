package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.repository.local.UserLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class ProfileMiddleware(
    private val userLocalRepository: UserLocalRepository
) : Middleware<ProfileState, ProfileAction>() {
    override suspend fun process(state: ProfileState, action: ProfileAction) {
        when (action) {
            is ProfileAction.GetUserInfo -> getUserInfo()
            else -> Unit
        }
    }

    private fun getUserInfo() {
        launchIO {
            userLocalRepository.userInfo.flowOn(Dispatchers.Main).collect { userInfo ->
                dispatch(ProfileAction.UpdateUserInfo(userInfo))
            }
        }
    }

}
