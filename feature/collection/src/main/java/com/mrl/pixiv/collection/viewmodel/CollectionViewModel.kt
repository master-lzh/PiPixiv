package com.mrl.pixiv.collection.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Novel
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.user.BookmarkTag
import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.repository.paging.CollectionIllustPagingSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Stable
data class CollectionState(
    val userId: Long,
    @Restrict val restrict: String,
    val filterTag: String,
    val userBookmarksNovels: ImmutableList<Novel>,
    val userBookmarkTagsIllust: ImmutableList<BookmarkTag>,
) : State {


    companion object {
        val INITIAL = CollectionState(
            userId = Long.MIN_VALUE,
            restrict = Restrict.PUBLIC,
            filterTag = "",
            userBookmarksNovels = persistentListOf(),
            userBookmarkTagsIllust = persistentListOf()
        )
    }
}

sealed class CollectionAction : Action {
    data class LoadUserBookmarksTagsIllust(@Restrict val restrict: String) : CollectionAction()

    data class UpdateRestrict(@Restrict val restrict: String) : CollectionAction()
    data class UpdateFilterTag(
        @Restrict val restrict: String,
        val filterTag: String
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
    initialState = CollectionState.INITIAL.copy(userId = uid),
    reducer = reducer,
    middlewares = listOf(middleware),
), KoinComponent {
    val userBookmarksIllusts = Pager(PagingConfig(pageSize = 20)) {
        CollectionIllustPagingSource(
            get(), uid, UserBookmarksIllustQuery(
                restrict = state().restrict,
                userId = uid,
                tag = state().filterTag
            )
        )
    }.flow.cachedIn(viewModelScope)
}