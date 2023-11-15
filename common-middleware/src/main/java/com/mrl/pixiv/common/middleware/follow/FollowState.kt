package com.mrl.pixiv.common.middleware.follow

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.mrl.pixiv.common.data.State

data class FollowState(
    val followStatus: SnapshotStateMap<Long, Boolean>,
) : State {
    companion object {
        val INITIAL = FollowState(
            followStatus = mutableStateMapOf()
        )
    }
}

