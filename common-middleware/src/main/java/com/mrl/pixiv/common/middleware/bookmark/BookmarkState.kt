package com.mrl.pixiv.common.middleware.bookmark

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.mrl.pixiv.common.data.State

@Stable
data class BookmarkState(
    val bookmarkStatus: SnapshotStateMap<Long, Boolean>,
) : State {
    companion object {
        val INITIAL = BookmarkState(
            bookmarkStatus = mutableStateMapOf()
        )
    }
}
