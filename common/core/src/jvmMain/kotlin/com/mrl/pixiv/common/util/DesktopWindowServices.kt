@file:Suppress("DEPRECATION")
@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.mrl.pixiv.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import dev.nucleusframework.window.tao.TaoWindow

internal data class DesktopWindowServices(
    val window: TaoWindow,
    val clipboard: Clipboard,
    val clipboardManager: ClipboardManager,
)

@Volatile
private var activeWindowServices: DesktopWindowServices? = null

@Composable
fun BindDesktopWindowServices(window: TaoWindow) {
    val clipboard = LocalClipboard.current
    val clipboardManager = LocalClipboardManager.current
    DisposableEffect(window, clipboard, clipboardManager) {
        val services = DesktopWindowServices(window, clipboard, clipboardManager)
        activeWindowServices = services
        onDispose {
            if (activeWindowServices === services) activeWindowServices = null
        }
    }
}

internal fun desktopWindowServices(): DesktopWindowServices =
    checkNotNull(activeWindowServices) { "The desktop window is no longer available." }
