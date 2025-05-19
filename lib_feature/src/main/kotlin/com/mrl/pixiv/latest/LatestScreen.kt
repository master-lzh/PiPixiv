package com.mrl.pixiv.latest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LatestScreen(
    modifier: Modifier = Modifier
) {
    val pages = remember { LatestPage.entries.toList() }
    val pagerState = rememberPagerState { pages.size }

    Scaffold(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(it)) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {

            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {

            }
        }
    }
}