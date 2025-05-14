package com.mrl.pixiv.common.ui.illust

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.ui.item.SquareIllustItem
import com.mrl.pixiv.common.util.NavigateToHorizontalPictureScreen
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import com.mrl.pixiv.common.viewmodel.bookmark.requireBookmarkState

private const val KEY_LOADING = "loading"
private const val KEY_SPACER = "spacer"

fun LazyGridScope.illustGrid(
    illusts: LazyPagingItems<Illust>,
    navToPictureScreen: NavigateToHorizontalPictureScreen,
    enableLoading: Boolean = false,
) {
    if (enableLoading && illusts.loadState.refresh is LoadState.Loading && illusts.itemCount == 0) {
        item(key = KEY_LOADING, span = { GridItemSpan(maxLineSpan) }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
    if (illusts.itemCount > 0) {
        items(
            illusts.itemCount,
            key = illusts.itemKey { it.id }
        ) { index ->
            val illust = illusts[index] ?: return@items
            val isBookmarked = requireBookmarkState[illust.id] ?: illust.isBookmarked
            SquareIllustItem(
                illust = illust,
                isBookmarked = isBookmarked,
                onBookmarkClick = { restrict: String, tags: List<String>? ->
                    if (isBookmarked) {
                        BookmarkState.deleteBookmarkIllust(illust.id)
                    } else {
                        BookmarkState.bookmarkIllust(illust.id, restrict, tags)
                    }
                },
                navToPictureScreen = { prefix ->
                    navToPictureScreen(illusts.itemSnapshotList.items, index, prefix)
                },
                shouldShowTip = index == 0,
            )
        }
        item(key = KEY_SPACER) {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}