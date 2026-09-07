package com.mrl.pixiv.common.compose.layout

import androidx.compose.ui.input.pointer.PointerIcon
import java.awt.Cursor

private val resizePointerIcon = PointerIcon(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))

internal actual fun horizontalResizePointerIcon(): PointerIcon = resizePointerIcon
