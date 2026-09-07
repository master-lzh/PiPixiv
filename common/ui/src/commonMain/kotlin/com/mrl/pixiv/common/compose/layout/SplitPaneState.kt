package com.mrl.pixiv.common.compose.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

private const val DefaultSplitFraction = 0.42f

/** Stores the user's preferred ratio; temporary window constraints never overwrite it. */
@Stable
class SplitPaneState(initialFraction: Float = DefaultSplitFraction) {
    private val initialFraction = initialFraction.validFraction()

    var fraction by mutableFloatStateOf(this.initialFraction)
        private set

    fun updateFraction(fraction: Float) {
        if (fraction.isFinite()) this.fraction = fraction.coerceIn(0f, 1f)
    }

    fun reset() {
        fraction = initialFraction
    }
}

@Composable
fun rememberSplitPaneState(
    key: Any = Unit,
    initialFraction: Float = DefaultSplitFraction,
): SplitPaneState {
    val saver = remember(initialFraction) {
        Saver<SplitPaneState, Float>(
            save = { it.fraction },
            restore = { savedFraction ->
                SplitPaneState(initialFraction).apply { updateFraction(savedFraction) }
            },
        )
    }
    return rememberSaveable(key, initialFraction, saver = saver) {
        SplitPaneState(initialFraction)
    }
}

@Immutable
data class SplitWidths(
    val sourceWidth: Float,
    val detailWidth: Float,
    val dividerWidth: Float,
    val canSplit: Boolean,
)

/**
 * All dimensions use the same unit (normally pixels). The scene should only display two panes
 * when [SplitWidths.canSplit] is true. An undersized or invalid constraint still produces finite,
 * nonnegative widths, so an outgoing scene remains safe while the window is resizing.
 */
fun calculateSplitWidths(
    totalWidth: Float,
    dividerWidth: Float,
    minSourceWidth: Float,
    minDetailWidth: Float,
    fraction: Float,
): SplitWidths {
    val total = totalWidth.validDimension()
    val divider = dividerWidth.validDimension().coerceAtMost(total)
    val available = total - divider
    val sourceMinimum = minSourceWidth.validDimension()
    val detailMinimum = minDetailWidth.validDimension()
    val canSplit = available > 0f &&
        available.toDouble() >= sourceMinimum.toDouble() + detailMinimum.toDouble()
    val preferredSource = available * fraction.validFraction()
    val source = if (canSplit) {
        preferredSource.coerceIn(sourceMinimum, available - detailMinimum)
    } else {
        preferredSource
    }
    return SplitWidths(
        sourceWidth = source,
        detailWidth = (available - source).coerceAtLeast(0f),
        dividerWidth = divider,
        canSplit = canSplit,
    )
}

private fun Float.validDimension() = if (isFinite()) coerceAtLeast(0f) else 0f

private fun Float.validFraction() =
    if (isFinite()) coerceIn(0f, 1f) else DefaultSplitFraction
