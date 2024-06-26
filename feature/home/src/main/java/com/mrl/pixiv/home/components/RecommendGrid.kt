package com.mrl.pixiv.home.components

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.util.DisplayUtil
import com.mrl.pixiv.util.OnScrollToBottom
import kotlinx.collections.immutable.ImmutableList

private const val LOADING_ITEM_COUNT = 4
private const val INCLUDE_EDGE = true

@Composable
fun RecommendGrid(
    navToPictureScreen: (Illust, String) -> Unit,
    bookmarkState: BookmarkState,
    lazyStaggeredGridState: LazyStaggeredGridState,
    recommendImageList: ImmutableList<Illust>,
    onBookmarkClick: OnBookmarkClick,
    onScrollToBottom: () -> Unit,
    dispatch: (BookmarkAction) -> Unit
) {
    val spanCount = when (LocalConfiguration.current.orientation) {
        ORIENTATION_PORTRAIT -> 2
        ORIENTATION_LANDSCAPE -> 4
        else -> 2
    }
    val paddingValues = 5.dp
    val width =
        (DisplayUtil.getScreenWidthDp() - paddingValues * (spanCount + if (INCLUDE_EDGE) 1 else -1)) / spanCount

    LazyVerticalStaggeredGrid(
        state = lazyStaggeredGridState,
        contentPadding = PaddingValues(paddingValues),
        columns = StaggeredGridCells.Fixed(spanCount),
        verticalItemSpacing = 3.dp,
        modifier = Modifier.fillMaxSize()
    ) {
        items(recommendImageList, key = { it.id }) {
            RecommendImageItem(
                width,
                navToPictureScreen,
                it,
                bookmarkState,
                onBookmarkClick,
                dispatch
            )
        }

        if (recommendImageList.isNotEmpty()) {
            itemsIndexed(
                List(LOADING_ITEM_COUNT) { 0 },
                key = { index, _ -> "loading-$index" }) { _, _ ->
                RecommendSkeleton(size = width)
            }
        }
    }
    lazyStaggeredGridState.OnScrollToBottom(loadingItemCount = LOADING_ITEM_COUNT) {
        onScrollToBottom()
    }
}