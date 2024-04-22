package com.mrl.pixiv.common_ui.illust

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

@Composable
fun IllustGrid(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    illusts: List<Illust>,
    bookmarkState: BookmarkState,
    dispatch: (BookmarkAction) -> Unit,
    spanCount: Int,
    horizontalPadding: Dp = 0.dp,
    paddingValues: PaddingValues = PaddingValues(1.dp),
    navToPictureScreen: (Illust) -> Unit,
    canLoadMore: Boolean = true,
    onLoadMore: () -> Unit,
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
        items(illusts, key = { "illust_${it.id}" }) { illust ->
            SquareIllustItem(
                illust = illust,
                bookmarkState = bookmarkState,
                dispatch = dispatch,
                spanCount = spanCount,
                horizontalPadding = horizontalPadding,
                paddingValues = paddingValues,
                navToPictureScreen = navToPictureScreen,
            )
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
    }
    lazyGridState.OnScrollToBottom(loadingItemCount = currentLoadingItem) {
        onLoadMore()
    }
}