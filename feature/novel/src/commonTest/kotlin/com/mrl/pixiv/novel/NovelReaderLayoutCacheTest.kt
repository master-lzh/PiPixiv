package com.mrl.pixiv.novel

import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class NovelReaderLayoutCacheTest {
    private val state = NovelState(
        paragraphs = persistentListOf("first", "second"),
    )

    @Test
    fun `font size change invalidates paragraph layouts`() {
        assertNotEquals(
            state.paragraphLayoutCacheKey(),
            state.copy(fontSize = state.fontSize + 1).paragraphLayoutCacheKey(),
        )
    }

    @Test
    fun `line spacing change invalidates paragraph layouts`() {
        assertNotEquals(
            state.paragraphLayoutCacheKey(),
            state.copy(lineSpacingSp = state.lineSpacingSp + 1).paragraphLayoutCacheKey(),
        )
    }

    @Test
    fun `reader chrome changes retain paragraph layouts`() {
        assertEquals(
            state.paragraphLayoutCacheKey(),
            state.copy(showBottomSheet = true).paragraphLayoutCacheKey(),
        )
    }

    @Test
    fun `resizing the reader invalidates paragraph layouts`() {
        assertNotEquals(
            state.paragraphLayoutCacheKey(contentWidthPx = 760),
            state.paragraphLayoutCacheKey(contentWidthPx = 420),
        )
    }

    @Test
    fun `moving to a different display density invalidates paragraph layouts`() {
        assertNotEquals(
            state.paragraphLayoutCacheKey(contentWidthPx = 760, density = 1f),
            state.paragraphLayoutCacheKey(contentWidthPx = 760, density = 2f),
        )
    }

    @Test
    fun `changing system font scale invalidates paragraph layouts`() {
        assertNotEquals(
            state.paragraphLayoutCacheKey(fontScale = 1f),
            state.paragraphLayoutCacheKey(fontScale = 1.3f),
        )
    }

    @Test
    fun `unchanged reader dimensions retain paragraph layouts`() {
        assertEquals(
            state.paragraphLayoutCacheKey(contentWidthPx = 420, density = 2f, fontScale = 1.1f),
            state.copy(showBottomSheet = true)
                .paragraphLayoutCacheKey(contentWidthPx = 420, density = 2f, fontScale = 1.1f),
        )
    }
}
