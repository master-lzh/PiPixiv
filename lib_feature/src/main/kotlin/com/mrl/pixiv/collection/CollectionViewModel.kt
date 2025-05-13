package com.mrl.pixiv.collection

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mrl.pixiv.common.data.Novel
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.data.user.BookmarkTag
import com.mrl.pixiv.common.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.common.repository.PixivRepository
import com.mrl.pixiv.common.repository.paging.CollectionIllustPagingSource
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.ViewIntent
import com.mrl.pixiv.common.viewmodel.state
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.android.annotation.KoinViewModel

@Stable
data class CollectionState(
    @Restrict val restrict: String = Restrict.PUBLIC,
    val filterTag: String = "",
    val userBookmarksNovels: ImmutableList<Novel> = persistentListOf(),
    val userBookmarkTagsIllust: ImmutableList<BookmarkTag> = persistentListOf(),
)

sealed class CollectionAction : ViewIntent {
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
    private val uid: Long,
) : BaseMviViewModel<CollectionState, CollectionAction>(
    initialState = CollectionState(),
) {
    val userBookmarksIllusts = Pager(PagingConfig(pageSize = 20)) {
        CollectionIllustPagingSource(
            uid, UserBookmarksIllustQuery(
                restrict = state.restrict,
                userId = uid,
                tag = state.filterTag
            )
        )
    }.flow.cachedIn(viewModelScope)

    override suspend fun handleIntent(intent: CollectionAction) {
        when (intent) {
            is CollectionAction.LoadUserBookmarksTagsIllust -> loadUserBookmarkTagsIllust(intent.restrict)

            is CollectionAction.UpdateRestrict ->
                updateState {
                    copy(
                        restrict = intent.restrict,
                        filterTag = ""
                    )
                }

            is CollectionAction.UpdateFilterTag ->
                updateState {
                    copy(
                        restrict = intent.restrict,
                        filterTag = intent.filterTag
                    )
                }

            is CollectionAction.UpdateUserBookmarksNovels ->
                updateState { copy(userBookmarksNovels = intent.userBookmarksNovels) }

            is CollectionAction.UpdateUserBookmarkTagsIllust ->
                updateState {
                    copy(userBookmarkTagsIllust = intent.userBookmarkTagsIllust)
                }
        }
    }

    private fun loadUserBookmarkTagsIllust(@Restrict restrict: String) {
        launchIO {
            val resp = PixivRepository.getUserBookmarkTagsIllust(
                userId = uid,
                restrict = restrict
            )
            updateState {
                copy(
                    restrict = restrict,
                    userBookmarkTagsIllust = resp.bookmarkTags.toImmutableList()
                )
            }
        }
    }
}