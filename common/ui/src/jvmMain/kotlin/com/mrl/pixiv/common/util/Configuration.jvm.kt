package com.mrl.pixiv.common.util

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import com.mrl.pixiv.common.compose.layout.isWidthCompact

@Composable
actual fun currentOrientation(): Orientation {
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    return when {
        windowAdaptiveInfo.isWidthCompact -> Orientation.PORTRAIT
        else -> Orientation.LANDSCAPE
    }
}