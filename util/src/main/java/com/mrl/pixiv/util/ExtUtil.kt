package com.mrl.pixiv.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import java.net.URL
import java.net.URLDecoder

val Number.dp_: Int
    get() = DisplayUtil.dp2px(this.toFloat())

val Number.sp_: Int
    get() = DisplayUtil.sp2px(this.toFloat())

val Number.second: Long
    get() = this.toLong() * 1000

val Number.minute: Long
    get() = this.second * 60

val Number.hour: Long
    get() = this.minute * 60

val Number.day: Long
    get() = this.hour * 24

val Number.week: Long
    get() = this.day * 7

val Number.month: Long
    get() = this.day * 30

val String.queryParams: Map<String, String>
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
            queryMap
        } catch (e: Exception) {
            mapOf()
        }
    }

val Any.TAG: String
    get() = this::class.java.simpleName

@OptIn(ExperimentalFoundationApi::class)
val LazyStaggeredGridState.isScrollToTop: Boolean
    get() = firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0

@OptIn(ExperimentalFoundationApi::class)
val LazyStaggeredGridState.isScrollToBottom: Boolean
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyStaggeredGridState.OnScrollToBottom(block: () -> Unit) {
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false
            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            block()
        }
    }
}