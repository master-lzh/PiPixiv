package com.mrl.pixiv.common.compose.layout

import android.view.PointerIcon.TYPE_HORIZONTAL_DOUBLE_ARROW
import androidx.compose.ui.input.pointer.PointerIcon

private val resizePointerIcon = PointerIcon(TYPE_HORIZONTAL_DOUBLE_ARROW)

internal actual fun horizontalResizePointerIcon(): PointerIcon = resizePointerIcon
