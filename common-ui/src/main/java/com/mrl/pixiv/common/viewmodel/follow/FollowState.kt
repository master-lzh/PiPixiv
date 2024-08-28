package com.mrl.pixiv.common.viewmodel.follow

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrl.pixiv.common.coroutine.launchNetwork
import com.mrl.pixiv.common.viewmodel.GlobalState
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.user.UserFollowAddReq
import com.mrl.pixiv.data.user.UserFollowDeleteReq
import com.mrl.pixiv.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
class FollowState(
    private val userRepository: UserRepository,
) : GlobalState<Map<Long, Boolean>>() {
    private val _followStatus: MutableStateFlow<Map<Long, Boolean>> = MutableStateFlow(emptyMap())
    val followStatus = _followStatus.asStateFlow()

    @Composable
    override fun state(): Map<Long, Boolean> = followStatus.collectAsStateWithLifecycle().value

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
        _followStatus.value += (userId to isFollowed)
    }
}