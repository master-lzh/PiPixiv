package com.mrl.pixiv.home.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mrl.pixiv.common.data.State
import com.mrl.pixiv.home.state.RecommendImageItemState

data class HomeState(
    val recommendImageList: SnapshotStateList<RecommendImageItemState>,
    val isRefresh: Boolean,
    val nextUrl: String,
    val refreshTokenResult: Boolean,
    val loadMore: Boolean
) : State {
    companion object {
        val INITIAL = HomeState(
            recommendImageList = mutableStateListOf(),
            isRefresh = true,
            nextUrl = "",
            refreshTokenResult = false,
            loadMore = false,
        )
    }
}