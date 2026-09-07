package com.mrl.pixiv.common.compose.layout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SplitPaneStateTest {
    @Test
    fun preferredRatioUsesWidthAfterTheDivider() {
        val widths = calculateSplitWidths(1224f, 24f, 360f, 420f, 0.42f)

        assertEquals(504f, widths.sourceWidth, 0.001f)
        assertEquals(696f, widths.detailWidth, 0.001f)
        assertTrue(widths.canSplit)
    }

    @Test
    fun eachPaneKeepsItsMinimumAtBothDragLimits() {
        val leftLimit = calculateSplitWidths(1024f, 24f, 360f, 420f, -1f)
        val rightLimit = calculateSplitWidths(1024f, 24f, 360f, 420f, 2f)

        assertEquals(360f, leftLimit.sourceWidth)
        assertEquals(640f, leftLimit.detailWidth)
        assertEquals(580f, rightLimit.sourceWidth)
        assertEquals(420f, rightLimit.detailWidth)
    }

    @Test
    fun exactMinimumBoundaryAllowsBothPanes() {
        val widths = calculateSplitWidths(804f, 24f, 360f, 420f, 0.8f)

        assertTrue(widths.canSplit)
        assertEquals(360f, widths.sourceWidth)
        assertEquals(420f, widths.detailWidth)
        assertFalse(calculateSplitWidths(803f, 24f, 360f, 420f, 0.8f).canSplit)
    }

    @Test
    fun narrowWindowDoesNotOverwritePreferredRatio() {
        val state = SplitPaneState()
        state.updateFraction(0.65f)
        val narrow = calculateSplitWidths(824f, 24f, 360f, 420f, state.fraction)
        val restored = calculateSplitWidths(1624f, 24f, 360f, 420f, state.fraction)

        assertEquals(380f, narrow.sourceWidth)
        assertEquals(0.65f, state.fraction)
        assertEquals(1040f, restored.sourceWidth, 0.001f)
    }

    @Test
    fun insufficientSpaceRemainsFiniteAndInsideTheContainer() {
        val widths = calculateSplitWidths(200f, 24f, 360f, 420f, 0.42f)

        assertFalse(widths.canSplit)
        assertTrue(widths.sourceWidth >= 0f)
        assertTrue(widths.detailWidth >= 0f)
        assertEquals(200f, widths.sourceWidth + widths.detailWidth + widths.dividerWidth, 0.001f)
        assertEquals(SplitWidths(0f, 0f, 10f, false), calculateSplitWidths(10f, 24f, 360f, 420f, 0.42f))
    }

    @Test
    fun invalidNumbersNeverEscapeAsInvalidMeasurements() {
        val invalidTotal = calculateSplitWidths(Float.NaN, 24f, 360f, 420f, 0.42f)
        val invalidMinima = calculateSplitWidths(1000f, -24f, Float.NaN, Float.POSITIVE_INFINITY, Float.NaN)

        assertEquals(SplitWidths(0f, 0f, 0f, false), invalidTotal)
        assertEquals(420f, invalidMinima.sourceWidth, 0.001f)
        assertEquals(580f, invalidMinima.detailWidth, 0.001f)
        assertTrue(invalidMinima.canSplit)
        assertFalse(calculateSplitWidths(0f, 0f, 0f, 0f, 0.5f).canSplit)
    }

    @Test
    fun veryLargeMinimumsCannotOverflowTheSplitCheck() {
        val widths = calculateSplitWidths(Float.MAX_VALUE, 24f, Float.MAX_VALUE, Float.MAX_VALUE, 0.5f)

        assertFalse(widths.canSplit)
        assertTrue(widths.sourceWidth.isFinite())
        assertTrue(widths.detailWidth.isFinite())
    }

    @Test
    fun resetRestoresInitialRatioAndInvalidInputIsIgnored() {
        val state = SplitPaneState(initialFraction = 0.45f)
        state.updateFraction(2f)
        assertEquals(1f, state.fraction)
        state.updateFraction(Float.NaN)
        assertEquals(1f, state.fraction)
        state.reset()
        assertEquals(0.45f, state.fraction)
        assertEquals(0.42f, SplitPaneState(Float.NaN).fraction)
    }
}
