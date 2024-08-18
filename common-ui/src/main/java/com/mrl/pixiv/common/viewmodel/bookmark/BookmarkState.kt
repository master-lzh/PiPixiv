package com.mrl.pixiv.common.viewmodel.bookmark

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.mrl.pixiv.common.viewmodel.State

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
