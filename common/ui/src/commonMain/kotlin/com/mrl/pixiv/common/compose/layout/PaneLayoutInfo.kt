package com.mrl.pixiv.common.compose.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass

enum class PaneRole {
    Single,
    Source,
    Detail,
}

/** The space available to this page, after the scene has allocated its panes. */
@Immutable
data class PaneLayoutInfo(
    val size: DpSize,
    val sizeClass: WindowSizeClass,
    val role: PaneRole,
    val isSplit: Boolean,
)

val LocalPaneLayoutInfo = compositionLocalOf<PaneLayoutInfo?> { null }

@Composable
fun PaneHost(
    role: PaneRole,
    isSplit: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize(), propagateMinConstraints = true) {
        val fallback = if (!constraints.hasBoundedWidth || !constraints.hasBoundedHeight) {
            currentPaneLayoutInfo().size
        } else {
            DpSize.Zero
        }
        val size = DpSize(
            width = if (constraints.hasBoundedWidth) maxWidth else fallback.width,
            height = if (constraints.hasBoundedHeight) maxHeight else fallback.height,
        )
        val info = remember(size, role, isSplit) { paneLayoutInfo(size, role, isSplit) }
        CompositionLocalProvider(LocalPaneLayoutInfo provides info, content = content)
    }
}

/** Falls back to the host window for screens and previews outside a [PaneHost]. */
@Composable
fun currentPaneLayoutInfo(): PaneLayoutInfo {
    LocalPaneLayoutInfo.current?.let { return it }
    val windowSize = LocalWindowInfo.current.containerSize
    val size = with(LocalDensity.current) {
        DpSize(windowSize.width.toDp(), windowSize.height.toDp())
    }
    return remember(size) { paneLayoutInfo(size, PaneRole.Single, isSplit = false) }
}

private fun paneLayoutInfo(size: DpSize, role: PaneRole, isSplit: Boolean) = PaneLayoutInfo(
    size = size,
    sizeClass = WindowSizeClass.BREAKPOINTS_V2.computeWindowSizeClass(
        widthDp = size.width.value,
        heightDp = size.height.value,
    ),
    role = role,
    isSplit = isSplit,
)
