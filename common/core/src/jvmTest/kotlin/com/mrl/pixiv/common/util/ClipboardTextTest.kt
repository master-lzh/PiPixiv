@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.mrl.pixiv.common.util

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.asAwtTransferable
import kotlinx.coroutines.runBlocking
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ClipboardTextTest {
    @Test
    fun plainTextRoundTripPreservesUnicodeAndWhitespace() = runBlocking {
        val clipboard = MemoryClipboard()
        val text = "  Pixiv 插画 🎨\nhttps://www.pixiv.net/artworks/123\t"

        clipboard.writePlainText(text)

        assertEquals(text, clipboard.readPlainText())
    }

    @Test
    fun emptyTextIsDifferentFromAnEmptyClipboard() = runBlocking {
        val clipboard = MemoryClipboard()
        assertNull(clipboard.readPlainText())

        clipboard.writePlainText("")

        assertEquals("", clipboard.readPlainText())
    }

    @Test
    fun nonTextClipboardReturnsNullWithoutLoadingImageData() = runBlocking {
        val clipboard = MemoryClipboard()
        clipboard.setClipEntry(
            ClipEntry(object : Transferable {
                override fun getTransferDataFlavors(): Array<DataFlavor> =
                    arrayOf(DataFlavor.imageFlavor)

                override fun isDataFlavorSupported(flavor: DataFlavor): Boolean =
                    flavor == DataFlavor.imageFlavor

                override fun getTransferData(flavor: DataFlavor): Any =
                    error("Reading text must not load image data")
            })
        )

        assertNull(clipboard.readPlainText())
    }

    private class MemoryClipboard : Clipboard {
        // An AWT clipboard with no Toolkit or display-server dependency.
        private val clipboard = java.awt.datatransfer.Clipboard("test")

        override suspend fun getClipEntry(): ClipEntry? =
            clipboard.getContents(null)?.let(::ClipEntry)

        override suspend fun setClipEntry(clipEntry: ClipEntry?) {
            clipboard.setContents(clipEntry?.asAwtTransferable, null)
        }
    }
}
