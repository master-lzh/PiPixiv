package com.mrl.pixiv.common.viewmodel.bookmark

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.repository.IllustRepository

class BookmarkMiddleware(
    private val illustRepository: IllustRepository,
) : Middleware<BookmarkState, BookmarkAction>() {
    override suspend fun process(state: BookmarkState, action: BookmarkAction) {
        when (action) {
            is BookmarkAction.IllustBookmarkAddIntent -> bookmarkIllust(
                action.illustId,
                action.restrict,
                action.tags
            )

            is BookmarkAction.IllustBookmarkDeleteIntent -> deleteBookmarkIllust(action.illustId)

            else -> {}
        }
    }

    private fun deleteBookmarkIllust(illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRepository.postIllustBookmarkDelete(
                    IllustBookmarkDeleteReq(
                        illustId
                    )
                )
            ) {
                dispatch(BookmarkAction.UpdateBookmarkState(illustId, false))
            }
        }
    }

    private fun bookmarkIllust(illustId: Long, restrict: String, tags: List<String>?) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRepository.postIllustBookmarkAdd(
                    IllustBookmarkAddReq(illustId, restrict, tags)
                )
            ) {
                dispatch(BookmarkAction.UpdateBookmarkState(illustId, true))
            }
        }
    }
}