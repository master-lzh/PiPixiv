package com.mrl.pixiv.picture

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.repository.IllustCacheRepo
import com.mrl.pixiv.common.ui.LocalNavigator
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HorizontalSwipePictureScreen(
    illusts: ImmutableList<Illust>,
    index: Int,
    prefix: String,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(index) { illusts.size }
    val navigator = LocalNavigator.current
    val onBack: () -> Unit = {
        IllustCacheRepo.removeList(prefix)
        navigator.popBackStack()
    }
    BackHandler(onBack = onBack)
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
    ) {
        PictureScreen(
            illust = illusts[it],
            onBack = onBack
        )
    }
}