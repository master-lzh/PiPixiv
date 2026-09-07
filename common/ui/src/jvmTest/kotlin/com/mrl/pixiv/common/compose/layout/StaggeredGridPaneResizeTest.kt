package com.mrl.pixiv.common.compose.layout

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class, ExperimentalSharedTransitionApi::class)
class StaggeredGridPaneResizeTest {
    @Test
    fun collectionOpenedInSourcePaneSurvivesClosingAndReopeningDetail() =
        runDesktopComposeUiTest(width = 1500, height = 900, testTimeout = 30.seconds) {
            // The ViewModel retains this state when the source entry moves between scenes.
            val gridState = LazyStaggeredGridState()
            var showCollection by mutableStateOf(false)
            var wide by mutableStateOf(false)
            setContent {
                AnimatedPane(wide) {
                    if (showCollection) CollectionGrid(gridState)
                    else Spacer(Modifier.fillMaxSize())
                }
            }
            // Match Latest -> Following -> author detail -> Collection in the left pane.
            runOnIdle { showCollection = true }
            waitForIdle()
            runOnIdle {
                assertEquals(5, visibleLaneCount(gridState))
                wide = true
            }
            waitForIdle()
            runOnIdle {
                assertEquals(6, visibleLaneCount(gridState))
                assertEquals(0, gridState.firstVisibleItemIndex)
                wide = false
            }
            waitForIdle()
            runOnIdle {
                assertEquals(5, visibleLaneCount(gridState))
                assertEquals(201, gridState.layoutInfo.totalItemsCount)
            }
        }

    @Test
    fun animatedResizeKeepsScrolledContentWhenMoreItemsArrive() =
        runDesktopComposeUiTest(width = 1500, height = 900, testTimeout = 30.seconds) {
            val gridState = LazyStaggeredGridState()
            var wide by mutableStateOf(false)
            var itemCount by mutableStateOf(80)
            setContent {
                AnimatedPane(wide) { CollectionGrid(gridState, itemCount) }
            }
            onNode(hasScrollToIndexAction()).performScrollToIndex(42)
            var anchor = -1
            runOnIdle {
                anchor = gridState.firstVisibleItemIndex
                assertTrue(anchor > 0)
                itemCount = 200
                wide = true
            }
            waitForIdle()
            runOnIdle {
                assertEquals(201, gridState.layoutInfo.totalItemsCount)
                assertTrue(gridState.layoutInfo.visibleItemsInfo.any { it.index == anchor })
                wide = false
            }
            waitForIdle()
            runOnIdle {
                assertTrue(gridState.layoutInfo.visibleItemsInfo.any { it.index == anchor })
                assertTrue(gridState.firstVisibleItemIndex > 0)
            }
        }

    @Test
    fun fractionalDensityAndPaddingKeepFoundationsColumnBreakpoints() =
        runDesktopComposeUiTest(width = 900, height = 900, testTimeout = 30.seconds) {
            val safeState = LazyStaggeredGridState()
            val referenceState = LazyStaggeredGridState()
            var gridWidth by mutableStateOf(506.dp)
            setContent {
                CompositionLocalProvider(LocalDensity provides Density(1.5f)) {
                    Column {
                        AdaptiveVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Adaptive(100.dp),
                            state = safeState,
                            modifier = Modifier.width(gridWidth).height(180.dp),
                            contentPadding = PaddingValues(horizontal = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(1.dp),
                        ) {
                            items(20) { Spacer(Modifier.height(30.dp)) }
                        }
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Adaptive(100.dp),
                            state = referenceState,
                            modifier = Modifier.width(gridWidth).height(180.dp),
                            contentPadding = PaddingValues(horizontal = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(1.dp),
                        ) {
                            items(20) { Spacer(Modifier.height(30.dp)) }
                        }
                    }
                }
            }
            runOnIdle {
                assertEquals(4, visibleLaneCount(referenceState))
                assertEquals(visibleLaneCount(referenceState), visibleLaneCount(safeState))
                gridWidth = 507.dp
            }
            waitForIdle()
            runOnIdle {
                assertEquals(5, visibleLaneCount(referenceState))
                assertEquals(visibleLaneCount(referenceState), visibleLaneCount(safeState))
            }
        }
}

/**
 * Navigation's shared entry movement can give lookahead and approach different widths. With raw
 * Adaptive grid cells this 5 -> 6 lane transition fails at LazyStaggeredGridMeasure.kt:532 because
 * a FullLine item's cached gaps still have five lanes during the six-lane lookahead measure.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AnimatedPane(wide: Boolean, content: @Composable () -> Unit) {
    SharedTransitionLayout {
        LookaheadScope {
            Box(
                Modifier.animateBounds(
                    lookaheadScope = this,
                    modifier = Modifier.width(if (wide) 1000.dp else 800.dp),
                    boundsTransform = { _, _ -> tween(400) },
                ),
            ) { content() }
        }
    }
}

@Composable
private fun CollectionGrid(state: LazyStaggeredGridState, itemCount: Int = 200) {
    AdaptiveVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(150.dp),
        state = state,
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalItemSpacing = 7.dp,
        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 10.dp),
    ) {
        item(key = "top_space", span = StaggeredGridItemSpan.FullLine) {
            Spacer(Modifier.height(40.dp))
        }
        items(count = itemCount, key = { "picture_$it" }) { index ->
            Spacer(Modifier.height((120 + index * 29 % 130).dp))
        }
    }
}

private fun visibleLaneCount(state: LazyStaggeredGridState): Int =
    state.layoutInfo.visibleItemsInfo.maxOf { it.lane } + 1
