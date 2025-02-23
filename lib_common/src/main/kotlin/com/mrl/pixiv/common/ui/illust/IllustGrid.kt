package com.mrl.pixiv.common.ui.illust

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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.ui.components.m3.Surface
import com.mrl.pixiv.common.ui.item.SquareIllustItem
import com.mrl.pixiv.common.util.isEven
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import org.koin.compose.koinInject

@Composable
fun IllustGrid(
    illusts: LazyPagingItems<Illust>,
    spanCount: Int,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    horizontalPadding: Dp = 0.dp,
    bookmarkState: BookmarkState = koinInject(),
    paddingValues: PaddingValues = PaddingValues(1.dp),
    navToPictureScreen: (Illust, String) -> Unit,
    leadingContent: (LazyGridScope.() -> Unit)? = null,
) {
    var currentLoadingItem by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(illusts.itemCount) {
        currentLoadingItem = if (illusts.itemCount.isEven()) {
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

        if (illusts.loadState.refresh is LoadState.Loading && illusts.itemCount == 0) {
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
        }
        items(
            illusts.itemCount,
            key = illusts.itemKey { it.id }
        ) { index ->
            val illust = illusts[index] ?: return@items
            val isBookmarked = bookmarkState.state[illust.id] ?: illust.isBookmarked
            SquareIllustItem(
                illust = illust,
                isBookmarked = isBookmarked,
                onBookmarkClick = { restrict: String, tags: List<String>? ->
                    if (isBookmarked) {
                        bookmarkState.deleteBookmarkIllust(illust.id)
                    } else {
                        bookmarkState.bookmarkIllust(illust.id, restrict, tags)
                    }
                },
                spanCount = spanCount,
                horizontalPadding = horizontalPadding,
                paddingValues = paddingValues,
                shouldShowTip = index == 0,
                navToPictureScreen = navToPictureScreen,
            )
        }

        if (illusts.loadState.refresh !is LoadState.Loading && !illusts.loadState.append.endOfPaginationReached) {
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
}