package com.mrl.pixiv.common.viewmodel.follow

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.user.UserFollowAddReq
import com.mrl.pixiv.data.user.UserFollowDeleteReq
import com.mrl.pixiv.repository.UserRepository


class FollowMiddleware(
    private val userRepository: UserRepository,
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
                request = userRepository.unFollowUser(
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
                request = userRepository.followUser(
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