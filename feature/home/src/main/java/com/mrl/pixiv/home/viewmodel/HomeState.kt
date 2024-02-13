package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.data.State
import com.mrl.pixiv.data.Illust

data class HomeState(
    val recommendImageList: List<Illust>,
    val isRefresh: Boolean,
    val nextUrl: String,
    val loadMore: Boolean
) : State {
    companion object {
        val INITIAL = HomeState(
            recommendImageList = emptyList(),
            isRefresh = true,
            nextUrl = "",
            loadMore = false,
        )
    }
}