package com.mrl.pixiv.common.compose.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Resolves the lane count from this grid's target constraints before measuring its content.
 *
 * During a Navigation 3 shared-entry transition, lookahead and approach measure at different
 * widths. Foundation 1.12 shares full-span gap arrays between those passes, so resolving Adaptive
 * cells separately in each pass can index a narrow array with a wider lane count. Subcomposition
 * shares the target lane count across both passes; cell widths still follow the animated bounds.
 */
@Composable
fun AdaptiveVerticalStaggeredGrid(
    columns: StaggeredGridCells,
    state: LazyStaggeredGridState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalItemSpacing: Dp = 0.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(0.dp),
    content: LazyStaggeredGridScope.() -> Unit,
) {
    BoxWithConstraints(modifier = modifier, propagateMinConstraints = true) {
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val laneCount = with(density) {
            val horizontalPadding = contentPadding.calculateStartPadding(layoutDirection) +
                contentPadding.calculateEndPadding(layoutDirection)
            val availableWidth = (constraints.maxWidth - horizontalPadding.roundToPx())
                .coerceAtLeast(0)
            with(columns) {
                calculateCrossAxisCellSizes(
                    availableSize = availableWidth,
                    spacing = horizontalArrangement.spacing.roundToPx(),
                ).size.coerceAtLeast(1)
            }
        }
        LazyVerticalStaggeredGrid(
            columns = remember(laneCount) { StaggeredGridCells.Fixed(laneCount) },
            state = state,
            contentPadding = contentPadding,
            verticalItemSpacing = verticalItemSpacing,
            horizontalArrangement = horizontalArrangement,
            content = content,
        )
    }
}
