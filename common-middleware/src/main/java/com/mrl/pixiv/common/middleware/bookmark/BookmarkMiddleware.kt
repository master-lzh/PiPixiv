package com.mrl.pixiv.common.middleware.bookmark

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.repository.remote.IllustRemoteRepository

class BookmarkMiddleware(
    private val illustRemoteRepository: IllustRemoteRepository,
) : Middleware<BookmarkState, BookmarkAction>() {
    override suspend fun process(state: BookmarkState, action: BookmarkAction) {
        when (action) {
            is BookmarkAction.IllustBookmarkAddIntent -> bookmarkIllust(action.illustId)
            is BookmarkAction.IllustBookmarkDeleteIntent -> deleteBookmarkIllust(action.illustId)

            else -> {}
        }
    }

    private fun deleteBookmarkIllust(illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.postIllustBookmarkDelete(
                    IllustBookmarkDeleteReq(
                        illustId
                    )
                )
            ) {
                dispatch(BookmarkAction.UpdateBookmarkState(illustId, false))
            }
        }
    }

    private fun bookmarkIllust(illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.postIllustBookmarkAdd(IllustBookmarkAddReq(illustId))
            ) {
                dispatch(BookmarkAction.UpdateBookmarkState(illustId, true))
            }
        }
    }
}