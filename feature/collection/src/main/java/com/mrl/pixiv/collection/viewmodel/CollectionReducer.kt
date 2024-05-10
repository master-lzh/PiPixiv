package com.mrl.pixiv.collection.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList

class CollectionReducer : Reducer<CollectionState, CollectionAction> {
    override fun reduce(state: CollectionState, action: CollectionAction): CollectionState {
        return when (action) {
            is CollectionAction.LoadUserBookmarksIllusts -> state.copy(loading = true)
            is CollectionAction.UpdateUserId -> state.copy(userId = action.userId)
            is CollectionAction.UpdateRestrict -> state.copy(
                restrict = action.restrict,
                filterTag = ""
            )

            is CollectionAction.UpdateFilterTag -> state.copy(
                restrict = action.restrict,
                filterTag = action.filterTag
            )

            is CollectionAction.UpdateUserBookmarksIllusts -> state.copy(
                userBookmarksIllusts = action.userBookmarksIllusts,
                loading = false,
                illustNextUrl = action.illustNextUrl
            )

            is CollectionAction.UpdateMoreUserBookmarksIllusts -> state.copy(
                userBookmarksIllusts = (state.userBookmarksIllusts + action.userBookmarksIllusts).toImmutableList(),
                illustNextUrl = action.illustNextUrl
            )

            is CollectionAction.UpdateUserBookmarksNovels -> state.copy(userBookmarksNovels = action.userBookmarksNovels)
            is CollectionAction.UpdateUserBookmarkTagsIllust -> state.copy(userBookmarkTagsIllust = action.userBookmarkTagsIllust)
            else -> state
        }
    }
}