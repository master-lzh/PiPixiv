package com.mrl.pixiv.common.middleware.follow

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.user.UserFollowAddReq
import com.mrl.pixiv.data.user.UserFollowDeleteReq
import com.mrl.pixiv.repository.remote.UserRemoteRepository


class FollowMiddleware(
    private val userRemoteRepository: UserRemoteRepository,
) : Middleware<FollowState, FollowAction>() {
    override suspend fun process(state: FollowState, action: FollowAction) {
        when (action) {
            is FollowAction.FollowUser -> followUser(action)
            is FollowAction.UnFollowUser -> unFollowUser(action)

            else -> {}
        }
    }

    private fun unFollowUser(action: FollowAction.UnFollowUser) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = userRemoteRepository.unFollowUser(
                    UserFollowDeleteReq(
                        userId = action.userId,
                    )
                )
            ) {
                dispatch(FollowAction.UpdateFollowState(action.userId, false))
            }
        }
    }

    private fun followUser(action: FollowAction.FollowUser) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = userRemoteRepository.followUser(
                    UserFollowAddReq(
                        userId = action.userId,
                        restrict = Restrict.PUBLIC
                    )
                )
            ) {
                dispatch(FollowAction.UpdateFollowState(action.userId, true))
            }
        }
    }
}