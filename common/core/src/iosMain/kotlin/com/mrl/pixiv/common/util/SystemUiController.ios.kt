package com.mrl.pixiv.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf

// Each SwiftUI scene supplies its own status bar controller to the Compose tree.
val LocalStatusBarVisibilityController = staticCompositionLocalOf<(Boolean) -> Unit> { {} }

@Composable
actual fun StatusBarVisibilityEffect(hidden: Boolean) {
    val setStatusBarHidden = LocalStatusBarVisibilityController.current
    DisposableEffect(setStatusBarHidden, hidden) {
        setStatusBarHidden(hidden)
        onDispose {
            setStatusBarHidden(false)
        }
    }
}
