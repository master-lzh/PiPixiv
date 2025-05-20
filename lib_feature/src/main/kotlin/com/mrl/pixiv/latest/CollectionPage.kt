package com.mrl.pixiv.latest

import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.mrl.pixiv.collection.CollectionViewModel
import com.mrl.pixiv.common.compose.LocalNavigator
import com.mrl.pixiv.common.compose.ui.illust.RectangleIllustItem
import com.mrl.pixiv.common.util.navigateToPictureScreen
import com.mrl.pixiv.common.viewmodel.asState
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState.isBookmark
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CollectionPage(
    uid: Long,
    modifier: Modifier = Modifier,
    viewModel: CollectionViewModel = koinViewModel { parametersOf(uid) },
    navHostController: NavHostController = LocalNavigator.current
) {
    val state = viewModel.asState()
    val userBookmarksIllusts = viewModel.userBookmarksIllusts.collectAsLazyPagingItems()
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
    ) {
        items(
            count = userBookmarksIllusts.itemCount,
            key = userBookmarksIllusts.itemKey { it.id }
        ) { index ->
            val illust = userBookmarksIllusts[index] ?: return@items
            val isBookmarked = illust.isBookmark
            RectangleIllustItem(
                illust = illust,
                isBookmarked = isBookmarked,
                navToPictureScreen = { prefix ->
                    navHostController.navigateToPictureScreen(
                        userBookmarksIllusts.itemSnapshotList.items,
                        index,
                        prefix
                    )
                },
                onBookmarkClick = { restrict: String, tags: List<String>? ->
                    if (isBookmarked) {
                        BookmarkState.deleteBookmarkIllust(illust.id)
                    } else {
                        BookmarkState.bookmarkIllust(illust.id, restrict, tags)
                    }
                }
            )
        }
    }
}