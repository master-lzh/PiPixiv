package com.mrl.pixiv.collection.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import org.koin.core.annotation.Single

@Single
class CollectionReducer : Reducer<CollectionState, CollectionAction> {
    override fun CollectionState.reduce(action: CollectionAction): CollectionState {
        return when (action) {
            is CollectionAction.UpdateRestrict -> copy(
                restrict = action.restrict,
                filterTag = ""
            )

            is CollectionAction.UpdateFilterTag -> copy(
                restrict = action.restrict,
                filterTag = action.filterTag
            )

            is CollectionAction.UpdateUserBookmarksNovels -> copy(userBookmarksNovels = action.userBookmarksNovels)
            is CollectionAction.UpdateUserBookmarkTagsIllust -> copy(userBookmarkTagsIllust = action.userBookmarkTagsIllust)
            else -> this
        }
    }
}