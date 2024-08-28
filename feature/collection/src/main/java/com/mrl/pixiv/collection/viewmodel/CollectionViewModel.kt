package com.mrl.pixiv.collection.viewmodel

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Novel
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.user.BookmarkTag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel

@Stable
data class CollectionState(
    val userId: Long,
    val refreshing: Boolean,
    @Restrict val restrict: String,
    val filterTag: String,
    val userBookmarksIllusts: ImmutableList<Illust>,
    val illustNextUrl: String?,
    val userBookmarksNovels: ImmutableList<Novel>,
    val userBookmarkTagsIllust: ImmutableList<BookmarkTag>,
    val loading: Boolean,
) : State {


    companion object {
        val INITIAL = CollectionState(
            userId = Long.MIN_VALUE,
            refreshing = true,
            restrict = Restrict.PUBLIC,
            filterTag = "",
            userBookmarksIllusts = persistentListOf(),
            illustNextUrl = null,
            userBookmarksNovels = persistentListOf(),
            userBookmarkTagsIllust = persistentListOf(),
            loading = true
        )
    }
}

sealed class CollectionAction : Action {
    data class LoadUserBookmarksIllusts(
        @Restrict val restrict: String,
        val filterTag: String
    ) : CollectionAction()

    data class LoadMoreUserBookmarksIllusts(val nextUrl: String) : CollectionAction()
    data class LoadUserBookmarksTagsIllust(@Restrict val restrict: String) : CollectionAction()


    data class UpdateUserId(val userId: Long) : CollectionAction()
    data class UpdateRestrict(@Restrict val restrict: String) : CollectionAction()
    data class UpdateFilterTag(
        @Restrict val restrict: String,
        val filterTag: String
    ) : CollectionAction()

    data class UpdateUserBookmarksIllusts(
        val userBookmarksIllusts: ImmutableList<Illust>,
        val illustNextUrl: String?
    ) : CollectionAction()

    data class UpdateMoreUserBookmarksIllusts(
        val userBookmarksIllusts: ImmutableList<Illust>,
        val illustNextUrl: String?
    ) : CollectionAction()

    data class UpdateUserBookmarksNovels(
        val userBookmarksNovels: ImmutableList<Novel>
    ) : CollectionAction()

    data class UpdateUserBookmarkTagsIllust(
        val userBookmarkTagsIllust: ImmutableList<BookmarkTag>
    ) : CollectionAction()
}

@KoinViewModel
class CollectionViewModel(
    uid: Long,
    middleware: CollectionMiddleware,
    reducer: CollectionReducer
) : BaseViewModel<CollectionState, CollectionAction>(
    initialState = CollectionState.INITIAL,
    reducer = reducer,
    middlewares = listOf(middleware),
) {
    init {
        dispatch(CollectionAction.UpdateUserId(uid))
        dispatch(CollectionAction.LoadUserBookmarksIllusts(Restrict.PUBLIC, ""))
    }
}