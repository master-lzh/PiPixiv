package com.mrl.pixiv.latest

import com.mrl.pixiv.common.data.AppViewMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class LatestPageTest {

    @Test
    fun tabsGroupUserActivityBeforeCollectionAndDiscovery() {
        assertEquals(
            listOf(
                LatestPage.Trend,
                LatestPage.Following,
                LatestPage.Collection,
            ),
            LatestPage.pagesFor(AppViewMode.ILLUST),
        )
        assertEquals(
            listOf(
                LatestPage.Trend,
                LatestPage.NovelWatchlist,
                LatestPage.Following,
                LatestPage.Collection,
                LatestPage.NovelNew,
            ),
            LatestPage.pagesFor(AppViewMode.NOVEL),
        )
    }

    @Test
    fun novelTabsKeepIndependentScrollStates() {
        val viewModel = LatestViewModel()

        assertNotSame(
            viewModel.newNovelLazyListState,
            viewModel.watchlistNovelLazyListState,
        )
    }

    @Test
    fun illustrationAndNovelModesUsePagerStatesWithMatchingPageCounts() {
        val viewModel = LatestViewModel()
        val illustPagerState = viewModel.pagerStateFor(AppViewMode.ILLUST)
        val novelPagerState = viewModel.pagerStateFor(AppViewMode.NOVEL)

        assertNotSame(illustPagerState, novelPagerState)
        assertEquals(3, illustPagerState.pageCount)
        assertEquals(5, novelPagerState.pageCount)
    }
}
