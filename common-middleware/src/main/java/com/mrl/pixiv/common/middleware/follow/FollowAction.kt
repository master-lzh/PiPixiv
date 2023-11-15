package com.mrl.pixiv.common.middleware.follow

import com.mrl.pixiv.common.data.Action

sealed class FollowAction : Action {
    data class UpdateFollowState(val userId: Long, val isFollowed: Boolean) : FollowAction()
    data class FollowUser(val userId: Long) : FollowAction()
    data class UnFollowUser(val userId: Long) : FollowAction()
}
