package com.mrl.pixiv.common.compose.layout

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.util.RStrings
import com.mrl.pixiv.strings.split_pane_reset
import com.mrl.pixiv.strings.split_pane_resize_handle
import org.jetbrains.compose.resources.stringResource
import kotlin.math.ceil
import kotlin.math.roundToInt

val SplitPaneDividerWidth = 24.dp

/**
 * A physical left/right split. Only the central 48dp handle receives resizing gestures; the rest
 * of the gutter does not steal scrolling or edge gestures from either page.
 */
@Composable
fun ResizableSplitLayout(
    state: SplitPaneState,
    minSourceWidth: Dp,
    minDetailWidth: Dp,
    modifier: Modifier = Modifier,
    onDividerFocusChanged: (Boolean) -> Unit = {},
    source: @Composable () -> Unit,
    detail: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val sourceMinimum = with(density) {
            ceil(minSourceWidth.toPx()).takeIf { it.isFinite() }?.coerceAtLeast(0f) ?: 0f
        }
        val detailMinimum = with(density) {
            ceil(minDetailWidth.toPx()).takeIf { it.isFinite() }?.coerceAtLeast(0f) ?: 0f
        }
        val divider = with(density) { SplitPaneDividerWidth.roundToPx() }.toFloat()
        val availableWidth = if (constraints.hasBoundedWidth) {
            constraints.maxWidth.toFloat()
        } else {
            sourceMinimum + detailMinimum + divider
        }
        val calculateWidths = {
            calculateSplitWidths(
                totalWidth = availableWidth,
                dividerWidth = divider,
                minSourceWidth = sourceMinimum,
                minDetailWidth = detailMinimum,
                fraction = state.fraction,
            )
        }

        Layout(
            modifier = Modifier.fillMaxSize(),
            content = {
                Box(Modifier.clipToBounds().testTag("split-source")) { source() }
                Box(Modifier.clipToBounds().testTag("split-detail")) { detail() }
                SplitPaneHandle(
                    state = state,
                    widths = calculateWidths,
                    minSourceWidth = sourceMinimum,
                    minDetailWidth = detailMinimum,
                    onFocusChanged = onDividerFocusChanged,
                )
            },
        ) { measurables, layoutConstraints ->
            val width = layoutConstraints.constrainWidth(availableWidth.roundToInt())
            val height = if (layoutConstraints.hasBoundedHeight) {
                layoutConstraints.maxHeight
            } else {
                layoutConstraints.constrainHeight(48.dp.roundToPx())
            }
            // Read the fraction during measurement, avoiding recomposition of both page contents
            // for each pointer movement.
            val widths = calculateWidths()
            val dividerWidth = widths.dividerWidth.roundToInt().coerceIn(0, width)
            val sourceWidth = widths.sourceWidth.roundToInt().coerceIn(0, width - dividerWidth)
            val detailWidth = width - sourceWidth - dividerWidth
            val sourcePlaceable = measurables[0].measure(Constraints.fixed(sourceWidth, height))
            val detailPlaceable = measurables[1].measure(Constraints.fixed(detailWidth, height))
            val handleWidth = 48.dp.roundToPx().coerceAtMost(width)
            val handleHeight = 48.dp.roundToPx().coerceAtMost(height)
            val handlePlaceable = measurables[2].measure(
                Constraints.fixed(handleWidth, handleHeight),
            )
            layout(width, height) {
                sourcePlaceable.place(0, 0)
                detailPlaceable.place(sourceWidth + dividerWidth, 0)
                handlePlaceable.place(
                    x = (sourceWidth + (dividerWidth - handleWidth) / 2)
                        .coerceIn(0, width - handleWidth),
                    y = (height - handleHeight) / 2,
                )
            }
        }
    }
}

@Composable
private fun SplitPaneHandle(
    state: SplitPaneState,
    widths: () -> SplitWidths,
    minSourceWidth: Float,
    minDetailWidth: Float,
    onFocusChanged: (Boolean) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val currentOnFocusChanged by rememberUpdatedState(onFocusChanged)
    DisposableEffect(Unit) {
        onDispose { currentOnFocusChanged(false) }
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val color by animateColorAsState(
        targetValue = if (isDragging || isHovered || isFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        label = "split handle color",
    )
    val currentWidths = widths()
    val availableWidth = currentWidths.sourceWidth + currentWidths.detailWidth
    val enabled = currentWidths.canSplit && availableWidth > minSourceWidth + minDetailWidth
    val minimumFraction = if (enabled) minSourceWidth / availableWidth else 0f
    val maximumFraction = if (enabled) (availableWidth - minDetailWidth) / availableWidth else 1f
    val currentFraction = if (availableWidth > 0f) currentWidths.sourceWidth / availableWidth else 0f
    val handleDescription = stringResource(RStrings.split_pane_resize_handle)
    val resetDescription = stringResource(RStrings.split_pane_reset)
    val keyboardStep = with(LocalDensity.current) { 24.dp.toPx() }
    val resizeBy: (Float) -> Unit = { delta ->
        val measured = widths()
        val available = measured.sourceWidth + measured.detailWidth
        if (measured.canSplit && available > 0f) {
            val sourceWidth = (measured.sourceWidth + delta)
                .coerceIn(minSourceWidth, available - minDetailWidth)
            state.updateFraction(sourceWidth / available)
        }
    }

    Box(
        modifier = Modifier
            .testTag("split-divider")
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChanged(it.isFocused) }
            .onKeyEvent { event ->
                val handledKey = event.key in listOf(
                    Key.DirectionLeft, Key.DirectionRight, Key.MoveHome, Key.MoveEnd,
                    Key.Enter, Key.NumPadEnter,
                )
                if (!enabled || !handledKey) {
                    false
                } else {
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.DirectionLeft -> resizeBy(-keyboardStep)
                            Key.DirectionRight -> resizeBy(keyboardStep)
                            Key.MoveHome -> state.updateFraction(minimumFraction)
                            Key.MoveEnd -> state.updateFraction(maximumFraction)
                            Key.Enter, Key.NumPadEnter -> state.reset()
                        }
                    }
                    true
                }
            }
            .focusable(enabled = enabled, interactionSource = interactionSource)
            .hoverable(interactionSource = interactionSource, enabled = enabled)
            .pointerHoverIcon(horizontalResizePointerIcon())
            .draggable(
                state = rememberDraggableState(resizeBy),
                orientation = Orientation.Horizontal,
                enabled = enabled,
                interactionSource = interactionSource,
                onDragStarted = { focusRequester.requestFocus() },
            )
            .pointerInput(state, enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = { focusRequester.requestFocus() },
                        onDoubleTap = {
                            focusRequester.requestFocus()
                            state.reset()
                        },
                    )
                }
            }
            .semantics {
                contentDescription = handleDescription
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = currentFraction.coerceIn(minimumFraction, maximumFraction),
                    range = minimumFraction..maximumFraction,
                )
                if (enabled) {
                    setProgress { requestedFraction ->
                        if (requestedFraction.isFinite()) {
                            state.updateFraction(requestedFraction.coerceIn(minimumFraction, maximumFraction))
                            true
                        } else {
                            false
                        }
                    }
                    customActions = listOf(
                        CustomAccessibilityAction(resetDescription) {
                            state.reset()
                            true
                        },
                    )
                } else {
                    disabled()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(width = 4.dp, height = 40.dp)
                .background(color, RoundedCornerShape(50)),
        )
    }
}

internal expect fun horizontalResizePointerIcon(): PointerIcon
