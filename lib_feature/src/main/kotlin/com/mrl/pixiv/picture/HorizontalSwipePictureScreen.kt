package com.mrl.pixiv.picture

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.repository.IllustCacheRepo
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HorizontalSwipePictureScreen(
    illusts: ImmutableList<Illust>,
    index: Int,
    prefix: String,
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
    DisposableEffect(prefix) {
        onDispose {
            IllustCacheRepo.removeList(prefix)
        }
    }
}