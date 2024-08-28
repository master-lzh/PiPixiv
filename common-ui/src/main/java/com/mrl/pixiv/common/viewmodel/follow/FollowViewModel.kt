package com.mrl.pixiv.common.viewmodel.follow

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import org.koin.android.annotation.KoinViewModel

@Stable
data class FollowState(
    val followStatus: SnapshotStateMap<Long, Boolean>,
) : State {
    companion object {
        val INITIAL = FollowState(
            followStatus = mutableStateMapOf()
        )
    }
}

sealed class FollowAction : Action {
    data class UpdateFollowState(val userId: Long, val isFollowed: Boolean) : FollowAction()
    data class FollowUser(val userId: Long) : FollowAction()
    data class UnFollowUser(val userId: Long) : FollowAction()
}

@KoinViewModel
class FollowViewModel(
    reducer: FollowReducer,
    middleware: FollowMiddleware
) : BaseViewModel<FollowState, FollowAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = FollowState.INITIAL,
) {
}