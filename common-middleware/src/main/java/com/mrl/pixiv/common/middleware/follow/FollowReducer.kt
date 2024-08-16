package com.mrl.pixiv.common.middleware.follow

import com.mrl.pixiv.common.viewmodel.Reducer


class FollowReducer : Reducer<FollowState, FollowAction> {
    override fun FollowState.reduce(action: FollowAction): FollowState {
        return when (action) {
            is FollowAction.UpdateFollowState -> copy(
                followStatus = followStatus.apply { put(action.userId, action.isFollowed) }
            )

            else -> this
        }
    }
}