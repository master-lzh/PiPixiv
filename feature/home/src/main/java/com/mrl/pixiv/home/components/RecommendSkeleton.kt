package com.mrl.pixiv.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.ui.components.Surface
import com.mrl.pixiv.util.DisplayUtil

@Preview
@PreviewLightDark
@PreviewScreenSizes
@Composable
fun RecommendSkeleton(
    spanCount: Int = 2
) {
    val size =
        (DisplayUtil.getScreenWidthDp() - SPACING_HORIZONTAL_DP * (spanCount + if (INCLUDE_EDGE) 1 else -1)) / spanCount
    Surface(
        Modifier
            .size(size)
            .padding(horizontal = 5.dp)
            .padding(bottom = 5.dp),
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        propagateMinConstraints = false,
    ) {

    }
}