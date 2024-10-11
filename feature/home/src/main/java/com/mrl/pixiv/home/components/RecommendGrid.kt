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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.util.DisplayUtil

private const val LOADING_ITEM_COUNT = 4
private const val INCLUDE_EDGE = true

@Composable
fun RecommendGrid(
    recommendImageList: LazyPagingItems<Illust>,
    navToPictureScreen: (Illust, String) -> Unit,
    bookmarkState: BookmarkState,
    lazyStaggeredGridState: LazyStaggeredGridState,
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
        items(recommendImageList.itemCount, key = recommendImageList.itemKey { it.id }) {
            val illust = recommendImageList[it]
            if (illust != null) {
                val isBookmarked = bookmarkState.state[illust.id] ?: illust.isBookmarked
                RecommendImageItem(
                    width = width,
                    navToPictureScreen = navToPictureScreen,
                    illust = illust,
                    isBookmarked = isBookmarked,
                    onBookmarkClick = { restrict, tags ->
                        if (isBookmarked) {
                            bookmarkState.deleteBookmarkIllust(illust.id)
                        } else {
                            bookmarkState.bookmarkIllust(illust.id, restrict, tags)
                        }
                    }
                )
            }
        }

        when (recommendImageList.loadState.append) {
            is LoadState.Loading -> { // Pagination Loading UI
                items(LOADING_ITEM_COUNT) {
                    RecommendSkeleton(size = width)
                }
            }

            else -> {}
        }

//        if (recommendImageList.isNotEmpty()) {
//            itemsIndexed(
//                List(LOADING_ITEM_COUNT) { 0 },
//                key = { index, _ -> "loading-$index" }) { _, _ ->
//                RecommendSkeleton(size = width)
//            }
//        }
    }
}