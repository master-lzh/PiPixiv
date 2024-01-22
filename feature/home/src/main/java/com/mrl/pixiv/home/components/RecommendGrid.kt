package com.mrl.pixiv.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.util.OnScrollToBottom

@Composable
fun RecommendGrid(
    navToPictureScreen: (Illust) -> Unit,
    bookmarkState: BookmarkState,
    lazyStaggeredGridState: LazyStaggeredGridState,
    recommendImageList: List<Illust>,
    loadMore: Boolean,
    onBookmarkClick: (Long, Boolean) -> Unit,
    onScrollToBottom: () -> Unit,
) {
    LazyVerticalStaggeredGrid(
        state = lazyStaggeredGridState,
        contentPadding = PaddingValues(5.dp),
        columns = StaggeredGridCells.Fixed(SPAN_COUNT),
        verticalItemSpacing = 3.dp,
        modifier = Modifier.fillMaxSize()
    ) {
        items(recommendImageList, key = { it.id }) {
            RecommendImageItem(navToPictureScreen, it, bookmarkState, onBookmarkClick)
        }
        if (loadMore) {
            item(key = "loading", span = StaggeredGridItemSpan.FullLine) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
    lazyStaggeredGridState.OnScrollToBottom {
        onScrollToBottom()
    }
}