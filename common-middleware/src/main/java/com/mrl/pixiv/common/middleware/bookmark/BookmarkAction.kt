package com.mrl.pixiv.common.middleware.bookmark

import com.mrl.pixiv.common.data.Action

sealed class BookmarkAction : Action {
    data class IllustBookmarkAddIntent(val illustId: Long) : BookmarkAction()
    data class IllustBookmarkDeleteIntent(val illustId: Long) : BookmarkAction()

    data class UpdateBookmarkState(val illustId: Long, val isBookmarked: Boolean) : BookmarkAction()
}
