package com.mrl.pixiv.common_ui.illust

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.ui.components.m3.Surface
import com.mrl.pixiv.common_ui.item.SquareIllustItem
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.util.OnScrollToBottom
import com.mrl.pixiv.util.isEven
import kotlinx.collections.immutable.ImmutableList

@Composable
fun IllustGrid(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    illusts: ImmutableList<Illust>,
    bookmarkState: BookmarkState,
    dispatch: (BookmarkAction) -> Unit,
    spanCount: Int,
    horizontalPadding: Dp = 0.dp,
    paddingValues: PaddingValues = PaddingValues(1.dp),
    navToPictureScreen: (Illust) -> Unit,
    canLoadMore: Boolean = true,
    onLoadMore: () -> Unit,
    loading: Boolean = false,
    leadingContent: (LazyGridScope.() -> Unit)? = null,
) {
    var currentLoadingItem by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(illusts.size) {
        currentLoadingItem = if (illusts.size.isEven()) {
            4
        } else {
            5
        }
    }
    LazyVerticalGrid(
        state = lazyGridState,
        modifier = modifier,
        columns = GridCells.Fixed(spanCount),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        leadingContent?.invoke(this)

        if (loading) {
            item(key = "loading", span = { GridItemSpan(spanCount) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            itemsIndexed(illusts, key = { _, item -> "illust_${item.id}" }) { index, illust ->
                SquareIllustItem(
                    illust = illust,
                    bookmarkState = bookmarkState,
                    dispatch = dispatch,
                    spanCount = spanCount,
                    horizontalPadding = horizontalPadding,
                    paddingValues = paddingValues,
                    shouldShowTip = index == 0,
                    navToPictureScreen = navToPictureScreen,
                )
            }
        }

        if (canLoadMore) {
            items(currentLoadingItem, key = { "loading_$it" }) {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 4.dp,
                    propagateMinConstraints = false,
                ) {

                }
            }
        }
        item(key = "spacer") {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    lazyGridState.OnScrollToBottom(loadingItemCount = currentLoadingItem) {
        if (canLoadMore && !loading) {
            onLoadMore()
        }
    }
}