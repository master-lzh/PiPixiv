package com.mrl.pixiv.common.viewmodel.follow

import androidx.compose.runtime.mutableStateMapOf
import com.mrl.pixiv.common.coroutine.launchProcess
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.data.User
import com.mrl.pixiv.common.repository.PixivRepository
import kotlinx.coroutines.Dispatchers

val requireFollowState
    get() = FollowState.state

object FollowState {
    internal val state = mutableStateMapOf<Long, Boolean>()

    val User.isFollowing: Boolean
        get() = state[id] ?: isFollowed

    fun followUser(userId: Long) {
        launchProcess(Dispatchers.IO) {
            PixivRepository.followUser(userId, Restrict.PUBLIC)
            state[userId] = true
        }
    }

    fun unFollowUser(userId: Long) {
        launchProcess(Dispatchers.IO) {
            PixivRepository.unFollowUser(userId)
            state[userId] = false
        }
    }
}