package com.mrl.pixiv.common.middleware.bookmark

import com.mrl.pixiv.common.data.Action

sealed class BookmarkAction : Action {
    data class IllustBookmarkAddIntent(val illustId: Long) : BookmarkAction()
    data class IllustBookmarkDeleteIntent(val illustId: Long) : BookmarkAction()
    data class UpdateState(val state: BookmarkState) : BookmarkAction()
}
