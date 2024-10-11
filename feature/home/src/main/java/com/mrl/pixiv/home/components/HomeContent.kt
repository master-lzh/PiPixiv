package com.mrl.pixiv.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import com.mrl.pixiv.data.Illust

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeContent(
    recommendImageList: LazyPagingItems<Illust>,
    navToPictureScreen: (Illust, String) -> Unit,
    bookmarkState: BookmarkState,
    lazyStaggeredGridState: LazyStaggeredGridState,
) {
    RecommendGrid(
        recommendImageList = recommendImageList,
        navToPictureScreen = navToPictureScreen,
        bookmarkState = bookmarkState,
        lazyStaggeredGridState = lazyStaggeredGridState,
    )
}