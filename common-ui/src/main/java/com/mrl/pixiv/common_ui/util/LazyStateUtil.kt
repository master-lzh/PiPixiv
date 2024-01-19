package com.mrl.pixiv.common_ui.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun LazyListState.OnScrollToBottom(
    onScrollToBottom: () -> Unit
) {
    val isScrollToBottom by remember {
        derivedStateOf {
            layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(isScrollToBottom) {
        if (isScrollToBottom) {
            onScrollToBottom()
        }
    }
}

@Composable
fun LazyGridState.OnScrollToBottom(
    onScrollToBottom: () -> Unit
) {
    val isScrollToBottom by remember {
        derivedStateOf {
            layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(isScrollToBottom) {
        if (isScrollToBottom) {
            onScrollToBottom()
        }
    }
}

@Composable
fun LazyStaggeredGridState.OnScrollToBottom(
    onScrollToBottom: () -> Unit
) {
    val isScrollToBottom by remember {
        derivedStateOf {
            layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(isScrollToBottom) {
        if (isScrollToBottom) {
            onScrollToBottom()
        }
    }
}