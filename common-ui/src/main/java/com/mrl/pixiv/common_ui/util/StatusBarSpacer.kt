package com.mrl.pixiv.common_ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun StatusBarSpacer(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.background
) {
    Spacer(
        modifier
            .background(color)
            .windowInsetsTopHeight(WindowInsets.statusBars) // Match the height of the status bar
            .fillMaxWidth()
    )
}