package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.ProfileImageUrls
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.User
import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.data.user.UserBookmarksNovelQuery
import com.mrl.pixiv.data.user.UserDetailQuery
import com.mrl.pixiv.data.user.copy
import com.mrl.pixiv.profile.state.UserInfo
import com.mrl.pixiv.profile.state.toUserInfo
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.UserRemoteRepository
import kotlinx.coroutines.flow.first


class ProfileMiddleware(
    private val userLocalRepository: UserLocalRepository,
    private val userRemoteRepository: UserRemoteRepository,
) : Middleware<ProfileState, ProfileAction>() {
    override suspend fun process(state: ProfileState, action: ProfileAction) {
        when (action) {
            is ProfileAction.GetUserInfoIntent -> getUserInfo()
            is ProfileAction.GetUserBookmarksIllustIntent -> getUserBookmarksIllust()
            is ProfileAction.GetUserBookmarksNovelIntent -> getUserBookmarksNovel()
            is ProfileAction.GetUserIllustsIntent -> TODO()

            else -> {}
        }
    }

    private fun getUserBookmarksNovel() =
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

    private fun getUserBookmarksIllust() =
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


    private fun getUserInfo() = launchNetwork {
        val userId = userLocalRepository.getUserId()
        requestHttpDataWithFlow(
            request = userRemoteRepository.getUserDetail(UserDetailQuery(userId = userId)),
            failedCallback = {
                val userInfo = userLocalRepository.userInfo.first()
                dispatch(
                    ProfileAction.UpdateUserInfo(
                        UserInfo(
                            user = User(
                                id = userInfo.uid,
                                name = userInfo.username,
                                profileImageUrls = ProfileImageUrls(userInfo.avatar),
                                account = ""
                            )
                        )
                    )
                )
            }
        ) {
            userLocalRepository.setUserInfo { userInfo ->
                userInfo.copy {
                    uid = it.user.id
                    username = it.user.name
                    avatar = it.user.profileImageUrls.medium
                }
            }
            dispatch(ProfileAction.UpdateUserInfo(it.toUserInfo()))
        }
    }
}