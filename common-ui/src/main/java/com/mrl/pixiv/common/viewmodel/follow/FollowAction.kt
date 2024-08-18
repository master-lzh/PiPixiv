package com.mrl.pixiv.common.viewmodel.follow

import com.mrl.pixiv.common.viewmodel.Action

sealed class FollowAction : Action {
    data class UpdateFollowState(val userId: Long, val isFollowed: Boolean) : FollowAction()
    data class FollowUser(val userId: Long) : FollowAction()
    data class UnFollowUser(val userId: Long) : FollowAction()
}
