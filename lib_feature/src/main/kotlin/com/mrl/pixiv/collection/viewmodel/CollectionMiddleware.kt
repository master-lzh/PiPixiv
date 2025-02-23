package com.mrl.pixiv.collection.viewmodel

import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.data.user.UserBookmarkTagsQuery
import com.mrl.pixiv.common.repository.CollectionRepository
import com.mrl.pixiv.common.viewmodel.Middleware
import kotlinx.collections.immutable.toImmutableList
import org.koin.core.annotation.Factory

@Factory
class CollectionMiddleware(
    private val collectionRepository: CollectionRepository
) : Middleware<CollectionState, CollectionAction>() {
    override suspend fun process(state: CollectionState, action: CollectionAction) {
        when (action) {
            is CollectionAction.LoadUserBookmarksTagsIllust -> loadUserBookmarkTagsIllust(
                action.restrict,
                state.userId
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

}