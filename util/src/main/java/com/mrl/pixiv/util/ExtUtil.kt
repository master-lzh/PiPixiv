package com.mrl.pixiv.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import java.net.URL
import java.net.URLDecoder

val String.queryParams: ImmutableMap<String, String>
    get() {
        val queryMap = mutableMapOf<String, String>()
        return try {
            val query = URLDecoder.decode(URL(this).query, "UTF-8")
            if (query != null) {
                for (param in query.split("&")) {
                    val keyValuePair = param.split("=")
                    queryMap[keyValuePair[0]] = if (keyValuePair.size > 1) keyValuePair[1] else ""
                }
            }
            queryMap.toImmutableMap()
        } catch (e: Exception) {
            persistentMapOf()
        }
    }

val Any.TAG: String
    get() = this::class.simpleName ?: "TAG"

val LazyStaggeredGridState.isScrollToTop: Boolean
    @Composable
    get() {
        val isTop by remember {
            derivedStateOf {
                firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
            }
        }
        return isTop
    }

val LazyStaggeredGridState.isScrollToBottom: Boolean
    @Composable
    get() {
        val isBottom by remember {
            derivedStateOf {
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                    ?: return@derivedStateOf false
                lastVisibleItem.index >= layoutInfo.totalItemsCount - 1
            }
        }
        return isBottom
    }

@Composable
fun LazyStaggeredGridState.OnScrollToBottom(
    loadingItemCount: Int,
    debounceTime: Long = 300,
    block: () -> Unit
) {
    val updatedBlock by rememberUpdatedState(block)
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - loadingItemCount
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { shouldLoadMore }
            .debounce(debounceTime)
            .distinctUntilChanged()
            .filter { it }
            .collect {
                updatedBlock()
            }
    }
}

@Composable
fun LazyGridState.OnScrollToBottom(
    loadingItemCount: Int,
    debounceTime: Long = 300,
    block: () -> Unit = {},
) {
    val updatedBlock by rememberUpdatedState(block)
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - loadingItemCount
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { shouldLoadMore }
            .debounce(debounceTime)
            .distinctUntilChanged()
            .filter { it }
            .collect {
                updatedBlock()
            }
    }
}

@Composable
fun LazyListState.OnScrollToBottom(
    loadingItemCount: Int,
    debounceTime: Long = 300,
    block: () -> Unit = {},
) {
    val updatedBlock by rememberUpdatedState(block)
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - loadingItemCount
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { shouldLoadMore }
            .debounce(debounceTime)
            .distinctUntilChanged()
            .filter { it }
            .collect {
                updatedBlock()
            }
    }
}