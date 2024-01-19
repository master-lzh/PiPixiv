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

@Composable
fun StatusBarSpacer() {
    Spacer(
        Modifier
            .background(MaterialTheme.colors.background)
            .windowInsetsTopHeight(WindowInsets.statusBars) // Match the height of the status bar
            .fillMaxWidth()
    )
}