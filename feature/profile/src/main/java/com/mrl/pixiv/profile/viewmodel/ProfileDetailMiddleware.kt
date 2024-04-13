package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
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


class ProfileDetailMiddleware(
    private val userLocalRepository: UserLocalRepository,
    private val userRemoteRepository: UserRemoteRepository,
) : Middleware<ProfileDetailState, ProfileDetailAction>() {
    override suspend fun process(state: ProfileDetailState, action: ProfileDetailAction) {
        when (action) {
            is ProfileDetailAction.GetUserInfoIntent -> getUserInfo()
            is ProfileDetailAction.GetUserBookmarksIllustIntent -> getUserBookmarksIllust()
            is ProfileDetailAction.GetUserBookmarksNovelIntent -> getUserBookmarksNovel()
            is ProfileDetailAction.GetUserIllustsIntent -> TODO()

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
                dispatch(ProfileDetailAction.UpdateUserBookmarksNovels(it.novels))
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
                dispatch(ProfileDetailAction.UpdateUserBookmarksIllusts(it.illusts))
            }
        }


    private fun getUserInfo() = launchNetwork {
        val userId = userLocalRepository.getUserId()
        requestHttpDataWithFlow(
            request = userRemoteRepository.getUserDetail(UserDetailQuery(userId = userId)),
            failedCallback = {
                val userInfo = userLocalRepository.userInfo.first()
                dispatch(
                    ProfileDetailAction.UpdateUserInfo(
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
            dispatch(ProfileDetailAction.UpdateUserInfo(it.toUserInfo()))
        }
    }
}