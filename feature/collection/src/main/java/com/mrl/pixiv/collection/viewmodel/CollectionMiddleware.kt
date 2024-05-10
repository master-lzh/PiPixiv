package com.mrl.pixiv.collection.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.user.UserBookmarkTagsQuery
import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.repository.CollectionRepository
import com.mrl.pixiv.util.queryParams
import kotlinx.collections.immutable.toImmutableList

class CollectionMiddleware(
    private val collectionRepository: CollectionRepository
) : Middleware<CollectionState, CollectionAction>() {
    override suspend fun process(state: CollectionState, action: CollectionAction) {
        when (action) {
            is CollectionAction.LoadUserBookmarksIllusts -> loadUserBookmarksIllusts(
                action.restrict,
                action.filterTag,
                state.userId
            )

            is CollectionAction.LoadMoreUserBookmarksIllusts -> loadMoreUserBookmarksIllusts(
                action.nextUrl
            )

            is CollectionAction.LoadUserBookmarksTagsIllust -> loadUserBookmarkTagsIllust(
                action.restrict,
                state.userId
            )

            is CollectionAction.UpdateRestrict -> dispatch(
                CollectionAction.LoadUserBookmarksIllusts(
                    restrict = action.restrict,
                    filterTag = ""
                )
            )

            is CollectionAction.UpdateFilterTag -> dispatch(
                CollectionAction.LoadUserBookmarksIllusts(
                    restrict = action.restrict,
                    filterTag = action.filterTag
                )
            )

            else -> Unit
        }
    }

    private fun loadUserBookmarkTagsIllust(@Restrict restrict: String, userId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = collectionRepository.getUserBookmarkTagsIllust(
                    UserBookmarkTagsQuery(
                        userId = if (userId == Long.MIN_VALUE) collectionRepository.getUserInfo().uid else userId,
                        restrict = restrict
                    )
                )
            ) {
                dispatch(
                    CollectionAction.UpdateUserBookmarkTagsIllust(
                        userBookmarkTagsIllust = it.bookmarkTags.toImmutableList()
                    )
                )
            }
        }
    }

    private fun loadMoreUserBookmarksIllusts(nextUrl: String) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = collectionRepository.loadMoreUserBookmarksIllusts(nextUrl.queryParams)
            ) {
                dispatch(
                    CollectionAction.UpdateMoreUserBookmarksIllusts(
                        userBookmarksIllusts = it.illusts.toImmutableList(),
                        illustNextUrl = it.nextURL
                    )
                )
            }
        }
    }

    private fun loadUserBookmarksIllusts(
        @Restrict restrict: String,
        filterTag: String,
        userId: Long
    ) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = collectionRepository.getUserBookmarksIllusts(
                    UserBookmarksIllustQuery(
                        restrict = restrict,
                        tag = filterTag,
                        userId = if (userId == Long.MIN_VALUE) collectionRepository.getUserInfo().uid else userId,
                    )
                )
            ) {
                dispatch(
                    CollectionAction.UpdateUserBookmarksIllusts(
                        userBookmarksIllusts = it.illusts.toImmutableList(),
                        illustNextUrl = it.nextURL
                    )
                )
            }
        }
    }
}