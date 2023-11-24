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
            is BookmarkAction.IllustBookmarkAddIntent -> bookmarkIllust(
                state,
                action.illustId
            )

            is BookmarkAction.IllustBookmarkDeleteIntent -> deleteBookmarkIllust(
                state,
                action.illustId
            )

            else -> {}
        }
    }

    private fun deleteBookmarkIllust(state: BookmarkState, illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.postIllustBookmarkDelete(
                    IllustBookmarkDeleteReq(
                        illustId
                    )
                )
            ) {
                dispatch(BookmarkAction.UpdateState(state.apply {
                    bookmarkStatus[illustId] = false
                }))
            }
        }
    }

    private fun bookmarkIllust(state: BookmarkState, illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.postIllustBookmarkAdd(IllustBookmarkAddReq(illustId))
            ) {
                dispatch(BookmarkAction.UpdateState(state.apply {
                    bookmarkStatus[illustId] = true
                }))
            }
        }
    }
}