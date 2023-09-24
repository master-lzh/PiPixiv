package com.mrl.pixiv.common.middleware.bookmark

import com.mrl.pixiv.common.data.State

data class BookmarkState(
    val isBookmark: Boolean,
) : State {
    companion object {
        val INITIAL = BookmarkState(
            isBookmark = false
        )
    }
}
