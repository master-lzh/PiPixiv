package com.mrl.pixiv.profile

import com.mrl.pixiv.common.base.BaseViewModel
import com.mrl.pixiv.data.user.UserDetailQuery
import com.mrl.pixiv.profile.state.toUserInfo
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.UserRemoteRepository
import kotlinx.coroutines.flow.first

class ProfileViewModel(
    private val userLocalRepository: UserLocalRepository,
    private val userRemoteRepository: UserRemoteRepository,
) : BaseViewModel<ProfileUiState, ProfileUiIntent>() {

    init {
        dispatch(ProfileUiIntent.GetUserInfoIntent)
    }

    override fun handleUserIntent(intent: ProfileUiIntent) {
        when (intent) {
            is ProfileUiIntent.GetUserInfoIntent -> getUserInfo()
            is ProfileUiIntent.GetUserBookmarksIllustIntent -> TODO()
            is ProfileUiIntent.GetUserIllustsIntent -> TODO()
        }
    }

    private fun getUserInfo() = launchIO {
        val userId = userLocalRepository.userId.first()
        requestHttpDataWithFlow(
            request = userRemoteRepository.getUserDetail(UserDetailQuery(userId = userId))
        ) {
            if (it != null) {
                updateUiState { apply { userInfo = it.toUserInfo() } }
            }
        }
    }

    override fun initUiState(): ProfileUiState = ProfileUiState()
}