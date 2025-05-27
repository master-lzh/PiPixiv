package com.mrl.pixiv.home.components

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.mrl.pixiv.common.compose.ui.illust.RectangleIllustItem
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.kts.spaceBy
import com.mrl.pixiv.common.util.NavigateToHorizontalPictureScreen
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import com.mrl.pixiv.common.viewmodel.bookmark.isBookmark

private const val LOADING_ITEM_COUNT_PORTRAIT = 2
private const val LOADING_ITEM_COUNT_LANDSCAPE = 4

@Composable
fun RecommendGrid(
    recommendImageList: LazyPagingItems<Illust>,
    navToPictureScreen: NavigateToHorizontalPictureScreen,
    lazyStaggeredGridState: LazyStaggeredGridState,
) {
    val spanCount = when (LocalConfiguration.current.orientation) {
        ORIENTATION_PORTRAIT -> LOADING_ITEM_COUNT_PORTRAIT
        ORIENTATION_LANDSCAPE -> LOADING_ITEM_COUNT_LANDSCAPE
        else -> LOADING_ITEM_COUNT_PORTRAIT
    }

    LazyVerticalStaggeredGrid(
        state = lazyStaggeredGridState,
        contentPadding = PaddingValues(5.dp),
        columns = StaggeredGridCells.Fixed(spanCount),
        verticalItemSpacing = 3.dp,
        horizontalArrangement = 5f.spaceBy,
        modifier = Modifier.fillMaxSize()
    ) {
        items(recommendImageList.itemCount, key = recommendImageList.itemKey { it.id }) {
            val illust = recommendImageList[it] ?: return@items
            val isBookmarked = illust.isBookmark
            RectangleIllustItem(
                navToPictureScreen = { prefix ->
                    navToPictureScreen(recommendImageList.itemSnapshotList.items, it, prefix)
                },
                illust = illust,
                isBookmarked = isBookmarked,
                onBookmarkClick = { restrict, tags ->
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