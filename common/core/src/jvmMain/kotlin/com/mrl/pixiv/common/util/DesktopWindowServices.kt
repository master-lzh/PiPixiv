package com.mrl.pixiv.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import dev.nucleusframework.window.tao.TaoWindow

internal data class DesktopWindowServices(
    val window: TaoWindow,
    val clipboard: Clipboard,
)

@Volatile
private var activeWindowServices: DesktopWindowServices? = null

@Composable
fun BindDesktopWindowServices(window: TaoWindow) {
    val clipboard = LocalClipboard.current
    DisposableEffect(window, clipboard) {
        val services = DesktopWindowServices(window, clipboard)
        activeWindowServices = services
        onDispose {
            if (activeWindowServices === services) activeWindowServices = null
        }
    }
}

internal fun desktopWindowServices(): DesktopWindowServices =
    checkNotNull(activeWindowServices) { "The desktop window is no longer available." }
