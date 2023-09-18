package com.mrl.pixiv.profile

import androidx.compose.runtime.toMutableStateList
import com.mrl.pixiv.common.base.BaseScreenViewModel
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.data.user.UserDetailQuery
import com.mrl.pixiv.profile.state.toUserInfo
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.UserRemoteRepository
import kotlinx.coroutines.flow.first

class ProfileViewModel(
    private val userLocalRepository: UserLocalRepository,
    private val userRemoteRepository: UserRemoteRepository,
) : BaseScreenViewModel<ProfileUiState, ProfileUiIntent>() {

    init {
//        dispatch(ProfileUiIntent.GetUserInfoIntent)
//        dispatch(ProfileUiIntent.GetUserIllustsIntent())
//        dispatch(ProfileUiIntent.GetUserBookmarksIllustIntent())
    }

    override fun handleUserIntent(intent: ProfileUiIntent) {
        when (intent) {
            is ProfileUiIntent.GetUserInfoIntent -> getUserInfo()
            is ProfileUiIntent.GetUserBookmarksIllustIntent -> getUserBookmarksIllust()
            is ProfileUiIntent.GetUserIllustsIntent -> TODO()
        }
    }

    private fun getUserBookmarksIllust() {
        launchNetwork {
            val userId = userLocalRepository.userId.first()
            requestHttpDataWithFlow(
                request = userRemoteRepository.getUserBookmarksIllust(
                    UserBookmarksIllustQuery(restrict = Restrict.PUBLIC, userId = userId)
                )
            ) {
                if (it != null) {
                    updateUiState { apply { userBookmarksIllusts = it.illusts.toMutableStateList() } }
                }
            }
        }
    }

    private fun getUserInfo() = launchNetwork {
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