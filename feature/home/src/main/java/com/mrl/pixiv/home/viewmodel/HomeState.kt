package com.mrl.pixiv.home.viewmodel

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class HomeState(
    val recommendImageList: ImmutableList<Illust>,
    val isRefresh: Boolean,
    val nextUrl: String,
    val loadMore: Boolean,
    val exception: Throwable?,
) : State {
    companion object {
        val INITIAL = HomeState(
            recommendImageList = persistentListOf(),
            isRefresh = true,
            nextUrl = "",
            loadMore = false,
            exception = null,
        )
    }
}