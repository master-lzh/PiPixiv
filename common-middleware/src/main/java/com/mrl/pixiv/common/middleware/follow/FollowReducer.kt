package com.mrl.pixiv.common.middleware.follow

import com.mrl.pixiv.common.data.Reducer


class FollowReducer : Reducer<FollowState, FollowAction> {
    override fun reduce(state: FollowState, action: FollowAction): FollowState {
        return when (action) {
            is FollowAction.UpdateFollowState -> state.copy(
                followStatus = state.followStatus.apply { put(action.userId, action.isFollowed) }
            )

            else -> state
        }
    }
}