package com.mrl.pixiv.common.viewmodel.bookmark

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.data.Restrict

sealed class BookmarkAction : Action {
    data class IllustBookmarkAddIntent(
        val illustId: Long,
        val restrict: String = Restrict.PUBLIC,
        val tags: List<String>? = null,
    ) : BookmarkAction()
    data class IllustBookmarkDeleteIntent(val illustId: Long) : BookmarkAction()

    data class UpdateBookmarkState(val illustId: Long, val isBookmarked: Boolean) : BookmarkAction()
}
