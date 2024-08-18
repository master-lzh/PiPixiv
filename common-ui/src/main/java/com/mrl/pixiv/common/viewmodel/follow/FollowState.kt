package com.mrl.pixiv.common.viewmodel.follow

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.mrl.pixiv.common.viewmodel.State

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

