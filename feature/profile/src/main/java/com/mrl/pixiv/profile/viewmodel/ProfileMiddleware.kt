package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.data.user.UserBookmarksNovelQuery
import com.mrl.pixiv.data.user.UserDetailQuery
import com.mrl.pixiv.profile.state.toUserInfo
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.UserRemoteRepository


class ProfileMiddleware(
    private val userLocalRepository: UserLocalRepository,
    private val userRemoteRepository: UserRemoteRepository,
) : Middleware<ProfileState, ProfileAction>() {
    override suspend fun process(state: ProfileState, action: ProfileAction) {
        when (action) {
            is ProfileAction.GetUserInfoIntent -> getUserInfo(state)
            is ProfileAction.GetUserBookmarksIllustIntent -> getUserBookmarksIllust(state)
            is ProfileAction.GetUserBookmarksNovelIntent -> getUserBookmarksNovel(state)
            is ProfileAction.GetUserIllustsIntent -> TODO()

            else -> {}
        }
    }

    private fun getUserBookmarksNovel(state: ProfileState) =
        launchNetwork {
            val userId = userLocalRepository.getUserId()
            requestHttpDataWithFlow(
                request = userRemoteRepository.getUserBookmarksNovels(
                    UserBookmarksNovelQuery(restrict = Restrict.PUBLIC, userId = userId)
                )
            ) {
                dispatch(ProfileAction.UpdateUserBookmarksNovels(it.novels))
            }
        }

    private fun getUserBookmarksIllust(state: ProfileState) =
        launchNetwork {
            val userId = userLocalRepository.getUserId()
            requestHttpDataWithFlow(
                request = userRemoteRepository.getUserBookmarksIllust(
                    UserBookmarksIllustQuery(restrict = Restrict.PUBLIC, userId = userId)
                )
            ) {
                dispatch(ProfileAction.UpdateUserBookmarksIllusts(it.illusts))
            }
        }


    private fun getUserInfo(state: ProfileState) = launchNetwork {
        val userId = userLocalRepository.getUserId()
        requestHttpDataWithFlow(
            request = userRemoteRepository.getUserDetail(UserDetailQuery(userId = userId))
        ) {
            dispatch(ProfileAction.UpdateUserInfo(it.toUserInfo()))
        }
    }
}