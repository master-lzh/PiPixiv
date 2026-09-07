package com.mrl.pixiv.common.compose.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

@Stable
class PaneInputState {
    var activeEntryId: String? by mutableStateOf(null)
    var dividerFocused: Boolean by mutableStateOf(false)
}

val LocalPaneKeyEventsEnabled = compositionLocalOf { true }

/** Both entries can be resumed, but keyboard scrolling belongs to the focused pane. */
@Composable
fun PaneInputScope(
    entryId: String,
    state: PaneInputState,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalPaneKeyEventsEnabled provides
            (state.activeEntryId == entryId && !state.dividerFocused)
    ) {
        Box(
            Modifier.fillMaxSize()
                .onFocusChanged { if (it.hasFocus) state.activeEntryId = entryId }
                .pointerInput(entryId, state) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.changes.any { it.pressed && !it.previousPressed }) {
                                state.activeEntryId = entryId
                                state.dividerFocused = false
                            }
                        }
                    }
                }
        ) { content() }
    }
}
