package com.mrl.pixiv.picture

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrl.pixiv.common.data.Illust
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HorizontalSwipePictureScreen(
    illusts: ImmutableList<Illust>,
    index: Int,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(index) { illusts.size }
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
    ) {
        PictureScreen(
            illust = illusts[it],
        )
    }
}