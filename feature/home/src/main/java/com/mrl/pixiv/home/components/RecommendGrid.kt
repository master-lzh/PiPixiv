package com.mrl.pixiv.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.home.state.RecommendImageItemState
import com.mrl.pixiv.util.OnScrollToBottom

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendGrid(
    scaffoldState: ScaffoldState,
    lazyStaggeredGridState: LazyStaggeredGridState,
    recommendImageList: List<RecommendImageItemState>,
    loadMore: Boolean,
    onBookmarkClick: (Long, Boolean) -> Unit,
    onScrollToBottom: () -> Unit,
) {
    LazyVerticalStaggeredGrid(
        state = lazyStaggeredGridState,
        contentPadding = PaddingValues(5.dp),
        columns = StaggeredGridCells.Fixed(SPAN_COUNT),
        verticalItemSpacing = 3.dp,
    ) {
        items(recommendImageList, key = { it.id }) {
            RecommendImageItem(scaffoldState, it, onBookmarkClick)
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