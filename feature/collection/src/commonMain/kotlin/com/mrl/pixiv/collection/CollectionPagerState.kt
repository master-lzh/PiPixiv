package com.mrl.pixiv.collection

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.mrl.pixiv.common.router.NavigationManager
import kotlinx.coroutines.flow.drop

@Composable
internal fun rememberCollectionPagerState(
    initialPage: Int,
    navigationManager: NavigationManager,
): PagerState {
    val pagerState = rememberPagerState(initialPage) { 2 }
    LaunchedEffect(pagerState, navigationManager) {
        // Keep a restored detail until the user actually changes the collection type.
        snapshotFlow { pagerState.settledPage }.drop(1).collect {
            navigationManager.closeCurrentDetailBranch()
        }
    }
    return pagerState
}
