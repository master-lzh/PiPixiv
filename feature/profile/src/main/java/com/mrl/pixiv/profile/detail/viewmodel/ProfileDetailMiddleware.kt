package com.mrl.pixiv.profile.detail.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.ProfileImageUrls
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.data.User
import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.data.user.UserBookmarksNovelQuery
import com.mrl.pixiv.data.user.UserDetailQuery
import com.mrl.pixiv.data.user.UserIllustsQuery
import com.mrl.pixiv.profile.detail.state.UserInfo
import com.mrl.pixiv.profile.detail.state.toUserInfo
import kotlinx.coroutines.flow.first


class ProfileDetailMiddleware(
    private val userRepository: com.mrl.pixiv.repository.UserRepository,
) : Middleware<ProfileDetailState, ProfileDetailAction>() {
    override suspend fun process(state: ProfileDetailState, action: ProfileDetailAction) {
        when (action) {
            is ProfileDetailAction.GetUserInfoIntent -> getUserInfo(action.uid)
            is ProfileDetailAction.GetUserBookmarksIllustIntent -> getUserBookmarksIllust(action.uid)
            is ProfileDetailAction.GetUserBookmarksNovelIntent -> getUserBookmarksNovel(action.uid)
            is ProfileDetailAction.GetUserIllustsIntent -> getUserIllusts(action.uid)

            else -> {}
        }
    }

    private fun getUserIllusts(uid: Long) {
        launchNetwork {
            val userId = if (uid != Long.MIN_VALUE) uid else userRepository.getUserId()
            requestHttpDataWithFlow(
                request = userRepository.getUserIllusts(
                    UserIllustsQuery(
                        userId = userId,
                        type = Type.Illust.value,
                    )
                ),
            ) {
                dispatch(ProfileDetailAction.UpdateUserIllusts(it.illusts))
            }
        }
    }

    private fun getUserBookmarksNovel(uid: Long) =
        launchNetwork {
            val userId = if (uid != Long.MIN_VALUE) uid else userRepository.getUserId()
            requestHttpDataWithFlow(
                request = userRepository.getUserBookmarksNovels(
                    UserBookmarksNovelQuery(restrict = Restrict.PUBLIC, userId = userId)
                )
            ) {
                dispatch(ProfileDetailAction.UpdateUserBookmarksNovels(it.novels))
            }
        }

    private fun getUserBookmarksIllust(uid: Long) =
        launchNetwork {
            val userId = if (uid != Long.MIN_VALUE) uid else userRepository.getUserId()
            requestHttpDataWithFlow(
                request = userRepository.getUserBookmarksIllust(
                    UserBookmarksIllustQuery(restrict = Restrict.PUBLIC, userId = userId)
                )
            ) {
                dispatch(ProfileDetailAction.UpdateUserBookmarksIllusts(it.illusts))
            }
        }


    private fun getUserInfo(uid: Long) = launchNetwork {
        val userId = if (uid != Long.MIN_VALUE) uid else userRepository.getUserId()
        requestHttpDataWithFlow(
            request = userRepository.getUserDetail(UserDetailQuery(userId = userId)),
            failedCallback = {
                val userInfo = userRepository.userInfo.first()
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
            if (uid == Long.MIN_VALUE) {
                userRepository.setUserInfo { userInfo ->
                    userInfo.copy(
                        uid = it.user.id,
                        username = it.user.name,
                        avatar = it.user.profileImageUrls.medium
                    )
                }
            }
            dispatch(ProfileDetailAction.UpdateUserInfo(it.toUserInfo()))
        }
    }
}