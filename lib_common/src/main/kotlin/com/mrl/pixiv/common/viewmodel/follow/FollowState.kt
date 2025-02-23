package com.mrl.pixiv.common.viewmodel.follow

import com.mrl.pixiv.common.coroutine.launchNetwork
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.data.user.UserFollowAddReq
import com.mrl.pixiv.common.data.user.UserFollowDeleteReq
import com.mrl.pixiv.common.repository.UserRepository
import com.mrl.pixiv.common.viewmodel.GlobalState
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import org.koin.core.annotation.Single

@Single
class FollowState(
    private val userRepository: UserRepository,
) : GlobalState<ImmutableMap<Long, Boolean>>(
    initialSate = persistentMapOf()
) {

    fun followUser(userId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = userRepository.followUser(UserFollowAddReq(userId, Restrict.PUBLIC))
            ) {
                updateFollowState(userId, true)
            }
        }
    }

    fun unFollowUser(userId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = userRepository.unFollowUser(UserFollowDeleteReq(userId))
            ) {
                updateFollowState(userId, false)
            }
        }
    }

    private fun updateFollowState(userId: Long, isFollowed: Boolean) {
        updateState {
            it.toPersistentMap().put(userId, isFollowed)
        }
    }
}