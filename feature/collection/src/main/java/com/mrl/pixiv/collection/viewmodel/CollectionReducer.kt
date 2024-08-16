package com.mrl.pixiv.collection.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList

class CollectionReducer : Reducer<CollectionState, CollectionAction> {
    override fun CollectionState.reduce(action: CollectionAction): CollectionState {
        return when (action) {
            is CollectionAction.LoadUserBookmarksIllusts -> copy(
                refreshing = true,
                loading = true
            )

            is CollectionAction.UpdateUserId -> copy(userId = action.userId)
            is CollectionAction.UpdateRestrict -> copy(
                restrict = action.restrict,
                filterTag = ""
            )

            is CollectionAction.UpdateFilterTag -> copy(
                restrict = action.restrict,
                filterTag = action.filterTag
            )

            is CollectionAction.UpdateUserBookmarksIllusts -> copy(
                userBookmarksIllusts = action.userBookmarksIllusts,
                refreshing = false,
                loading = false,
                illustNextUrl = action.illustNextUrl
            )

            is CollectionAction.UpdateMoreUserBookmarksIllusts -> copy(
                userBookmarksIllusts = (userBookmarksIllusts + action.userBookmarksIllusts).toImmutableList(),
                illustNextUrl = action.illustNextUrl
            )

            is CollectionAction.UpdateUserBookmarksNovels -> copy(userBookmarksNovels = action.userBookmarksNovels)
            is CollectionAction.UpdateUserBookmarkTagsIllust -> copy(userBookmarkTagsIllust = action.userBookmarkTagsIllust)
            else -> this
        }
    }
}